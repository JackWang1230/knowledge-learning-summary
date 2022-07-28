package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.FieldRefreshEnum;
import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.dao.time.QueryPartnerStoresAllDao;
import cn.wr.collect.sync.dao.time.QueryStandardGoodsDao;
import cn.wr.collect.sync.model.MutationResult;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.gc.PartnerStoresAll;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.ReflectUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.RedisConstant.REDIS_REFRESH_DATA_PREFIX;
import static cn.wr.collect.sync.constants.RedisConstant.REDIS_TABLE_PREFIX;

@Deprecated
public class CacheChangeSink extends RichSinkFunction<MutationResult> {
    private static final long serialVersionUID = -5967539381426197980L;
    private static final Logger log = LoggerFactory.getLogger(CacheChangeSink.class);
    private QueryStandardGoodsDao queryStandardGoodsDao;
    private QueryPartnerStoresAllDao queryPartnerStoresAllDao;
    private RedisService redisService;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool parameterTool = (ParameterTool) getRuntimeContext()
                .getExecutionConfig().getGlobalJobParameters();
        queryStandardGoodsDao = new QueryStandardGoodsDao(parameterTool);
        queryPartnerStoresAllDao = new QueryPartnerStoresAllDao(parameterTool);
        redisService = new RedisService(parameterTool);
    }

    @Override
    public void invoke(MutationResult value, Context context) {
        // 校验基础数据指定字段是否发生变更，如果发生变更，则保存变更数据id存入redis
        this.cacheChangeRecord(value);
    }

    /**
     * 保存发生变更的数据
     * @param result
     */
    private void cacheChangeRecord(MutationResult result) {
        if (Objects.isNull(result) || !ReflectUtil.checkFieldsIsDiff(result)) {
            return;
        }
        List<String> list = new ArrayList<>();
        FieldRefreshEnum refresh = FieldRefreshEnum.getEnum(result.getTableName());
        if (null != refresh && null != result.getNewRecord()) {
            int size;
            switch (refresh) {
                case gc_goods_manual:
                case base_goods:
                case gc_base_spu_img:
                case gc_base_nootc:
                    String oa = ReflectUtil.getFieldValueByFieldName("approvalNumber", result.getOldRecord());
                    if (StringUtils.isNotBlank(oa)) {
                        list.add(JSON.toJSONString(PgConcatParams.builder().approvalNumber(oa).build()));
                    }

                    String na = ReflectUtil.getFieldValueByFieldName("approvalNumber", result.getNewRecord());
                    if (StringUtils.isNotBlank(na)) {
                        list.add(JSON.toJSONString(PgConcatParams.builder().approvalNumber(na).build()));
                    }
                    break;

                case gc_goods_dosage:
                case gc_standard_goods_syncrds:
                case merchant_goods_category_mapping:
                case gc_goods_overweight:
                    String ot = ReflectUtil.getFieldValueByFieldName("tradeCode", result.getOldRecord());
                    if (StringUtils.isNotBlank(ot)) {
                        list.add(JSON.toJSONString(PgConcatParams.builder().tradeCode(ot).build()));
                    }

                    String nt = ReflectUtil.getFieldValueByFieldName("tradeCode", result.getNewRecord());
                    if (StringUtils.isNotBlank(nt)) {
                        list.add(JSON.toJSONString(PgConcatParams.builder().tradeCode(nt).build()));
                    }
                    break;

                case partner_goods_img:
                case partner_goods_info:
                case gc_partner_goods_gift:
                    String dbId = ReflectUtil.getFieldValueByFieldName("dbId", result.getNewRecord());
                    String internalId = ReflectUtil.getFieldValueByFieldName("internalId", result.getNewRecord());
                    if (StringUtils.isNotBlank(dbId) || StringUtils.isNotBlank(internalId)) {
                        list.add(JSON.toJSONString(PgConcatParams.builder().dbId(dbId).internalId(internalId).build()));
                    }

                    String oldDbId = ReflectUtil.getFieldValueByFieldName("dbId", result.getOldRecord());
                    String oldInternalId = ReflectUtil.getFieldValueByFieldName("internalId", result.getOldRecord());
                    if (StringUtils.isNotBlank(oldDbId) && StringUtils.isNotBlank(oldInternalId)) {
                        list.add(JSON.toJSONString(PgConcatParams.builder().dbId(oldDbId).internalId(oldInternalId).build()));
                    }
                    break;

                case gc_goods_spu_attr_syncrds:
                    String spuId = ReflectUtil.getFieldValueByFieldName("spuId", result.getNewRecord());
                    if (StringUtils.isNotBlank(spuId)) {
                        List<String> tradeCodeList = queryStandardGoodsDao.queryTradeCodeBySpuId(Long.valueOf(spuId));
                        if (CollectionUtils.isNotEmpty(tradeCodeList)) {
                            list.addAll(tradeCodeList.stream()
                                    .filter(StringUtils::isNotBlank)
                                    .map(e -> JSON.toJSONString(PgConcatParams.builder().tradeCode(e).build()))
                                    .collect(Collectors.toList()));
                        }
                    }
                    break;

                case gc_goods_attr_info_syncrds:
                    String attrId = ReflectUtil.getFieldValueByFieldName("id", result.getNewRecord());
                    if (StringUtils.isNotBlank(attrId)) {
                        List<String> tradeCodeList = queryStandardGoodsDao.queryTradeCodeByAttrId(Long.valueOf(attrId));
                        if (CollectionUtils.isNotEmpty(tradeCodeList)) {
                            list.addAll(tradeCodeList.stream()
                                    .filter(StringUtils::isNotBlank)
                                    .map(e -> JSON.toJSONString(PgConcatParams.builder().tradeCode(e).build()))
                                    .collect(Collectors.toList()));
                        }
                    }
                    break;

                case pgc_store_info_increment:
                    String merchantId = ReflectUtil.getFieldValueByFieldName("organizationId", result.getNewRecord());
                    String storeId = ReflectUtil.getFieldValueByFieldName("storeId", result.getNewRecord());
                    if (StringUtils.isBlank(merchantId) || StringUtils.isBlank(storeId)) return;
                    size = queryPartnerStoresAllDao.updateByMerchantIdAndStoreId(Integer.valueOf(merchantId), Integer.valueOf(storeId));
                    log.info("### pgc_store_info_increment change update partnerStoresAll,size:{}", size);
                    break;

                case organize_base:
                    String type = ReflectUtil.getFieldValueByFieldName("organizationType", result.getNewRecord());
                    if (StringUtils.equals("5", type)) {
                        // type=5时 organizationId->store_id,rootId->merchant_id
                        String obMerchantId = ReflectUtil.getFieldValueByFieldName("rootId", result.getNewRecord());
                        String obStoreId = ReflectUtil.getFieldValueByFieldName("organizationId", result.getNewRecord());
                        if (StringUtils.isBlank(obMerchantId) || StringUtils.isBlank(obStoreId)) return;
                        size = queryPartnerStoresAllDao.updateByMerchantIdAndStoreId(Integer.valueOf(obMerchantId), Integer.valueOf(obStoreId));
                        log.info("### organize_base change update partnerStoresAll, merchantId:{}, storeId:{}, size:{}",
                                obMerchantId, obStoreId, size);
                    }
                    break;

                case platform_goods:
                    if (StringUtils.equals(CommonConstants.OPERATE_DELETE, result.getMutationType())) {
                        String pgStoreId = ReflectUtil.getFieldValueByFieldName("storeId", result.getNewRecord());
                        String pgMerchantId = ReflectUtil.getFieldValueByFieldName("merchantId", result.getNewRecord());
                        if (StringUtils.isBlank(pgMerchantId) || StringUtils.isBlank(pgStoreId)) return;
                        PartnerStoresAll partnerStoresAll = queryPartnerStoresAllDao.queryByMerchantIdAndStoreId(Integer.valueOf(pgMerchantId), Integer.valueOf(pgStoreId));
                        if (Objects.nonNull(partnerStoresAll)) {
                            list.add(JSON.toJSONString(PgConcatParams.builder()
                                    .dbId(String.valueOf(partnerStoresAll.getDbId()))
                                    .internalId(ReflectUtil.getFieldValueByFieldName("goodsInternalId", result.getNewRecord()))
                                    .merchantId(pgMerchantId)
                                    .storeId(pgStoreId)
                                    .build()));
                        }
                    }
                    break;

                case gc_sku_extend:
                    // 只处理助记码类型数据
                    String title = ReflectUtil.getFieldValueByFieldName("title", result.getNewRecord());
                    if (StringUtils.equals(CommonConstants.SPELL_WORD, title)) {
                        String skuNo = ReflectUtil.getFieldValueByFieldName("skuNo", result.getNewRecord());
                        this.convertGcSkuExtend(list, skuNo);
                    }
                    String oldTitle = ReflectUtil.getFieldValueByFieldName("title", result.getOldRecord());
                    if (StringUtils.equals(CommonConstants.SPELL_WORD, oldTitle)) {
                        String oldSkuNo = ReflectUtil.getFieldValueByFieldName("skuNo", result.getOldRecord());
                        this.convertGcSkuExtend(list, oldSkuNo);
                    }
                    break;
            }
        }
        log.info("### BasicSyncSink change json:{} binlog:{}", JSON.toJSONString(list), JSON.toJSONString(result));
        if (CollectionUtils.isNotEmpty(list)) {
            redisService.addSet(REDIS_TABLE_PREFIX + REDIS_REFRESH_DATA_PREFIX + Table.BasicTimeSyncTable.refresh_partner_goods_v10.name(), list);
        }
    }

    /**
     * 参数转换
     * @param list
     * @param skuNo
     */
    private void convertGcSkuExtend(List<String> list, String skuNo) {
        if (StringUtils.isBlank(skuNo) || skuNo.indexOf(SymbolConstants.HOR_LINE) <= 0) {
            return;
        }
        String gseMerchantId = skuNo.substring(0, skuNo.indexOf(SymbolConstants.HOR_LINE));
        String gseInternalId = skuNo.substring(skuNo.indexOf(SymbolConstants.HOR_LINE) + 1);
        if (StringUtils.isBlank(gseMerchantId) || StringUtils.isBlank(gseInternalId)) {
            return;
        }
        Integer gseDbId = queryPartnerStoresAllDao.queryDbIdByMerchantId(Integer.valueOf(gseMerchantId));
        if (Objects.isNull(gseDbId)) {
            return;
        }
        list.add(JSON.toJSONString(PgConcatParams.builder()
                .dbId(String.valueOf(gseDbId))
                .internalId(gseInternalId)
                .build()));
    }

}
