package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.PropertiesConstants;
import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.constants.TimeFields;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.alarm.GoodsCenterAlarm;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.redis.DbMerchant;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.RedisPoolUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


import static cn.wr.collect.sync.constants.CommonConstants.OPERATE_INSERT;
import static cn.wr.collect.sync.constants.CommonConstants.OPERATE_UPDATE;
import static cn.wr.collect.sync.constants.CommonConstants.SOURCE_GOODS_CENTER;
import static cn.wr.collect.sync.constants.CommonConstants.SOURCE_PARTNERS;


public class GoodsCenterAlarmSplitFlatMap extends RichFlatMapFunction<PolarDbBinlogBatch, Tuple2<String, GoodsCenterAlarm>> {
    private static final long serialVersionUID = -6570379210933820635L;
    private static final Logger log = LoggerFactory.getLogger(GoodsCenterAlarmSplitFlatMap.class);
    private RedisService redisService;

    private static final String FIELD_BARCODE = "barcode";
    private static final String FIELD_TRADE_CODE = "trade_code";
    private static final String FIELD_APPROVAL_NUMBER = "approval_number";
    private static final String FIELD_MERCHANT_ID = "merchant_id";
    private static final String FIELD_SKU_NO = "sku_no";
    private static final String FIELD_DB_ID = "db_id";
    private static final String FIELD_INTERNAL_ID = "internal_id";

    // gc_config_sku 监控变更字段
    private static final String [] GC_CONFIG_SKU_COLUMNS = {"barcode"};
    // partner_goods 监控变更字段
    private static final String [] PARTNER_GOODS_COLUMNS = {"trade_code", "approval_number"};

    // 忽略字段
    private static boolean RUNNING_FLAG = true;
    private List<Integer> filterDbIdList;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        redisService = new RedisService(tool);

        String filterDbIdStr = tool.get(PropertiesConstants.FILTER_DBID);
        if (StringUtils.isNotBlank(filterDbIdStr)) {
            filterDbIdList = Arrays.stream(filterDbIdStr.split(SymbolConstants.COMMA_EN))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        }
        else {
            filterDbIdList = Collections.emptyList();
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
        RUNNING_FLAG = false;
        RedisPoolUtil.closePool();
    }

    @Override
    public void flatMap(PolarDbBinlogBatch binlog, Collector<Tuple2<String, GoodsCenterAlarm>> collector) throws Exception {
        int index = 0;
        String operate = StringUtils.lowerCase(binlog.getType());
        for (Map<String, Object> map : binlog.getData()) {
            if (StringUtils.equals(CommonConstants.OPERATE_DELETE, operate)
                    || (StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)
                    && CollectionUtils.isNotEmpty(binlog.getOld())
                    && !checkIfNeedPush(binlog.getTable(), binlog.getOld().get(index)))) {
                index ++;
                log.info("GoodsCenterAlarmSplitFlatMap not need push table:{}, binlog id:{}", binlog.getTable(), binlog.getId());
                continue;
            }

            // 处理日期脏数据
            for (String f : TimeFields.fields) {
                if (map.containsKey(f) && Objects.nonNull(map.get(f))
                        && StringUtils.equals(TimeFields.ZERO_TIME, map.get(f).toString())) {
                    map.put(f, null);
                }
            }

            Map<String, Object> oldMap = null;
            if (CollectionUtils.isNotEmpty(binlog.getOld())) {
                oldMap = binlog.getOld().get(index);
            }
            index ++;

            // partner_goods / partners 表不同逻辑拆分数据
            this.splitBinlog(operate, binlog.getTable(), map, oldMap, collector);
        }
    }

    /**
     * 处理binlog
     * @param operate
     * @param table
     * @param data
     * @param collector
     */
    private void splitBinlog(String operate, String table, Map<String, Object> data, Map<String, Object> oldMap,
                             Collector<Tuple2<String, GoodsCenterAlarm>> collector) {
        Table.BaseDataTable t = Table.BaseDataTable.getEnum(table);
        if (Objects.isNull(t)) {
            return;
        }
        switch (t) {
            case gc_config_sku:
                switch (operate) {
                    case OPERATE_INSERT:
                        Long insertMerId = Long.valueOf(String.valueOf(data.get(FIELD_MERCHANT_ID)));
                        String insertSkuNo = String.valueOf(data.get(FIELD_SKU_NO));
                        GoodsCenterAlarm insert = new GoodsCenterAlarm(SOURCE_GOODS_CENTER, operate, table, insertMerId,
                                insertSkuNo.substring(String.valueOf(insertMerId).length() + 1));

                        collector.collect(Tuple2.of(OPERATE_INSERT + table + insert.getMerchantId(), insert));
                        break;

                    case OPERATE_UPDATE:
                        // 更新
                        Long updateMerId = Long.valueOf(String.valueOf(data.get(FIELD_MERCHANT_ID)));
                        String updateSkuNo = String.valueOf(data.get(FIELD_SKU_NO));
                        GoodsCenterAlarm update = new GoodsCenterAlarm(SOURCE_GOODS_CENTER, operate, table, updateMerId,
                                updateSkuNo.substring(String.valueOf(updateMerId).length() + 1));
                        if (oldMap.containsKey(FIELD_BARCODE)) {
                            GoodsCenterAlarm.Field field = new GoodsCenterAlarm.Field(FIELD_BARCODE,
                                    String.valueOf(oldMap.get(FIELD_BARCODE)),
                                    String.valueOf(data.get(FIELD_BARCODE)));
                            update.setFieldList(Collections.singletonList(field));
                        }

                        collector.collect(Tuple2.of(OPERATE_UPDATE + table + update.getMerchantId(), update));
                        break;
                }
                break;

            case partner_goods:
                switch (operate) {
                    case OPERATE_INSERT:
                        Integer insertDbId = Integer.parseInt(String.valueOf(data.get(FIELD_DB_ID)));
                        String insertInternalId = String.valueOf(data.get(FIELD_INTERNAL_ID));
                        List<DbMerchant> insertDbMerchantList = redisService.queryDbMerchant(insertDbId);
                        insertDbMerchantList.forEach(dbMerchant -> {
                            GoodsCenterAlarm insert = new GoodsCenterAlarm(SOURCE_PARTNERS, operate, table,
                                    Long.valueOf(dbMerchant.getMerchantId()), insertInternalId);

                            collector.collect(Tuple2.of(OPERATE_INSERT + table + insert.getMerchantId(), insert));
                        });
                        break;

                    case OPERATE_UPDATE:
                        // 更新
                        Integer updateDbId = Integer.parseInt(String.valueOf(data.get(FIELD_DB_ID)));
                        String updateInternalId = String.valueOf(data.get(FIELD_INTERNAL_ID));
                        List<DbMerchant> updateDbMerchantList = redisService.queryDbMerchant(updateDbId);
                        updateDbMerchantList.forEach(dbMerchant -> {
                            GoodsCenterAlarm update = new GoodsCenterAlarm(SOURCE_PARTNERS, operate, table,
                                    Long.valueOf(dbMerchant.getMerchantId()), updateInternalId);
                            List<GoodsCenterAlarm.Field> list = new ArrayList<>();
                            if (oldMap.containsKey(FIELD_TRADE_CODE)) {
                                GoodsCenterAlarm.Field fieldTradeCode = new GoodsCenterAlarm.Field(FIELD_TRADE_CODE,
                                        String.valueOf(oldMap.get(FIELD_TRADE_CODE)),
                                        String.valueOf(data.get(FIELD_TRADE_CODE)));
                                list.add(fieldTradeCode);
                            }
                            if (oldMap.containsKey(FIELD_APPROVAL_NUMBER)) {
                                GoodsCenterAlarm.Field fieldApproNumber = new GoodsCenterAlarm.Field(FIELD_APPROVAL_NUMBER,
                                        String.valueOf(oldMap.get(FIELD_APPROVAL_NUMBER)),
                                        String.valueOf(data.get(FIELD_APPROVAL_NUMBER)));
                                list.add(fieldApproNumber);
                            }
                            update.setFieldList(list);
                            collector.collect(Tuple2.of(OPERATE_UPDATE + table + update.getMerchantId(), update));
                        });
                        break;
                }
                break;

            default:
                break;
        }
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
            case gc_config_sku:
                return Objects.nonNull(old) && old.keySet().stream().anyMatch(key -> Arrays.asList(GC_CONFIG_SKU_COLUMNS).contains(key));
            case partner_goods:
                return Objects.nonNull(old) && old.keySet().stream().anyMatch(key -> Arrays.asList(PARTNER_GOODS_COLUMNS).contains(key));
            default:
                return false;
        }
    }
}
