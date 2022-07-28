package cn.wr.collect.sync.service;

import cn.wr.collect.sync.constants.*;
import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.dao.price.PriceListDAO;
import cn.wr.collect.sync.dao.price.PriceListDetailsDAO;
import cn.wr.collect.sync.dao.price.PriceStoreDAO;
import cn.wr.collect.sync.dao.time.*;
import cn.wr.collect.sync.function.Compute;
import cn.wr.collect.sync.hbase.HBaseService;
import cn.wr.collect.sync.model.*;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.gc.*;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.model.partner.PartnerGoodsInfo;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import cn.wr.collect.sync.model.price.PriceList;
import cn.wr.collect.sync.model.price.PriceListDetails;
import cn.wr.collect.sync.model.price.PriceStore;
import cn.wr.collect.sync.model.stock.StockGoods;
import cn.wr.collect.sync.model.stock.StockMerchant;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.HBaseUtils;
import cn.wr.collect.sync.utils.QueryUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.SqlConstants.SQL_PARTNER_GOODS;
import static cn.wr.collect.sync.constants.SqlConstants.SQL_STORE_GOODS;

public class OprtServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(MultiFieldService.class);
    private final ParameterTool tool;
    private final SingleFieldService singleFieldService;
    private final MultiFieldService multiFieldService;
    private final RedisService redisService;
    private final HBaseService hBaseService;
    private final List<Integer> filterDbIdList;

    public OprtServiceImpl(ParameterTool tool) {
        this.tool = tool;
        this.singleFieldService = new SingleFieldService();
        this.multiFieldService = new MultiFieldService(tool);
        this.redisService = new RedisService(tool);
        this.hBaseService = new HBaseService(HBaseUtils.getConnection(tool));
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

    /**
     * 操作
     * @param basic
     * @param collector
     * @param modCondition
     */
    public void operate(BasicModel<Model> basic, Collector<BasicModel<ElasticO2O>> collector, Integer modCondition) {
        // 获取表对应的实体类
        Table.BaseDataTable table = Table.BaseDataTable.getEnum(basic.getTableName());
        if (Objects.isNull(table)) {
            return;
        }

        // 获取数据
        Model data = basic.getData();

        // 转换partner_goods表查询字段
        List<PgConcatParams> paramsList = this.transferParams(table, basic.getOperate(), data);
        if (CollectionUtils.isEmpty(paramsList)) {
            return;
        }

        // 查询组装数据 下发下个算子
        this.queryGoodsThenCollect(basic.getOperate(), table, data, basic.getModFieldList(), collector,
                paramsList, modCondition);

    }

    /**
     * 查询条件参数赋值
     * @param table
     * @param data
     * @return
     */
    private List<PgConcatParams> transferParams(Table.BaseDataTable table, String operate, Model data) {

        String approvalNumber = null;
        String tradeCode = null;
        Integer dbId = null;
        String internalId = null;
        Long merchantId = null;
        Long storeId = null;
        Long spuId = null;
        String groupId = null;
        switch (table) {
            case merchant_goods_category_mapping: // 表已废弃
                tradeCode = ((MerchantGoodsCategoryMapping) data).getTradeCode();
                break;

            case gc_goods_overweight:
                tradeCode = ((GoodsOverweight) data).getTradeCode();
                break;

            case organize_base:
                // (新增 || 删除 || 更删) && 非O2O   不用更新数据，直接返回
                /*if ((StringUtils.equals(OPERATE_INSERT, operate) || StringUtils.equals(OPERATE_DELETE, operate)
                        || StringUtils.equals(OPERATE_UPDATE_DELETE, operate))
                        && !IS_O2O.equals(((OrganizeBase) data).getIsO2O())) {
                    return Collections.emptyList();
                }*/
                merchantId = ((OrganizeBase) data).getRootId();
                storeId = ((OrganizeBase) data).getOrganizationId();
                if (Objects.isNull(merchantId) || Objects.isNull(storeId)) {
                    return Collections.emptyList();
                }
                PartnerStoresAll psaOb = new QueryPartnerStoresAllDao(tool).queryByMerchantIdAndStoreId(merchantId.intValue(), storeId.intValue());
                if (Objects.isNull(psaOb)) {
                    return Collections.emptyList();
                }
                dbId = psaOb.getDbId();
                if (StringUtils.equals(ElasticEnum.O2O.getChannel(), psaOb.getChannel())) {
                    groupId = psaOb.getGroupId();
                }
                break;

            case pgc_store_info_increment:
                Integer merchantId02 = ((PgcStoreInfoIncrement) data).getOrganizationId();
                Integer storeId02 = ((PgcStoreInfoIncrement) data).getStoreId();
                if (Objects.isNull(merchantId02) || Objects.isNull(storeId02)) {
                    return Collections.emptyList();
                }

                // 未查询到门店直接返回
                PartnerStoresAll psaInc = new QueryPartnerStoresAllDao(tool).queryByMerchantIdAndStoreId(merchantId02, storeId02);
                if (Objects.isNull(psaInc)) {
                    return Collections.emptyList();
                }

                dbId = psaInc.getDbId();
                if (StringUtils.equals(ElasticEnum.O2O.getChannel(), psaInc.getChannel())) {
                    groupId = psaInc.getGroupId();
                }
                merchantId = Long.valueOf(merchantId02);
                storeId = Long.valueOf(storeId02);
                break;

            case platform_goods:
                merchantId = ((PlatformGoods) data).getMerchantId();
//                dbId = new QueryPartnerStoresAllDao(tool).queryDbIdByMerchantId(merchantId.intValue());
                storeId = ((PlatformGoods) data).getStoreId();
                internalId = ((PlatformGoods) data).getGoodsInternalId();
                if (Objects.isNull(merchantId) || Objects.isNull(storeId) || StringUtils.isBlank(internalId)) {
                    return Collections.emptyList();
                }
                break;

            case gc_sku_extend:
                String skuNo = ((SkuExtend) data).getSkuNo();
                if (StringUtils.isBlank(skuNo)
                        || !StringUtils.equals(CommonConstants.SPELL_WORD, ((SkuExtend) data).getTitle())) {
                    log.info("transferParams gc_sku_extend title no need update, sku_code: {}", skuNo);
                    return Collections.emptyList();
                }
                String[] split = skuNo.split(SymbolConstants.HOR_LINE);
                if (split.length != 2) {
                    log.info("transferParams gc_sku_extend sku_code is not valid, sku_code: {}", skuNo);
                    return Collections.emptyList();
                }
                merchantId = Long.valueOf(split[0]);
                dbId = new QueryPartnerStoresAllDao(tool).queryDbIdByMerchantId(merchantId.intValue());
                internalId = split[1];
                if (Objects.isNull(dbId) || StringUtils.isBlank(internalId)) {
                    return Collections.emptyList();
                }
                break;

            case gc_partner_goods_gift:
                dbId = ((PartnerGoodsGift) data).getDbId();
                internalId = ((PartnerGoodsGift) data).getInternalId();
                break;

            case gc_goods_dosage:
                tradeCode = ((GoodsDosage) data).getTradeCode();
                return this.concatParamByTradeCode(tradeCode);

            case gc_standard_goods_syncrds:
                tradeCode = ((StandardGoodsSyncrds) data).getTradeCode();
                return this.concatParamByTradeCode(tradeCode);

            case gc_goods_spu_attr_syncrds:
                tradeCode = ((GoodsSpuAttrSyncrds) data).getBarCode();
                if (StringUtils.isBlank(tradeCode) || Objects.isNull(((GoodsSpuAttrSyncrds) data).getAttrId())) {
                    return Collections.emptyList();
                }
                return this.concatParamByTradeCode(tradeCode);

            case gc_goods_attr_info_syncrds:
                if (Objects.isNull(data.getId())) {
                    return Collections.emptyList();
                }
                List<String> tradeCodeList2 = new QueryStandardGoodsDao(tool).queryTradeCodeByAttrId(data.getId());
                return tradeCodeList2.stream().flatMap(code -> {
                    List<PgConcatParams> l = this.concatParamByTradeCode(code);
                    return l.stream();
                }).distinct().collect(Collectors.toList());

            case gc_goods_cate_spu:
                tradeCode = ((GoodsCateSpu) data).getBarCode();
                if (Objects.isNull(tradeCode)) {
                    return Collections.emptyList();
                }
                return this.concatParamByTradeCode(tradeCode);

            case gc_category_info:
                if (Objects.isNull(data.getId())) {
                    return Collections.emptyList();
                }
                List<String> tradeCodeList3 = new QueryStandardGoodsDao(tool).queryTradeCodeByCateId(data.getId());
                if (CollectionUtils.isEmpty(tradeCodeList3)) {
                    return Collections.emptyList();
                }
                return tradeCodeList3.stream().flatMap(code -> {
                    List<PgConcatParams> l = this.concatParamByTradeCode(code);
                    return l.stream();
                }).collect(Collectors.toList());

            case partner_goods_img:
                dbId = ((PartnerGoodsImg) data).getDbId();
                internalId = ((PartnerGoodsImg) data).getInternalId();
                break;

            case partner_goods_info:
                dbId = ((PartnerGoodsInfo) data).getDbId();
                internalId = ((PartnerGoodsInfo) data).getInternalId();
                break;

            case gc_goods_manual:
                approvalNumber = ((GoodsManual) data).getApprovalNumber();
                return this.concatParamByApprovalNum(approvalNumber);

            case base_goods:
                approvalNumber = ((BaseGoods) data).getApprovalNumber();
                return this.concatParamByApprovalNum(approvalNumber);

            case gc_base_nootc:
                approvalNumber = ((BaseNootc) data).getApprovalNumber();
                return this.concatParamByApprovalNum(approvalNumber);

            case gc_base_spu_img:
                approvalNumber = ((BaseSpuImg) data).getApprovalNumber();
                return this.concatParamByApprovalNum(approvalNumber);

            case gc_goods_spu:
                approvalNumber = ((GoodsSpu) data).getApprovalNumber();
//                spuId = data.getId();
                return this.concatParamByApprovalNum(approvalNumber);

            case gc_config_sku:
                String configSkuNo = ((ConfigSku) data).getSkuNo();
                if (StringUtils.isBlank(configSkuNo)) {
                    log.info("transferParams gc_config_sku no is null, sku_code: {}", configSkuNo);
                    return Collections.emptyList();
                }
                String[] configSplit = configSkuNo.split(SymbolConstants.HOR_LINE);
                if (configSplit.length != 2) {
                    log.info("transferParams gc_config_sku sku_code is not valid, sku_code: {}", configSkuNo);
                    return Collections.emptyList();
                }
                merchantId = Long.valueOf(configSplit[0]);
                dbId = new QueryPartnerStoresAllDao(tool).queryDbIdByMerchantId(merchantId.intValue());
                internalId = configSplit[1];
                if (Objects.isNull(dbId) || StringUtils.isBlank(internalId)) {
                    return Collections.emptyList();
                }
                break;

            case stock_goods:
                merchantId = ((StockGoods) data).getMerchantId();
                storeId = ((StockGoods) data).getStoreId();
                internalId = ((StockGoods) data).getInternalId();
                if (Objects.isNull(merchantId) || Objects.isNull(storeId) || StringUtils.isBlank(internalId)) {
                    return Collections.emptyList();
                }
                OrganizeBase organizeBase = redisService.queryOrganizeBase(merchantId.intValue(), storeId.intValue());
                if (Objects.isNull(organizeBase) || !CommonConstants.IS_O2O.equals(organizeBase.getIsO2O())) {
                    return Collections.emptyList();
                }
                /*PartnerStoresAll psaSg = new QueryPartnerStoresAllDao(tool).queryByMerchantIdAndStoreId(merchantId.intValue(), storeId.intValue());
                if (Objects.isNull(psaSg) || Objects.isNull(psaSg.getDbId()) || StringUtils.isBlank(psaSg.getGroupId())) {
                    return Collections.emptyList();
                }
                dbId = psaSg.getDbId();
                groupId = psaSg.getGroupId();*/
                break;
            case price_list_details:
                merchantId = ((PriceListDetails) data).getOrganizationId();
                internalId = ((PriceListDetails) data).getInternalId();

                if (Objects.isNull(merchantId) || Objects.isNull(internalId)) {
                    return Collections.emptyList();
                }
                break;
            case price_store:
                storeId = ((PriceStore) data).getStoreId();
                merchantId = ((PriceStore) data).getOrganizationId();

                if (Objects.isNull(storeId) || Objects.isNull(merchantId)) {
                    return Collections.emptyList();
                }
                break;
            default:
                return Collections.emptyList();
        }

        if (StringUtils.isBlank(approvalNumber) && StringUtils.isBlank(tradeCode) && Objects.isNull(dbId)
                && StringUtils.isBlank(internalId) && Objects.isNull(merchantId) && Objects.isNull(storeId)) {
            log.info("transferParams is empty, table:{}, id:{}", table.name(), data.getId());
            return Collections.emptyList();
        }

        PgConcatParams concatParams = new PgConcatParams();
        concatParams.setInternalId(internalId);
        concatParams.setDbId(Objects.nonNull(dbId) ? String.valueOf(dbId) : null);
        concatParams.setApprovalNumber(approvalNumber);
        concatParams.setTradeCode(tradeCode);
        concatParams.setMerchantId(Objects.nonNull(merchantId) ? String.valueOf(merchantId) : null);
        concatParams.setStoreId(Objects.nonNull(storeId) ? String.valueOf(storeId) : null);
        concatParams.setSpuId(spuId);
        concatParams.setGroupId(groupId);
        return Collections.singletonList(concatParams);
    }


    /**
     * 根据条码组装参数
     * @param tradeCode
     * @return
     */
    public List<PgConcatParams> concatParamByTradeCode(String tradeCode) {
        if (StringUtils.isBlank(tradeCode)) {
            return Collections.emptyList();
        }
        PgConcatParamsDAO paramsDAO = new PgConcatParamsDAO(tool);
        List<PgConcatParams> list = paramsDAO.queryListByTradeCode(tradeCode);
        return CollectionUtils.isEmpty(list) ? Collections.emptyList() : list;
    }

    /**
     * 根据批文号组装参数
     * @param approvalNumber
     * @return
     */
    public List<PgConcatParams> concatParamByApprovalNum(String approvalNumber) {
        if (StringUtils.isBlank(approvalNumber)) {
            return Collections.emptyList();
        }
        PgConcatParamsDAO paramsDAO = new PgConcatParamsDAO(tool);
        List<PgConcatParams> list = paramsDAO.queryListByApprovalNum(approvalNumber);
        return CollectionUtils.isEmpty(list) ? Collections.emptyList() : list;
    }

    /**
     * 查询商品组装 下发下个算子
     * @param operate
     * @param table
     * @param collector
     * @param data
     * @param paramsList
     */
    public void queryGoodsThenCollect(String operate, Table.BaseDataTable table, Model data,
                                      List<String> modFieldList,
                                      Collector<BasicModel<ElasticO2O>> collector,
                                      List<PgConcatParams> paramsList,
                                      int modCondition) {

        // platform_goods 自带sku_code 直接更新es
        if (StringUtils.equals(Table.BaseDataTable.platform_goods.name(), table.name())) {
            this.collectPlatformGoods(operate, table, data, modFieldList, collector);
            return;
        }

        // stock_goods 库存更新逻辑单独处理
        if (StringUtils.equals(Table.BaseDataTable.stock_goods.name(), table.name())) {
            this.collectStockGoods(operate, table, data, modFieldList, collector, paramsList);
            return;
        }


        // price_store 价格中心门店价格清单关系表更新逻辑单独处理
        if (StringUtils.equals(Table.BaseDataTable.price_store.name(), table.name())) {
            this.collectPriceStore(operate, table, data, modFieldList, collector);
            return;
        }

        // price_list_details 价格中心价格更新逻辑单独处理
        if (StringUtils.equals(Table.BaseDataTable.price_list_details.name(), table.name())) {
            this.collectPriceListDetails(operate, table, data, modFieldList, collector);
            return;
        }

        // gc_partner_goods_gift 关系商品上下架，直接组装全字段数据
        if (StringUtils.equals(Table.BaseDataTable.gc_partner_goods_gift.name(), table.name())) {
            this.collectForGoods(operate, table, modFieldList, collector, paramsList);
            return;
        }

        // gc_standard_goods_syncrds dtp 情况下组装全字段数据否则只组装部分数据
        if (StringUtils.equals(Table.BaseDataTable.gc_standard_goods_syncrds.name(), table.name())) {
            // 判断是否DTP，如果是dtp则需要组装所有数据，否则只需要组装部分数据
            // StandardGoodsSyncrds standard = (StandardGoodsSyncrds) data;
            if (CommonConstants.MOD_FIELD_NONE != modCondition && CommonConstants.MOD_FIELD_SINGLE != modCondition) {
            // if (this.checkIfDtp(standard.getTradeCode())) {
                this.collectForGoods(operate, table, modFieldList, collector, paramsList);
                return;
            }
        }

        if (StringUtils.equals(Table.BaseDataTable.gc_goods_spu_attr_syncrds.name(), table.name())) {
            // 判断是否DTP
            // GoodsSpuAttrSyncrds spuAttr = (GoodsSpuAttrSyncrds) data;
            if (CommonConstants.MOD_FIELD_NONE != modCondition && CommonConstants.MOD_FIELD_SINGLE != modCondition) {
            // if (this.checkIfDtp(spuAttr.getAttrId())) {
                this.collectForGoods(operate, table, modFieldList, collector, paramsList);
                return;
            }
        }

        if (StringUtils.equals(Table.BaseDataTable.gc_goods_attr_info_syncrds.name(), table.name())) {
            // 判断是否DTP
//            if (this.checkIfDtp((GoodsAttrInfoSyncrds) data)) {
                this.collectForGoods(operate, table, modFieldList, collector, paramsList);
                return;
//            }
        }

        if (StringUtils.equals(Table.BaseDataTable.organize_base.name(), table.name())) {
            // 校验是否DTP门店状态变更
            if (this.checkIfDtpStore(operate, modFieldList)) {
                this.collectForStoreGoods(operate, table, modFieldList, collector, paramsList);
                return;
            }
        }

        if (StringUtils.equals(Table.BaseDataTable.gc_config_sku.name(), table.name())) {
            // 校验是否DTP门店状态变更
            if (this.checkIfDtpStoreConfigSku(operate, modFieldList)) {
                this.collectForGoods(operate, table, modFieldList, collector, paramsList);
                return;
            }
        }

        // 其他情况
        this.collectOther(operate, table, data, modFieldList, collector, paramsList, modCondition);
    }

    /**
     * 门店DTP状态变更，需要重新刷数据
     * DTP门店非DTP商品不写入ES
     * @param operate
     * @param modFieldList
     * @return
     */
    private boolean checkIfDtpStore(String operate, List<String> modFieldList) {
        return StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                || StringUtils.equals(CommonConstants.OPERATE_DELETE, operate)
                || StringUtils.equals(CommonConstants.OPERATE_UPDATE_DELETE, operate)
                || modFieldList.contains(CommonConstants.MOD_FIELD_NET_TYPE);
    }

    /**
     * 门店DTP状态变更，需要重新刷数据
     * DTP门店非DTP商品不写入ES
     * @param operate
     * @param modFieldList
     * @return
     */
    private boolean checkIfDtpStoreConfigSku(String operate, List<String> modFieldList) {
        return StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                || StringUtils.equals(CommonConstants.OPERATE_DELETE, operate)
                || StringUtils.equals(CommonConstants.OPERATE_UPDATE_DELETE, operate)
                || modFieldList.contains(CommonConstants.MOD_FIELD_BARCODE);
    }

    private boolean checkIfDtp(String tradeCode) {
        Set<String> attrIdList = new HashSet<>();
        List<GoodsSpuAttrSyncrds> spuAttrList = redisService.queryGcGoodsSpuAttrSyncrds(tradeCode);
        spuAttrList.forEach(spuAttr -> {
            GoodsAttrInfoSyncrds attrInfo = redisService.queryGcGoodsAttrInfoSyncrds(spuAttr.getAttrId());
            if (Objects.nonNull(attrInfo)) {
                attrIdList.addAll(Arrays.asList(attrInfo.getPids().split(SymbolConstants.COMMA_EN)));
                attrIdList.add(String.valueOf(attrInfo.getId()));
            }
        });
        return CommonConstants.IS_DTP_TRUE.equals(Compute.isDtp(attrIdList));
    }

    private boolean checkIfDtp(Long attrId) {
        GoodsAttrInfoSyncrds attrInfo = redisService.queryGcGoodsAttrInfoSyncrds(attrId);
        return this.checkIfDtp(attrInfo);
    }

    private boolean checkIfDtp(GoodsAttrInfoSyncrds attrInfo) {
        if (Objects.isNull(attrInfo)) {
            return false;
        }
        Set<String> attrIdList = new HashSet<>(Arrays.asList(attrInfo.getPids().split(SymbolConstants.COMMA_EN)));
        attrIdList.add(String.valueOf(attrInfo.getId()));
        return CommonConstants.IS_DTP_TRUE.equals(Compute.isDtp(attrIdList));
    }

    /**
     * platform_goods
     * @param operate
     * @param table
     * @param data
     * @param modFieldList
     * @param collector
     */
    private void collectPlatformGoods(String operate, Table.BaseDataTable table, Model data, List<String> modFieldList, Collector<BasicModel<ElasticO2O>> collector) {
        log.info("OprtServiceImpl platform_goods:{}", data.getId());
        ElasticO2O o2o = singleFieldService.transferO2O(new ElasticO2O(), operate, table, data);
        o2o.setSkuCode(((PlatformGoods) data).getMerchantId() + SymbolConstants.HOR_LINE
                + ((PlatformGoods) data).getStoreId() + SymbolConstants.HOR_LINE
                + ((PlatformGoods) data).getChannel() + SymbolConstants.HOR_LINE
                + ((PlatformGoods) data).getGoodsInternalId());
        o2o.setSyncDate(LocalDateTime.now());
        collector.collect(new BasicModel<>(table.name(), operate, o2o, modFieldList, SyncTypeEnum.PART.code));
    }

    /**
     * platform_goods
     * @param operate
     * @param table
     * @param data
     * @param modFieldList
     * @param collector
     */
    private void collectStockGoods(String operate, Table.BaseDataTable table, Model data, List<String> modFieldList,
                                   Collector<BasicModel<ElasticO2O>> collector, List<PgConcatParams> paramsList) {
        if (CollectionUtils.isEmpty(paramsList)) {
            return;
        }
        PgConcatParams params = paramsList.get(0);
        if (StringUtils.isBlank(params.getMerchantId()) || StringUtils.isBlank(params.getStoreId())
                || StringUtils.isBlank(params.getInternalId())) {
            return;
        }
        StockGoods stockGoods = (StockGoods) data;

        if (Objects.isNull(stockGoods.getCenterState()) && Objects.isNull(stockGoods.getVirtualOn())
                && Objects.isNull(stockGoods.getVirtualQuantity()) && Objects.isNull(stockGoods.getQuantity())
                && Objects.isNull(stockGoods.getSaleState())) {
            return;
        }

        ElasticO2O o2o = singleFieldService.transferO2O(new ElasticO2O(), operate, table, data);
        o2o.setSkuCode(stockGoods.getMerchantId() + SymbolConstants.HOR_LINE
                + stockGoods.getStoreId() + SymbolConstants.HOR_LINE
                + CommonConstants.CHANNEL_O2O + SymbolConstants.HOR_LINE
                + stockGoods.getInternalId());
        o2o.setSyncDate(LocalDateTime.now());

        if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
            if (Objects.nonNull(stockGoods.getCenterState()) || Objects.nonNull(stockGoods.getSaleState())) {
                o2o.setIsOffShelf(Compute.isOffShelf(stockGoods, null, null));
            }
        } else {
            // 库存不删除
            return;
        }
        collector.collect(new BasicModel<>(table.name(), operate, o2o, modFieldList, SyncTypeEnum.PART.code));
    }

    /**
     * price_list_details变更处理逻辑
     * @param operate
     * @param table
     * @param data
     * @param modFieldList
     * @param collector
     */
    private void collectPriceListDetails(String operate, Table.BaseDataTable table, Model data, List<String> modFieldList,
                                         Collector<BasicModel<ElasticO2O>> collector) {
        PriceListDetails priceListDetails = (PriceListDetails) data;
        log.info("【价格中心同步脚本】【price_list_details】【处理更新】主数据：{}，更新状态：{}", data, operate);
        Long organizationId = priceListDetails.getOrganizationId();

        //获取连锁白名单：
        StockMerchant stockMerchant = redisService.queryStockMerchant(organizationId.intValue(), organizationId.intValue());
        //如果未配置白名单，或者价格白名单为关闭的状态，则不需要更新es价格
        if (Objects.isNull(stockMerchant) || stockMerchant.getPriceState() == 0) {
            log.info("【价格中心同步脚本】【price_list_details】【白名单关闭】连锁id：{}, 白名单配置：{}", organizationId, stockMerchant);
            return;
        }

        log.info("【价格中心同步脚本】【price_list_details】【白名单开启】白名单配置：{}", stockMerchant);

        this.queryPriceStore(table, operate, priceListDetails, modFieldList, collector);
    }

    /**
     * 查询price_store获取store_id
     * @param table
     * @param operate
     * @param priceListDetails
     * @param modFieldList
     * @param collector
     */
    private void queryPriceStore(Table.BaseDataTable table, String operate, PriceListDetails priceListDetails, List<String> modFieldList,
                                 Collector<BasicModel<ElasticO2O>> collector) {
        Map<String, Object> mapParams = new HashMap<>();

        int page = 0;
        Long id = 0L;
        List<Model> modelList;
        QueryLimitDao queryDAO = new PriceStoreDAO(tool);

        while (true) {
            mapParams.put("organizationId", priceListDetails.getOrganizationId());
            mapParams.put("listId", priceListDetails.getListId());
            mapParams.put("id", id);

            // 从数据库查询数据,一次查1w条
            modelList = QueryUtil.findLimitPlus(queryDAO, page, mapParams, null);
            if (CollectionUtils.isEmpty(modelList)) {
                break;
            }

            modelList.stream()
                    .forEach(model -> {
                        this.collectForPrice(table, modFieldList, operate, (PriceStore) model, priceListDetails, collector);
                    });

            if (modelList.size() < QueryUtil.QUERY_PAGE_SIZE) {
                break;
            }
            id = modelList.get(modelList.size() - 1).getId();
            page++;
        }

    }


    /**
     * price_store变更处理逻辑
     * @param operate
     * @param table
     * @param data
     * @param modFieldList
     * @param collector
     */
    private void collectPriceStore(String operate, Table.BaseDataTable table, Model data, List<String> modFieldList,
                                   Collector<BasicModel<ElasticO2O>> collector) {
        PriceStore priceStore = (PriceStore) data;
        log.info("【价格中心同步脚本】【price_store】【处理更新】主数据：{}，更新状态：{}", data, operate);
        Long organizationId = priceStore.getOrganizationId();

        //获取连锁白名单：
        StockMerchant stockMerchant = redisService.queryStockMerchant(organizationId.intValue(), organizationId.intValue());
        //如果未配置白名单，或者价格白名单为关闭的状态，则不需要更新es价格
        if (Objects.isNull(stockMerchant) || stockMerchant.getPriceState() == 0) {
            log.info("【价格中心同步脚本】【price_store】【白名单关闭】连锁id：{}, 白名单配置：{}", organizationId, stockMerchant);
            return;
        }

        log.info("【价格中心同步脚本】【price_store】【白名单开启】白名单配置：{}", stockMerchant);
        //根据连锁id、价格清单，获取商品内码以及价格明细
        this.queryPriceDetails(table, operate, priceStore, modFieldList, collector);
    }

    /**
     * 查询price_details获取价格明细
     * @param operate
     * @param modFieldList
     * @param collector
     */
    private void queryPriceDetails(Table.BaseDataTable table, String operate, PriceStore priceStore, List<String> modFieldList,
                                        Collector<BasicModel<ElasticO2O>> collector) {

        Map<String, Object> mapParams = new HashMap<>();
        Map<String, Object> listParams = new HashMap<>();

        int page = 0;
        Long id = 0L;
        List<Model> modelList;
        QueryLimitDao queryDAO = new PriceListDetailsDAO(tool);
        PriceListDAO listDAO = new PriceListDAO(tool);
        while (true) {
            mapParams.put("organizationId", priceStore.getOrganizationId());
            mapParams.put("listId", priceStore.getListId());
            mapParams.put("id", id);

            // 从数据库查询数据,一次查1w条
            modelList = QueryUtil.findLimitPlus(queryDAO, page, mapParams, null);
            if (CollectionUtils.isEmpty(modelList)) {
                //第一次没查到的情况下，才去从price_list中取parent_list_id
                if (id == 0) {
                    listParams.put("organizationId", priceStore.getOrganizationId());
                    listParams.put("listId", priceStore.getListId());
                    PriceList priceList = listDAO.queryOne(listParams);

                    //如果priceList有数据，并且parent_list_id不为空的情况下
                    if (!Objects.isNull(priceList) && StringUtils.isNotBlank(priceList.getParentListId())) {
                        priceStore.setListId(priceList.getParentListId());
                        this.queryPriceDetails(table, operate, priceStore, modFieldList, collector);
                    }
                }
                break;
            }

            modelList.stream()
                    .forEach(model -> {
                        this.collectForPrice(table, modFieldList, operate, priceStore, (PriceListDetails) model, collector);
                    });

            if (modelList.size() < QueryUtil.QUERY_PAGE_SIZE) {
                break;
            }
            id = modelList.get(modelList.size() - 1).getId();
            page++;
        }
    }


    /**
     * 收集组装价格变更数据
     * @param table
     * @param modFieldList
     * @param operate
     * @param priceStore
     * @param priceListDetails
     * @param collector
     */
    private void collectForPrice(Table.BaseDataTable table, List<String> modFieldList, String operate, PriceStore priceStore, PriceListDetails priceListDetails, Collector<BasicModel<ElasticO2O>> collector) {
        String skuCode = Compute.skuCodeFromPrice(priceStore, priceListDetails);

        if (StringUtils.isBlank(skuCode)) {
            return;
        }

        ElasticO2O elasticO2O = new ElasticO2O();
        String value,key = null;
        Long setNX = 0L;

        switch (operate) {
            case CommonConstants.OPERATE_DELETE:
            case CommonConstants.OPERATE_UPDATE_DELETE:
                //根据 skuNo+salePrice+originalPrice 设置redis锁：
                value = Compute.priceRedisKey(skuCode, new BigDecimal(0), new BigDecimal(0));
                key = String.format(RedisConst.REDIS_SKU_CODE, value);

                setNX = redisService.setNX(key, value);
                //设置成功的情况下，再更新es数据：
                if (setNX == 0) {
                    log.info("【价格中心同步脚本】【{}】【redis锁设置失败】，数据：{}", table.name(), value);
                    break;
                }

                // 设置超时时间 防止redis数据量过多
                redisService.expire(key, RedisConst.REDIS_SKU_CODE_TIME_OUT);
                elasticO2O.setSkuCode(skuCode);
                elasticO2O.setSalePrice(new BigDecimal(0));
                elasticO2O.setBasePrice(new BigDecimal(0));

                log.info("【价格中心同步脚本】【{}】【es变更】数据：{}", table.name(), elasticO2O);
                collector.collect(new BasicModel<>(table.name(), operate, elasticO2O, modFieldList, SyncTypeEnum.PART.code));
                break;
            case CommonConstants.OPERATE_UPDATE:
            case CommonConstants.OPERATE_INSERT:
                //根据 skuNo+salePrice+originalPrice 设置redis锁：
                value = Compute.priceRedisKey(skuCode, priceListDetails.getSkuPrice(), priceListDetails.getOriginalPrice());
                key = String.format(RedisConst.REDIS_SKU_CODE, value);

                setNX = redisService.setNX(key, value);
                //设置成功的情况下，再更新es数据：
                if (setNX == 0) {
                    log.info("【价格中心同步脚本】【{}】【redis锁设置失败】，数据：{}", table.name(), value);
                }

                // 设置超时时间 防止redis数据量过多
                redisService.expire(key, RedisConst.REDIS_SKU_CODE_TIME_OUT);
                elasticO2O.setSkuCode(skuCode);
                elasticO2O.setSalePrice(priceListDetails.getSkuPrice());
                elasticO2O.setBasePrice(priceListDetails.getOriginalPrice());

                log.info("【价格中心同步脚本】【{}】【es变更】，数据：{}", table.name(), elasticO2O);
                collector.collect(new BasicModel<>(table.name(), operate, elasticO2O, modFieldList, SyncTypeEnum.PART.code));

                break;
            default:
                log.error("【价格中心同步脚本】【{}】【未知更新类型】：{}", table.name(), operate);
                break;
        }
    }


    /**
     * gc_partner_goods_gift
     * @param operate
     * @param table
     * @param modFieldList
     * @param collector
     * @param paramsList
     */
    private void collectForGoods(String operate, Table.BaseDataTable table, List<String> modFieldList,
                             Collector<BasicModel<ElasticO2O>> collector, List<PgConcatParams> paramsList) {
        paramsList.forEach(concatParams -> {
            Map<String, Object> mapParams = new HashMap<>();
            int page = 0;
            Long id = 0L;
            List<Model> modelList;
            QueryLimitDao queryDAO = new QueryBasicChangeDao(SQL_PARTNER_GOODS, concatParams, tool);
            while (true) {
                mapParams.put("id", id);
                // 从数据库查询数据,一次查1w条
                modelList = QueryUtil.findLimitPlus(queryDAO, page, mapParams, null);
                if (CollectionUtils.isEmpty(modelList)) {
                    break;
                }

                modelList.stream()
                        .filter(model -> !Compute.isFilterDbId(model, filterDbIdList))
                        .forEach(model -> {
                            multiFieldService.splitByPartnerGoods(table.name(), operate, modFieldList, (PartnerGoods) model, collector);
                        });

                if (modelList.size() < QueryUtil.QUERY_PAGE_SIZE) {
                    break;
                }
                id = modelList.get(modelList.size() - 1).getId();
                page++;
            }
        });
    }

    private void collectForStoreGoods(String operate, Table.BaseDataTable table, List<String> modFieldList,
                                     Collector<BasicModel<ElasticO2O>> collector, List<PgConcatParams> paramsList) {
        paramsList.forEach(concatParams -> {
            Map<String, Object> mapParams = new HashMap<>();
            int page = 0;
            Long id = 0L;
            List<Model> modelList;
            QueryLimitDao queryDAO = new QueryStoreGoodsDao(SQL_STORE_GOODS, concatParams, tool);
            while (true) {
                mapParams.put("id", id);
                // 从数据库查询数据,一次查1w条
                modelList = QueryUtil.findLimitPlus(queryDAO, page, mapParams, null);
                if (CollectionUtils.isEmpty(modelList)) {
                    break;
                }

                modelList.stream()
                        .filter(model -> !Compute.isFilterDbId(model, filterDbIdList))
                        .forEach(model -> {
                            BasicTimeStoreGoods basicTimeStoreGoods = new BasicTimeStoreGoods().transfer((PartnerStoreGoods) model, concatParams);
                            multiFieldService.splitByBasicTimeStoreGoods(table.name(), operate, modFieldList, basicTimeStoreGoods, collector);
                        });

                if (modelList.size() < QueryUtil.QUERY_PAGE_SIZE) {
                    break;
                }
                id = modelList.get(modelList.size() - 1).getId();
                page++;
            }
        });
    }

    private void collectOther(String operate, Table.BaseDataTable table, Model data, List<String> modFieldList,
                              Collector<BasicModel<ElasticO2O>> collector, List<PgConcatParams> paramsList, int modCondition) {
        paramsList.forEach(concatParams -> {
            Map<String, Object> mapParams = new HashMap<>();
            int page = 0;
            Long id = 0L;
            List<Model> modelList;
            QueryLimitDao queryDAO;
            if (StringUtils.isNotBlank(concatParams.getGroupId())) {
                queryDAO = new QueryStoreGoodsDao(SQL_STORE_GOODS, concatParams, tool);
            }
            else {
                queryDAO = new QueryBasicChangeDao(SQL_PARTNER_GOODS, concatParams, tool);
            }
            while (true) {
                mapParams.put("id", id);
                // 从数据库查询数据,一次查1w条
                modelList = QueryUtil.findLimitPlus(queryDAO, page, mapParams, null);
                if (CollectionUtils.isEmpty(modelList)) {
                    break;
                }

                modelList.stream()
                        .filter(model -> !Compute.isFilterDbId(model, filterDbIdList))
                        .forEach(model -> this.transferThenCollect(operate, table, data, modFieldList,
                                collector, model, concatParams, modCondition));

                if (modelList.size() < QueryUtil.QUERY_PAGE_SIZE) {
                    break;
                }
                id = modelList.get(modelList.size() - 1).getId();
                page++;
            }
        });
    }

    /**
     * 组装数据 下发下个算子
     * @param operate
     * @param table
     * @param data
     * @param modFieldList
     * @param collector
     * @param model
     * @param pgConcatParams
     * @param modCondition
     */
    private void transferThenCollect(String operate, Table.BaseDataTable table, Model data,
                                     List<String> modFieldList,
                                     Collector<BasicModel<ElasticO2O>> collector,
                                     Model model,
                                     PgConcatParams pgConcatParams,
                                     int modCondition) {
        // 添加逻辑处理门店发生变更情况下，依据partner_store_goods为主表，其他情况下以partner_goods表为准
        List<SplitMiddleData> middleData;
        PartnerGoods goods;
        if (model instanceof PartnerGoods) {
            goods = (PartnerGoods) model;
            middleData = this.assemblyByGoods(goods, pgConcatParams);
        }
        else if (model instanceof PartnerStoreGoods) {
            PartnerStoreGoods storeGoods = (PartnerStoreGoods) model;
            goods = hBaseService.queryPartnerGoods(storeGoods.getDbId(), storeGoods.getGoodsInternalId());
            if (Objects.isNull(goods)) {
                return;
            }
            middleData = this.assemblyByStoreGoods(storeGoods, pgConcatParams, goods);
        }
        else {
            return;
        }

        if (CollectionUtils.isEmpty(middleData)) {
            return;
        }

        // 组装基础数据
        ElasticO2O o2o;
        if (CommonConstants.MOD_FIELD_SINGLE == modCondition
                || (CommonConstants.MOD_FIELD_KEY == modCondition
                    && Arrays.asList(SingleFieldService.TABLE).contains(table.name()))) {
            o2o = singleFieldService.transferO2O(new ElasticO2O(), operate, table, data);
        }
        else {
            o2o = multiFieldService.transferO2O(new ElasticO2O(), operate, table, data, goods);
        }

        if (Objects.isNull(o2o)) {
            return;
        }

        // 设置同步时间
        o2o.setSyncDate(LocalDateTime.now());

        middleData.forEach(m -> {
            // 克隆字段值
            ElasticO2O clone = o2o.clone();

            // 特殊字段处理
            boolean b = multiFieldService.specialFields(table, goods, m, clone);
            if (!b) {
                log.info("OprtServiceImpl specialFields return false table:{} data:{}", table, JSON.toJSONString(data));
                return;
            }

            // skuCode 赋值
            clone.setSkuCode(m.getSkuCode());

            // 下发算子
            collector.collect(new BasicModel<>(table.name(), operate, clone, modFieldList, SyncTypeEnum.PART.code));
        });
    }



    /**
     * 根据goods表组装数据
     * @param goods
     * @return
     */
    private List<SplitMiddleData> assemblyByGoods(PartnerGoods goods, PgConcatParams params) {
        if (null == goods.getDbId() || StringUtils.isBlank(goods.getInternalId())) {
            return Collections.emptyList();
        }
        List<PartnerStoresAll> list = redisService.queryPartnerStoresAll(goods.getDbId());
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        // 根据条件过滤数据
        list = list.stream().filter(l ->
                (StringUtils.isBlank(params.getMerchantId())
                        || StringUtils.equals(params.getMerchantId(), l.getMerchantId().toString()))
                        && (StringUtils.isBlank(params.getStoreId())
                        || StringUtils.equals(params.getStoreId(), l.getStoreId().toString()))
        ).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        List<String> rowKeyList = list.stream().map(e -> (e.getDbId() + "-" + e.getGroupId() + "-" + goods.getInternalId()))
                .collect(Collectors.toList());

        List<PartnerStoreGoods> storeGoodsList = hBaseService.queryPartnerStoreGoods(rowKeyList);

        List<SplitMiddleData> collect = list.stream().map(psa -> {
            if (StringUtils.equals(ElasticEnum.O2O.getChannel(), psa.getChannel())) {
                if (CollectionUtils.isEmpty(storeGoodsList)) {
                    return null;
                }
                PartnerStoreGoods storeGoods = storeGoodsList.stream()
                        .filter(e -> psa.getDbId().equals(e.getDbId()) && StringUtils.equals(psa.getGroupId(), e.getGroupId())
                                && StringUtils.equals(goods.getInternalId(), e.getGoodsInternalId()))
                        .findFirst().orElse(null);
                if (null == storeGoods) {
                    return null;
                }
                return new SplitMiddleData(psa, goods, storeGoods);
            } else if (StringUtils.equals(ElasticEnum.B2C.getChannel(), psa.getChannel())) {
                return new SplitMiddleData(psa, goods, null);
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return collect;
    }


    /**
     * 根据goods表组装数据
     * @param storeGoods
     * @param params
     * @return
     */
    private List<SplitMiddleData> assemblyByStoreGoods(PartnerStoreGoods storeGoods, PgConcatParams params,
                                                       PartnerGoods goods) {
        if (Objects.isNull(storeGoods.getDbId()) || StringUtils.isBlank(storeGoods.getGroupId())
                || StringUtils.isBlank(storeGoods.getGoodsInternalId())) {
            return Collections.emptyList();
        }
        List<PartnerStoresAll> list = redisService.queryPartnerStoresAll(storeGoods.getDbId(), storeGoods.getGroupId());
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        // 根据条件过滤数据
        list = list.stream().filter(l ->
                (StringUtils.isBlank(params.getMerchantId())
                        || StringUtils.equals(params.getMerchantId(), l.getMerchantId().toString()))
                        && (StringUtils.isBlank(params.getStoreId())
                        || StringUtils.equals(params.getStoreId(), l.getStoreId().toString()))
        ).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        return list.stream().map(psa -> {
            if (StringUtils.equals(ElasticEnum.O2O.getChannel(), psa.getChannel())) {
                return new SplitMiddleData(psa, goods, storeGoods);
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

    }
}
