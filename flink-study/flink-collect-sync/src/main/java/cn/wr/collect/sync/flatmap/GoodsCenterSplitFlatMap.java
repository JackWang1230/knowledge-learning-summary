package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.PropertiesConstants;
import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.constants.TimeFields;
import cn.wr.collect.sync.dao.partner.PartnerGoodsDao;
import cn.wr.collect.sync.function.Compute;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.goodscenter.GoodsCenterDTO;
import cn.wr.collect.sync.model.mbs.NewPartnerDTO;
import cn.wr.collect.sync.model.mbs.PublishReqDTO;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.model.partner.Partners;
import cn.wr.collect.sync.model.redis.DbMerchant;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.ConvertUtils;
import cn.wr.collect.sync.utils.HttpUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.CommonConstants.FLAG_TRUE;
import static cn.wr.collect.sync.constants.CommonConstants.MBS2_URL;
import static cn.wr.collect.sync.constants.PropertiesConstants.*;
import static cn.wr.collect.sync.constants.RedisConstant.*;


public class GoodsCenterSplitFlatMap extends RichFlatMapFunction<PolarDbBinlogBatch, GoodsCenterDTO> {
    private static final long serialVersionUID = -6570379210933820635L;
    private static final Logger log = LoggerFactory.getLogger(GoodsCenterSplitFlatMap.class);
    private RedisService redisService;
    private PartnerGoodsDao partnerGoodsDAO;
    // 忽略字段
    private static final String [] IGNORE_COLUMNS = {"goods_create_time", "goods_update_time", "created_at",
            "updated_at", "gmtcreated", "gmtupdated"};

    // 忽略字段
    private static final String [] NEED_COLUMNS = {"dbname", "full_name", "organizationId"};
    private static final int PAGE_SIZE = 1000;
    private static boolean RUNNING_FLAG = true;

    private static boolean PUSH_FLAG = false;
    private static String MBS_TOPIC = null;
    private static String MBS_TAG = null;
    private static String MBS_ADDR = null;
    private List<Integer> filterDbIdList;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        redisService = new RedisService(tool);
        partnerGoodsDAO = new PartnerGoodsDao(tool);

        String filterDbIdStr = tool.get(PropertiesConstants.FILTER_DBID);
        if (StringUtils.isNotBlank(filterDbIdStr)) {
            filterDbIdList = Arrays.stream(filterDbIdStr.split(SymbolConstants.COMMA_EN))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        }
        else {
            filterDbIdList = Collections.emptyList();
        }

        PUSH_FLAG = tool.getBoolean(MBS_FIRST_PUSH_GOODS);
        MBS_ADDR = tool.get(MBS2_SERVICE_ADDR);
        MBS_TOPIC = tool.get(MBS_FIRST_PUSH_GOODS_TOPIC);
        MBS_TAG = tool.get(MBS_FIRST_PUSH_GOODS_TAG);
    }

    @Override
    public void close() throws Exception {
        super.close();
        RUNNING_FLAG = false;
    }

    @Override
    public void flatMap(PolarDbBinlogBatch binlog, Collector<GoodsCenterDTO> collector) {
        int i = 0;
        String operate = StringUtils.lowerCase(binlog.getType());
        for (Map<String, Object> map : binlog.getData()) {
            if (StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)
                    && CollectionUtils.isNotEmpty(binlog.getOld())
                    && !checkIfNeedPush(binlog.getTable(), binlog.getOld().get(i))) {
                i ++;
                log.info("GoodsCenterSplitFlatMap not need push table:{}, binlog id:{}", binlog.getTable(), binlog.getId());
                continue;
            }
            i ++;
            // 处理日期脏数据
            for (String f : TimeFields.fields) {
                if (map.containsKey(f) && Objects.nonNull(map.get(f))
                        && StringUtils.equals(TimeFields.ZERO_TIME, map.get(f).toString())) {
                    map.put(f, null);
                }
            }

            // partner_goods / partners 表不同逻辑拆分数据
            this.splitBinlog(operate, binlog.getTable(), map, collector);
        }
    }

    /**
     * 处理binlog
     * @param operate
     * @param table
     * @param data
     * @param collector
     */
    private void splitBinlog(String operate, String table, Map<String, Object> data, Collector<GoodsCenterDTO> collector) {
        Table.BaseDataTable t = Table.BaseDataTable.getEnum(table);
        if (Objects.isNull(t)) {
            return;
        }
        switch (t) {
            case partners:
                try {
                    Thread.sleep(5 * 1000L);
                }
                catch (Exception e) {
                    log.error("GoodsCenterSplitFlatMap Exception: {}", e);
                }

                Partners partners = JSON.parseObject(JSON.toJSONString(data), Partners.class);
                Integer dbId = Compute.dbId(partners);
                Integer merchantId = Compute.merchantId(partners);
                String merchantName = Compute.fullName(partners);
                if (Objects.isNull(dbId) || Objects.isNull(merchantId) || StringUtils.isBlank(merchantName)) {
                    return;
                }
                if (CollectionUtils.isNotEmpty(filterDbIdList) && filterDbIdList.contains(dbId)) {
                    return;
                }
                Map<String, Object> params = new HashMap<>();
                params.put("id", 0L);
                params.put("dbId", Compute.dbId(partners));
                int cnt = 0;
                while (RUNNING_FLAG) {
                    List<PartnerGoods> goodsList = partnerGoodsDAO.findLimit(0L, PAGE_SIZE, params, null);

                    if (CollectionUtils.isNotEmpty(goodsList)) {
                        goodsList.forEach(goods -> this.collect(Compute.operate(operate), goods, merchantId, merchantName, collector));
                    }
                    cnt += goodsList.size();
                    if (CollectionUtils.isEmpty(goodsList) || goodsList.size() != PAGE_SIZE) {
                        break;
                    }
                    params.put("id", goodsList.get(PAGE_SIZE - 1).getId());
                }

                // 推送mbs消息
                if (PUSH_FLAG) {
                    this.sendMbs(Long.valueOf(merchantId), cnt);
                }
                break;

            case partner_goods:
                PartnerGoods partnerGoods = JSON.parseObject(JSON.toJSONString(data), PartnerGoods.class);
                if (Objects.isNull(partnerGoods) || Objects.isNull(partnerGoods.getDbId())) {
                    log.info("GoodsCenterSplitFlatMap partner_goods not valid, json:{}", JSON.toJSONString(data));
                    return;
                }
                if (CollectionUtils.isNotEmpty(filterDbIdList) && filterDbIdList.contains(partnerGoods.getDbId())) {
                    return;
                }

                log.info("推送到商品中心的数据:{}", partnerGoods);

                log.info("这边的修改类型：{}", operate);

                this.collect(Compute.operate(operate), partnerGoods, collector);
                break;


            default:
                break;
        }
    }

    /**
     * 下发下个算子
     * @param type
     * @param partnerGoods
     * @param collector
     */
    private void collect(String type, PartnerGoods partnerGoods, Collector<GoodsCenterDTO> collector) {
        List<DbMerchant> dbMerchantList = redisService.queryDbMerchant(partnerGoods.getDbId());
        if (CollectionUtils.isEmpty(dbMerchantList)) {
            log.info("GoodsCenterSplitFlatMap need not push, type:{}, id:{}", type, partnerGoods.getId());
            return;
        }
        dbMerchantList.stream().filter(Objects::nonNull).forEach(info ->
                collector.collect(new GoodsCenterDTO().convert(partnerGoods, info, type)));
    }


    /**
     * 下发下个算子
     * @param type
     * @param partnerGoods
     * @param merchantId
     * @param merchantName
     * @param collector
     */
    private void collect(String type, PartnerGoods partnerGoods, Integer merchantId, String merchantName,
                         Collector<GoodsCenterDTO> collector) {
        collector.collect(new GoodsCenterDTO().convert(partnerGoods, merchantId, merchantName, type));
    }

    /**
     * 根据变更数据校验是否需要推送数据
     * @param old
     * @return
     */
    private boolean checkIfNeedPush(String table, Map<String, Object> old) {
        Table.BaseDataTable t = Table.BaseDataTable.getEnum(table);
        if (Objects.isNull(t)) {
            return false;
        }
        switch (t) {
            case partners:
                return Objects.nonNull(old) && old.keySet().stream().anyMatch(key -> Arrays.asList(NEED_COLUMNS).contains(key));
            case partner_goods:
                return Objects.nonNull(old) && old.keySet().stream().anyMatch(key -> !Arrays.asList(IGNORE_COLUMNS).contains(key));
            default:
                return false;
        }
    }

    /**
     * 等待Redis数据同步完成
     */
    @Deprecated
    private void waitSyncComplete() {
        String key = FLAG_TRUE;
        while (StringUtils.equals(FLAG_TRUE, key)) {
            key = redisService.get(SQOOP_REDIS_PREFIX + REDIS_TABLE_SEPARATOR + PGC_STORE_INFO_SHORT_REFRESH_KEY);
            if (StringUtils.equals(FLAG_TRUE, key)) {
                log.info("GoodsCenterSplitFlatMap waitSyncComplete waiting");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    log.error("GoodsCenterSplitFlatMap waitSyncComplete InterruptedException:{}", e);
                }
            } else {
//                LOGGER.info("GoodsCenterSplitFlatMap waitSyncComplete complete");
                break;
            }
        }
    }

    /**
     * 连锁首次新增发送mbs消息通知下游
     * @param merchantId
     * @param totalCnt
     */
    private void sendMbs(Long merchantId, Integer totalCnt) {
        if (Objects.isNull(merchantId) || merchantId.equals(0L) || Objects.isNull(totalCnt)) {
            log.info("GoodsCenterSplitFlatMap sendMbs params is not valid: merchantId: {}, totalCnt: {}", merchantId, totalCnt);
            return;
        }
        PublishReqDTO publishReqDTO = new PublishReqDTO();
        try {
            // 参数赋值
            publishReqDTO.setTopic(MBS_TOPIC);
            publishReqDTO.setTag(MBS_TAG);
            publishReqDTO.setMessage(new NewPartnerDTO(merchantId, totalCnt));
            publishReqDTO.setMsgKey(UUID.randomUUID().toString());
            publishReqDTO.setReqNo(UUID.randomUUID().toString());

            // 请求地址
            String url = MBS_ADDR + MBS2_URL;

            // 发送mbs
            long start = System.currentTimeMillis();
            log.info("GoodsCenterSplitFlatMap sendMbs reqDTO:{}", JSON.toJSONString(publishReqDTO));
            String result = HttpUtils.doPost(url, ConvertUtils.objectToMap(publishReqDTO));
            long end = System.currentTimeMillis();
            log.info("GoodsCenterSplitFlatMap sendMbs reqDTO:{}, result:{}, time:{}",
                    JSON.toJSONString(publishReqDTO), result, (end - start));
        }
        catch (Exception e) {
            log.error("GoodsCenterSplitFlatMap sendMbs reqDTO:{} Exception:{}",
                    JSON.toJSONString(publishReqDTO), e);
        }
    }
}
