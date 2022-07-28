package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.*;
import cn.wr.collect.sync.dao.time.QueryPartnerStoresAllDao;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.gc.OrganizeBase;
import cn.wr.collect.sync.model.gc.PartnerStoresAll;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.DateUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.PropertiesConstants.SINK_CACHE_PRODUCT_PROGRESS;
import static cn.wr.collect.sync.constants.PropertiesConstants.SINK_CACHE_STATE_PROGRESS;
import static cn.wr.collect.sync.utils.DateUtils.YYYY_MM_DD_HH_MM_SS_SSS;

public class SyncProgressSink extends RichSinkFunction<BasicModel<Model>> {
    private static final long serialVersionUID = 6342457884235471690L;
    private static final Logger log = LoggerFactory.getLogger(SyncProgressSink.class);

    private static final String BEGIN_TIME = "beginTime";
    private static final String SYNC_CNT = "syncCnt";
    private static final String TOTAL_CNT = "totalCnt";
    private RedisService redisService;
    private List<Integer> filterDbIdList;
    private QueryPartnerStoresAllDao storesAllDao;
    private boolean cacheProductProgress = false;
    private boolean cacheStateProgress = false;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool parameterTool = (ParameterTool) getRuntimeContext()
                .getExecutionConfig().getGlobalJobParameters();

        String filterDbIdStr = parameterTool.get(PropertiesConstants.FILTER_DBID);
        if (StringUtils.isNotBlank(filterDbIdStr)) {
            filterDbIdList = Arrays.stream(filterDbIdStr.split(SymbolConstants.COMMA_EN))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        }
        else {
            filterDbIdList = Collections.emptyList();
        }

        cacheProductProgress = parameterTool.getBoolean(SINK_CACHE_PRODUCT_PROGRESS);
        cacheStateProgress = parameterTool.getBoolean(SINK_CACHE_STATE_PROGRESS);

        redisService = new RedisService(parameterTool);

        storesAllDao = new QueryPartnerStoresAllDao(parameterTool);
    }

    @Override
    public void invoke(BasicModel<Model> basic, Context context) {
        String operate = basic.getOperate();
        Model data = basic.getData();
        if (data instanceof PartnerStoresAll) {
            if (!cacheProductProgress) {
                return;
            }
            log.info("SyncProgressSink gc_partner_stores_all json:{}", JSON.toJSONString(basic));
            switch (operate) {
                case CommonConstants.OPERATE_INSERT:
                case CommonConstants.OPERATE_UPDATE:
                    PartnerStoresAll storesAll = (PartnerStoresAll) basic.getData();
                    if (filterDbIdList.contains(storesAll.getDbId())) {
                        return;
                    }
                    String key = String.format(RedisConstant.REDIS_KEY_SYNC_PROGRESS_PRODUCT, storesAll.getStoreId());
                    this.cacheRedis(key);
                    break;
                default:
                    break;
            }
        }
        else if (data instanceof OrganizeBase) {
            if (!cacheStateProgress) {
                return;
            }
            log.info("SyncProgressSink organize_base json:{}", JSON.toJSONString(basic));
            boolean flag = false;
            OrganizeBase ob = (OrganizeBase) data;
            switch (operate) {
                case CommonConstants.OPERATE_INSERT:
                case CommonConstants.OPERATE_DELETE:
                    if (CommonConstants.IS_O2O.equals(ob.getIsO2O())) {
                        flag = true;
                    }
                    break;
                case CommonConstants.OPERATE_UPDATE:
                    if (CollectionUtils.isNotEmpty(basic.getModFieldList())
                            && basic.getModFieldList().contains(CommonConstants.MOD_FIELD_IS_O2O)) {
                        flag = true;
                    }
                default:
                    break;
            }

            if (!flag) {
                return;
            }

            Integer dbId = storesAllDao.queryDbIdByStoreId(ob.getOrganizationId().intValue());
            if (Objects.isNull(dbId) || filterDbIdList.contains(dbId)) {
                return;
            }

            String key = String.format(RedisConstant.REDIS_KEY_SYNC_PROGRESS_STATE, ob.getOrganizationId());

            // 缓存redis
            this.cacheRedis(key);
        }
    }

    /**
     * 同步进度缓存redis
     * @param key
     */
    private void cacheRedis(String key) {
        Map<String, String> val = new HashMap<>();
        val.put(BEGIN_TIME, DateUtils.format(LocalDateTime.now(), YYYY_MM_DD_HH_MM_SS_SSS));
        val.put(SYNC_CNT, "0");
        val.put(TOTAL_CNT, "0");
        redisService.addHash(key, val);
        redisService.expire(key, 60 * 60 * 24 * 3);
        log.info("SyncProgressSink key:{}, val:{}", key, val);
    }

}
