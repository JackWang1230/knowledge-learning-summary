package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.constants.BaseGoodsAllListenFieldConstants;
import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.dao.time.QueryPartnerStoresAllDao;
import cn.wr.collect.sync.function.Compute;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.gc.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.Collector;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static cn.wr.collect.sync.constants.CommonConstants.*;


public class BasicGoodsAllSplitFlatMap extends RichFlatMapFunction<BasicModel<Model>, BasicModel<PgConcatParams>> {
    private QueryPartnerStoresAllDao queryPartnerStoresAllDao;
    private ParameterTool parameterTool;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void open(Configuration parameters) throws Exception {
        //LOGGER.info("### BasicDataGoodsAllFlatMap open");
        super.open(parameters);
        this.parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        this.queryPartnerStoresAllDao = new QueryPartnerStoresAllDao(parameterTool);
    }

    @Override
    public void flatMap(BasicModel<Model> basicModel, Collector<BasicModel<PgConcatParams>> collector) throws Exception {
        if (Objects.isNull(basicModel) || StringUtils.isBlank(basicModel.getTableName())
                || Objects.isNull(basicModel.getData()) || StringUtils.isBlank(basicModel.getOperate())
                || (OPERATE_UPDATE.equals(basicModel.getOperate()) && Objects.isNull(basicModel.getOld()))) {
            return;
        }
        // 判断表名，过滤无用数据
        switch (basicModel.getTableName()) {
            case ORGANIZE_BASE:// todo 已测试
                if (OPERATE_INSERT.equals(basicModel.getOperate()) || OPERATE_DELETE.equals(basicModel.getOperate())) {
                    OrganizeBase organizeBase = (OrganizeBase) basicModel.getData();
                    // 若isO2O = 1 && netType != 2,门店有效，不是dtp药品，门店商品可售，continue
                    if (STORE_O2O_OPEN.equals(organizeBase.getIsO2O())) {
                        Integer dbId = queryPartnerStoresAllDao.queryDbIdByStoreId(organizeBase.getOrganizationId().intValue());
                        if (Objects.isNull(dbId)) {
                            return;
                        }
                        PgConcatParams pgConcatParams = new PgConcatParams();
                        pgConcatParams.setMerchantId(organizeBase.getRootId().toString());
                        pgConcatParams.setStoreId(organizeBase.getOrganizationId().toString());
                        pgConcatParams.setDbId(dbId.toString());
                        collector.collect(new BasicModel<>(basicModel.getTableName(), basicModel.getOperate(), pgConcatParams));
                    }
                } else if (OPERATE_UPDATE.equals(basicModel.getOperate())) {
                    // 只监听isO2O，netType字段
                    List<String> modFieldList = basicModel.getModFieldList();
                    boolean includeUpdateField = modFieldList.stream().filter(field ->
                            BaseGoodsAllListenFieldConstants.OrganizeBase.IS_O2O.getFieldEN().equals(field)).findFirst().isPresent();
                    if (includeUpdateField) {
                        OrganizeBase newOrganizeBase = (OrganizeBase) basicModel.getData();
                        OrganizeBase oldOrganizeBase = (OrganizeBase) basicModel.getOld();
                        // 若新isO2O = 1 && 新netType != 2，逻辑同insert，新门店商品可售，continue
                        if (STORE_O2O_OPEN.equals(newOrganizeBase.getIsO2O())) {
                            Integer dbId = queryPartnerStoresAllDao.queryDbIdByStoreId(newOrganizeBase.getOrganizationId().intValue());
                            if (Objects.isNull(dbId)) {
                                return;
                            }
                            PgConcatParams pgConcatParams = new PgConcatParams();
                            pgConcatParams.setMerchantId(newOrganizeBase.getRootId().toString());
                            pgConcatParams.setStoreId(newOrganizeBase.getOrganizationId().toString());
                            pgConcatParams.setDbId(dbId.toString());
                            collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_INSERT, pgConcatParams));
                        }
                        // 若旧isO2O = 1 && 旧netType != 2 && (新isO2O <> 1 || 新netType = 2),逻辑同delete,门店商品不可售,continue
                        else if (STORE_O2O_OPEN.equals(oldOrganizeBase.getIsO2O())
                                && !STORE_O2O_OPEN.equals(newOrganizeBase.getIsO2O())) {
                            Integer dbId = queryPartnerStoresAllDao.queryDbIdByStoreId(newOrganizeBase.getOrganizationId().intValue());
                            if (Objects.isNull(dbId)) {
                                return;
                            }
                            PgConcatParams pgConcatParams = new PgConcatParams();
                            pgConcatParams.setMerchantId(newOrganizeBase.getRootId().toString());
                            pgConcatParams.setStoreId(newOrganizeBase.getOrganizationId().toString());
                            pgConcatParams.setDbId(dbId.toString());
                            collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_DELETE, pgConcatParams));
                        }
                    }
                }
                break;
            case PLATFORM_GOODS:// todo 已测试
                if (OPERATE_INSERT.equals(basicModel.getOperate())) {
                    PlatformGoods platformGoods = (PlatformGoods) basicModel.getData();
                    // 商品下架
                    if (checkPlatformGoodsParam(platformGoods) && IS_WR_OFF_SHELF_OFF.equals(platformGoods.getStatus())) {
                        Integer dbId = queryPartnerStoresAllDao.queryDbIdByStoreId(platformGoods.getStoreId().intValue());

                        if (Objects.isNull(dbId)) {
                            return;
                        }
                        PgConcatParams pgConcatParams = new PgConcatParams();
                        pgConcatParams.setMerchantId(platformGoods.getMerchantId().toString());
                        pgConcatParams.setStoreId(platformGoods.getStoreId().toString());
                        pgConcatParams.setInternalId(platformGoods.getGoodsInternalId());
                        pgConcatParams.setDbId(dbId.toString());
                        collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_DELETE, pgConcatParams));
                    }
                } else if (OPERATE_DELETE.equals(basicModel.getOperate())) {
                    PlatformGoods platformGoods = (PlatformGoods) basicModel.getData();
                    // 商品下架
                    if (checkPlatformGoodsParam(platformGoods) && IS_WR_OFF_SHELF_OFF.equals(platformGoods.getStatus())) {
                        Integer dbId = queryPartnerStoresAllDao.queryDbIdByStoreId(platformGoods.getStoreId().intValue());
                        if (Objects.isNull(dbId)) {
                            return;
                        }
                        PgConcatParams pgConcatParams = new PgConcatParams();
                        pgConcatParams.setMerchantId(platformGoods.getMerchantId().toString());
                        pgConcatParams.setStoreId(platformGoods.getStoreId().toString());
                        pgConcatParams.setDbId(dbId.toString());
                        pgConcatParams.setInternalId(platformGoods.getGoodsInternalId());
                        collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_INSERT, pgConcatParams));
                    }
                } else if (OPERATE_UPDATE.equals(basicModel.getOperate())) {
                    List<String> modFieldList = basicModel.getModFieldList();
                    boolean includeUpdateField = modFieldList.stream().filter(field -> BaseGoodsAllListenFieldConstants.PlatformGoods.STATUS.getFieldEN().equals(field)).findFirst().isPresent();
                    PlatformGoods oldPlatformGoods = (PlatformGoods) basicModel.getOld();
                    PlatformGoods newPlatformGoods = (PlatformGoods) basicModel.getData();
                    if (includeUpdateField && checkPlatformGoodsParam(oldPlatformGoods) && checkPlatformGoodsParam(newPlatformGoods)) {
                        // 若旧status = 1，新status = 0，商品上架，逻辑同delete
                        if (IS_WR_OFF_SHELF_OFF.equals(oldPlatformGoods.getStatus()) && IS_WR_OFF_SHELF_ON.equals(newPlatformGoods.getStatus())) {
                            Integer dbId = queryPartnerStoresAllDao.queryDbIdByStoreId(newPlatformGoods.getStoreId().intValue());
                            if (Objects.isNull(dbId)) {
                                return;
                            }
                            PgConcatParams pgConcatParams = new PgConcatParams();
                            pgConcatParams.setMerchantId(newPlatformGoods.getMerchantId().toString());
                            pgConcatParams.setStoreId(newPlatformGoods.getStoreId().toString());
                            pgConcatParams.setDbId(dbId.toString());
                            pgConcatParams.setInternalId(newPlatformGoods.getGoodsInternalId());
                            collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_INSERT, pgConcatParams));
                        } else if (IS_WR_OFF_SHELF_ON.equals(oldPlatformGoods.getStatus()) && IS_WR_OFF_SHELF_OFF.equals(newPlatformGoods.getStatus())) {
                            // 若旧status = 0，新status = 1 ，商品下架，逻辑同insert
                            Integer dbId = queryPartnerStoresAllDao.queryDbIdByStoreId(newPlatformGoods.getStoreId().intValue());

                            if (Objects.isNull(dbId)) {
                                return;
                            }
                            PgConcatParams pgConcatParams = new PgConcatParams();
                            pgConcatParams.setMerchantId(newPlatformGoods.getMerchantId().toString());
                            pgConcatParams.setStoreId(newPlatformGoods.getStoreId().toString());
                            pgConcatParams.setDbId(dbId.toString());
                            pgConcatParams.setInternalId(newPlatformGoods.getGoodsInternalId());
                            collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_DELETE, pgConcatParams));
                        }
                    }
                }
                break;
            case GC_GOODS_SPU_ATTR_SYNCRDS:// todo 已测试
                if (OPERATE_INSERT.equals(basicModel.getOperate())) {
                    GoodsSpuAttrSyncrds goodsSpuAttrSyncrds = (GoodsSpuAttrSyncrds) basicModel.getData();
                    // tradeCode不为空，attrId在31002, 31003, 31004内，continue
                    if (StringUtils.isNotBlank(goodsSpuAttrSyncrds.getBarCode()) && Arrays.asList(CommonConstants.EPHEDRINE_ATTR_IDS).contains(goodsSpuAttrSyncrds.getAttrId())) {
                        PgConcatParams pgConcatParams = new PgConcatParams();
                        pgConcatParams.setTradeCode(goodsSpuAttrSyncrds.getBarCode());
                        collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_DELETE, pgConcatParams));
                    }
                } else if (OPERATE_DELETE.equals(basicModel.getOperate())) {
                    GoodsSpuAttrSyncrds goodsSpuAttrSyncrds = (GoodsSpuAttrSyncrds) basicModel.getData();
                    // tradeCode不为空，attrId在31002, 31003, 31004内，continue
                    if (StringUtils.isNotBlank(goodsSpuAttrSyncrds.getBarCode()) && Arrays.asList(CommonConstants.EPHEDRINE_ATTR_IDS).contains(goodsSpuAttrSyncrds.getAttrId())) {
                        PgConcatParams pgConcatParams = new PgConcatParams();
                        pgConcatParams.setTradeCode(goodsSpuAttrSyncrds.getBarCode());
                        collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_UPDATE, pgConcatParams));
                    }
                } else if (OPERATE_UPDATE.equals(basicModel.getOperate())) {
                    List<String> modFieldList = basicModel.getModFieldList();
                    boolean includeUpdateField = modFieldList.stream().filter(field -> BaseGoodsAllListenFieldConstants.GcGoodsSpuAttrSyncrds.barCode.getFieldEN().equals(field) ||
                            BaseGoodsAllListenFieldConstants.GcGoodsSpuAttrSyncrds.attrId.getFieldEN().equals(field)).findFirst().isPresent();
                    if (includeUpdateField) {
                        GoodsSpuAttrSyncrds newGoodsSpuAttrSyncrds = (GoodsSpuAttrSyncrds) basicModel.getData();
                        GoodsSpuAttrSyncrds oldGoodsSpuAttrSyncrds = (GoodsSpuAttrSyncrds) basicModel.getOld();

                        // spuId不变更,旧attrId in (31002,31003,31004)，新attrId in (31002,31003,31004),return
                        if (!basicModel.getModFieldList().contains(BaseGoodsAllListenFieldConstants.GcGoodsSpuAttrSyncrds.barCode.getFieldEN())
                                && basicModel.getModFieldList().contains(BaseGoodsAllListenFieldConstants.GcGoodsSpuAttrSyncrds.attrId.getFieldEN())
                                && Arrays.asList(CommonConstants.EPHEDRINE_ATTR_IDS).contains(oldGoodsSpuAttrSyncrds.getAttrId())
                                && Arrays.asList(CommonConstants.EPHEDRINE_ATTR_IDS).contains(newGoodsSpuAttrSyncrds.getAttrId())
                                && Arrays.asList(CommonConstants.DTP_ATTR_IDS).contains(oldGoodsSpuAttrSyncrds.getAttrId())
                                && Arrays.asList(CommonConstants.DTP_ATTR_IDS).contains(newGoodsSpuAttrSyncrds.getAttrId())) {
                            return;
                        }
                        // 如果旧spuId不为空,旧attrId in (31002,31003,31004),商品的属性变成非麻黄碱，旧商品可售
                        if ((Arrays.asList(CommonConstants.EPHEDRINE_ATTR_IDS).contains(oldGoodsSpuAttrSyncrds.getAttrId())
                                || Arrays.asList(CommonConstants.DTP_ATTR_IDS).contains(oldGoodsSpuAttrSyncrds.getAttrId()))
                                && StringUtils.isNotBlank(oldGoodsSpuAttrSyncrds.getBarCode())) {
                            PgConcatParams pgConcatParams = new PgConcatParams();
                            pgConcatParams.setTradeCode(oldGoodsSpuAttrSyncrds.getBarCode());
                            collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_UPDATE, pgConcatParams));
                        }
                        // 如果新spuId不为空，新attrId in (31002,31003,31004)，商品的属性变成麻黄碱，新商品不可售
                        if ((Arrays.asList(CommonConstants.EPHEDRINE_ATTR_IDS).contains(newGoodsSpuAttrSyncrds.getAttrId())
                                || Arrays.asList(CommonConstants.DTP_ATTR_IDS).contains(newGoodsSpuAttrSyncrds.getAttrId()))
                                && StringUtils.isNotBlank(newGoodsSpuAttrSyncrds.getBarCode())) {
                            PgConcatParams pgConcatParams = new PgConcatParams();
                            pgConcatParams.setTradeCode(newGoodsSpuAttrSyncrds.getBarCode());
                            collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_UPDATE, pgConcatParams));
                        }
                    }
                }
                break;
            case GC_PARTNER_STORES_ALL://
                if (OPERATE_INSERT.equals(basicModel.getOperate()) || OPERATE_DELETE.equals(basicModel.getOperate())) {
                    PartnerStoresAll partnerStoresAll = (PartnerStoresAll) basicModel.getData();
                    PgConcatParams pgConcatParams = new PgConcatParams();
                    pgConcatParams.setMerchantId(partnerStoresAll.getMerchantId().toString());
                    pgConcatParams.setStoreId(partnerStoresAll.getStoreId().toString());
                    pgConcatParams.setDbId(partnerStoresAll.getDbId().toString());
                    collector.collect(new BasicModel<>(basicModel.getTableName(), basicModel.getOperate(), pgConcatParams));
                } else if (OPERATE_UPDATE.equals(basicModel.getOperate())) {
                    List<String> modFieldList = basicModel.getModFieldList();
                    boolean includeUpdateField = modFieldList.stream().filter(field -> BaseGoodsAllListenFieldConstants.GcPartnerStoresAll.dbId.getFieldEN().equals(field)
                            || BaseGoodsAllListenFieldConstants.GcPartnerStoresAll.groupId.getFieldEN().equals(field)
                            || BaseGoodsAllListenFieldConstants.GcPartnerStoresAll.merchantId.getFieldEN().equals(field)
                            || BaseGoodsAllListenFieldConstants.GcPartnerStoresAll.storeId.getFieldEN().equals(field)
                            || BaseGoodsAllListenFieldConstants.GcPartnerStoresAll.channel.getFieldEN().equals(field)).findFirst().isPresent();
                    if (includeUpdateField) {
                        PartnerStoresAll newPartnerStoresAll = (PartnerStoresAll) basicModel.getData();
                        PartnerStoresAll oldPartnerStoresAll = (PartnerStoresAll) basicModel.getOld();

                        PgConcatParams oldPgConcatParams = new PgConcatParams();
                        oldPgConcatParams.setMerchantId(oldPartnerStoresAll.getMerchantId().toString());
                        oldPgConcatParams.setStoreId(oldPartnerStoresAll.getStoreId().toString());
                        oldPgConcatParams.setDbId(oldPartnerStoresAll.getDbId().toString());
                        collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_DELETE, oldPgConcatParams));

                        PgConcatParams newPgConcatParams = new PgConcatParams();
                        newPgConcatParams.setMerchantId(newPartnerStoresAll.getMerchantId().toString());
                        newPgConcatParams.setStoreId(newPartnerStoresAll.getStoreId().toString());
                        newPgConcatParams.setDbId(newPartnerStoresAll.getDbId().toString());
                        collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_INSERT, newPgConcatParams));
                    }
                }
                break;

            case GC_STANDARD_GOODS_SYNCRDS:// todo 已测试
                if (OPERATE_INSERT.equals(basicModel.getOperate()) || OPERATE_DELETE.equals(basicModel.getOperate())) {
                    StandardGoodsSyncrds standardGoodsSyncrds = (StandardGoodsSyncrds) basicModel.getData();
                    if (StringUtils.isNotBlank(standardGoodsSyncrds.getTradeCode())) {
                        PgConcatParams pgConcatParams = new PgConcatParams();
                        pgConcatParams.setTradeCode(standardGoodsSyncrds.getTradeCode());
                        collector.collect(new BasicModel<>(basicModel.getTableName(), basicModel.getOperate(), pgConcatParams));
                    }
                } else if (OPERATE_UPDATE.equals(basicModel.getOperate())) {
                    List<String> modFieldList = basicModel.getModFieldList();
                    boolean includeUpdateField = modFieldList.stream().filter(field -> BaseGoodsAllListenFieldConstants.GcStandardGoodsSyncrds.tradeCode.getFieldEN().equals(field)
                            || BaseGoodsAllListenFieldConstants.GcStandardGoodsSyncrds.spuId.getFieldEN().equals(field)).findFirst().isPresent();
                    if (includeUpdateField) {
                        StandardGoodsSyncrds oldStandardGoodsSyncrds = (StandardGoodsSyncrds) basicModel.getOld();
                        StandardGoodsSyncrds newStandardGoodsSyncrds = (StandardGoodsSyncrds) basicModel.getData();

                        if (modFieldList.contains("trade_code")) {
                            PgConcatParams oldPgConcatParams = new PgConcatParams();
                            oldPgConcatParams.setTradeCode(oldStandardGoodsSyncrds.getTradeCode());
                            collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_DELETE, oldPgConcatParams));

                            PgConcatParams newPgConcatParams = new PgConcatParams();
                            newPgConcatParams.setTradeCode(newStandardGoodsSyncrds.getTradeCode());
                            collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_INSERT, newPgConcatParams));
                        } else {
                            PgConcatParams newPgConcatParams = new PgConcatParams();
                            newPgConcatParams.setTradeCode(newStandardGoodsSyncrds.getTradeCode());
                            collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_UPDATE, newPgConcatParams));
                        }

                    }
                }
                break;
            case PGC_STORE_INFO_INCREMENT:
                if (OPERATE_INSERT.equals(basicModel.getOperate())) {
                    PgcStoreInfoIncrement pgcStoreInfoIncrement = (PgcStoreInfoIncrement) basicModel.getData();
                    Integer dbId = queryPartnerStoresAllDao.queryDbIdByStoreId(pgcStoreInfoIncrement.getStoreId());
                    if (Objects.isNull(dbId)) {
                        return;
                    }
                    PgConcatParams oldPgConcatParams = new PgConcatParams();
                    oldPgConcatParams.setMerchantId(pgcStoreInfoIncrement.getOrganizationId().toString());
                    oldPgConcatParams.setStoreId(pgcStoreInfoIncrement.getStoreId().toString());
                    oldPgConcatParams.setDbId(dbId.toString());
                    collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_DELETE, oldPgConcatParams));


                    PgConcatParams newPgConcatParams = new PgConcatParams();
                    newPgConcatParams.setMerchantId(pgcStoreInfoIncrement.getOrganizationId().toString());
                    newPgConcatParams.setStoreId(pgcStoreInfoIncrement.getStoreId().toString());
                    newPgConcatParams.setDbId(dbId.toString());
                    newPgConcatParams.setLocation(Compute.location(pgcStoreInfoIncrement));
                    collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_INSERT, newPgConcatParams));
                } else if (OPERATE_DELETE.equals(basicModel.getOperate())) {
                    PgcStoreInfoIncrement pgcStoreInfoIncrement = (PgcStoreInfoIncrement) basicModel.getData();
                    Integer dbId = queryPartnerStoresAllDao.queryDbIdByStoreId(pgcStoreInfoIncrement.getStoreId());
                    if (Objects.isNull(dbId)) {
                        return;
                    }
                    PgConcatParams oldPgConcatParams = new PgConcatParams();
                    oldPgConcatParams.setMerchantId(pgcStoreInfoIncrement.getOrganizationId().toString());
                    oldPgConcatParams.setStoreId(pgcStoreInfoIncrement.getStoreId().toString());
                    oldPgConcatParams.setLocation(Compute.location(pgcStoreInfoIncrement));
                    oldPgConcatParams.setDbId(dbId.toString());
                    collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_DELETE, oldPgConcatParams));


                    PgConcatParams newPgConcatParams = new PgConcatParams();
                    newPgConcatParams.setMerchantId(pgcStoreInfoIncrement.getOrganizationId().toString());
                    newPgConcatParams.setStoreId(pgcStoreInfoIncrement.getStoreId().toString());
                    newPgConcatParams.setDbId(dbId.toString());
                    collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_INSERT, newPgConcatParams));
                } else if (OPERATE_UPDATE.equals(basicModel.getOperate())) {
                    List<String> modFieldList = basicModel.getModFieldList();
                    boolean includeUpdateField = modFieldList.stream().filter(field -> BaseGoodsAllListenFieldConstants.PgcStoreInfoIncrement.store_id.getFieldEN().equals(field)
                            || BaseGoodsAllListenFieldConstants.PgcStoreInfoIncrement.organization_id.getFieldEN().equals(field)
                            || BaseGoodsAllListenFieldConstants.PgcStoreInfoIncrement.longitude.getFieldEN().equals(field)
                            || BaseGoodsAllListenFieldConstants.PgcStoreInfoIncrement.latitude.getFieldEN().equals(field)).findFirst().isPresent();
                    if (includeUpdateField) {
                        PgcStoreInfoIncrement newPgcStoreInfoIncrement = (PgcStoreInfoIncrement) basicModel.getData();
                        PgcStoreInfoIncrement oldPgcStoreInfoIncrement = (PgcStoreInfoIncrement) basicModel.getOld();
                        // 门店的坐标改变,门店的id不改变
                        if (!modFieldList.contains(BaseGoodsAllListenFieldConstants.PgcStoreInfoIncrement.store_id.getFieldEN())
                                && !modFieldList.contains(BaseGoodsAllListenFieldConstants.PgcStoreInfoIncrement.organization_id.getFieldEN())
                                && (modFieldList.contains(BaseGoodsAllListenFieldConstants.PgcStoreInfoIncrement.longitude.getFieldEN())
                                || modFieldList.contains(BaseGoodsAllListenFieldConstants.PgcStoreInfoIncrement.latitude.getFieldEN()))) {
                            // 删除老的门店坐标数据,新增新的门店坐标数据

                            Integer dbId = queryPartnerStoresAllDao.queryDbIdByStoreId(oldPgcStoreInfoIncrement.getStoreId());
                            if (Objects.isNull(dbId)) {
                                return;
                            }
                            PgConcatParams oldPgConcatParams = new PgConcatParams();
                            oldPgConcatParams.setMerchantId(oldPgcStoreInfoIncrement.getOrganizationId().toString());
                            oldPgConcatParams.setStoreId(oldPgcStoreInfoIncrement.getStoreId().toString());
                            oldPgConcatParams.setLocation(Compute.location(oldPgcStoreInfoIncrement));
                            oldPgConcatParams.setDbId(dbId.toString());
                            collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_DELETE, oldPgConcatParams));

                            PgConcatParams newPgConcatParams = new PgConcatParams();
                            newPgConcatParams.setMerchantId(newPgcStoreInfoIncrement.getOrganizationId().toString());
                            newPgConcatParams.setStoreId(newPgcStoreInfoIncrement.getStoreId().toString());
                            newPgConcatParams.setLocation(Compute.location(newPgcStoreInfoIncrement));
                            newPgConcatParams.setDbId(dbId.toString());
                            collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_INSERT, newPgConcatParams));
                        } else if (modFieldList.contains(BaseGoodsAllListenFieldConstants.PgcStoreInfoIncrement.store_id.getFieldEN())
                                || modFieldList.contains(BaseGoodsAllListenFieldConstants.PgcStoreInfoIncrement.organization_id.getFieldEN())) {
                            // 门店id改变
                            // 删除老的门店坐标数据

                            Integer oldDbId = queryPartnerStoresAllDao.queryDbIdByStoreId(oldPgcStoreInfoIncrement.getStoreId());
                            if (Objects.nonNull(oldDbId)) {
                                PgConcatParams oldPgConcatParams1 = new PgConcatParams();
                                oldPgConcatParams1.setMerchantId(oldPgcStoreInfoIncrement.getOrganizationId().toString());
                                oldPgConcatParams1.setStoreId(oldPgcStoreInfoIncrement.getStoreId().toString());
                                oldPgConcatParams1.setLocation(Compute.location(oldPgcStoreInfoIncrement));
                                oldPgConcatParams1.setDbId(oldDbId.toString());
                                collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_DELETE, oldPgConcatParams1));

                                PgConcatParams oldPgConcatParams2 = new PgConcatParams();
                                oldPgConcatParams2.setMerchantId(oldPgcStoreInfoIncrement.getOrganizationId().toString());
                                oldPgConcatParams2.setStoreId(oldPgcStoreInfoIncrement.getStoreId().toString());
                                oldPgConcatParams2.setDbId(oldDbId.toString());
                                collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_INSERT, oldPgConcatParams2));
                            }


                            // 新增新的门店坐标数据
                            Integer newDbId = queryPartnerStoresAllDao.queryDbIdByStoreId(newPgcStoreInfoIncrement.getStoreId());
                            if (Objects.nonNull(newDbId)) {
                                PgConcatParams newPgConcatParams1 = new PgConcatParams();
                                newPgConcatParams1.setMerchantId(newPgcStoreInfoIncrement.getOrganizationId().toString());
                                newPgConcatParams1.setStoreId(newPgcStoreInfoIncrement.getStoreId().toString());
                                newPgConcatParams1.setDbId(newDbId.toString());
                                collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_DELETE, newPgConcatParams1));

                                PgConcatParams newPgConcatParams2 = new PgConcatParams();
                                newPgConcatParams2.setMerchantId(newPgcStoreInfoIncrement.getOrganizationId().toString());
                                newPgConcatParams2.setStoreId(newPgcStoreInfoIncrement.getStoreId().toString());
                                newPgConcatParams2.setLocation(Compute.location(newPgcStoreInfoIncrement));
                                newPgConcatParams2.setDbId(newDbId.toString());
                                collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_INSERT, newPgConcatParams2));
                            }

                        }
                    }
                }
                break;
            // 无update_delete
            case GC_GOODS_OVERWEIGHT:// todo 已测试
                if ((OPERATE_INSERT.equals(basicModel.getOperate()))) {
                    GoodsOverweight goodsOverweight = (GoodsOverweight) basicModel.getData();
                    // 若 isOverweight = 1,商品超重，continue
                    if (GOODS_OVERWEIGHT_STATUS_TRUE.equals(goodsOverweight.getIsOverweight())) {
                        PgConcatParams pgConcatParams = new PgConcatParams();
                        pgConcatParams.setTradeCode(goodsOverweight.getTradeCode());
                        collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_UPDATE, pgConcatParams));
                    }
                } else if (OPERATE_DELETE.equals(basicModel.getOperate())) {
                    GoodsOverweight goodsOverweight = (GoodsOverweight) basicModel.getData();
                    // 若 isOverweight = 1,商品超重，continue
                    if (GOODS_OVERWEIGHT_STATUS_TRUE.equals(goodsOverweight.getIsOverweight())) {
                        PgConcatParams pgConcatParams = new PgConcatParams();
                        pgConcatParams.setTradeCode(goodsOverweight.getTradeCode());
                        collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_UPDATE, pgConcatParams));
                    }
                } else if (OPERATE_UPDATE.equals(basicModel.getOperate())) {
                    List<String> modFieldList = basicModel.getModFieldList();
                    boolean includeUpdateField = modFieldList.stream().filter(field -> "trade_code".equals(field) || "is_overweight".equals(field)).findFirst().isPresent();
                    if (includeUpdateField) {
                        GoodsOverweight newGoodsOverweight = (GoodsOverweight) basicModel.getData();
                        GoodsOverweight oldGoodsOverweight = (GoodsOverweight) basicModel.getOld();
                        // 旧条码删除超重
                        if (GOODS_OVERWEIGHT_STATUS_TRUE.equals(oldGoodsOverweight.getIsOverweight())) {
                            PgConcatParams pgConcatParams = new PgConcatParams();
                            pgConcatParams.setTradeCode(oldGoodsOverweight.getTradeCode());
                            collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_UPDATE, pgConcatParams));
                        }
                        // 新条码新增超重
                        if (GOODS_OVERWEIGHT_STATUS_TRUE.equals(newGoodsOverweight.getIsOverweight())) {
                            PgConcatParams pgConcatParams = new PgConcatParams();
                            pgConcatParams.setTradeCode(oldGoodsOverweight.getTradeCode());
                            collector.collect(new BasicModel<>(basicModel.getTableName(), OPERATE_UPDATE, pgConcatParams));
                        }
                    }
                }
                break;
        }

    }

    private boolean checkPlatformGoodsParam(PlatformGoods platformGoods) {
        if (Objects.nonNull(platformGoods.getMerchantId()) && Objects.nonNull(platformGoods.getStoreId())
                && StringUtils.isNotBlank(platformGoods.getChannel()) && StringUtils.isNotBlank(platformGoods.getGoodsInternalId())
        ) {
            return true;
        }
        return false;
    }


    private boolean isDbIdNull(Integer storeId) {
        Integer oldDbId = queryPartnerStoresAllDao.queryDbIdByStoreId(storeId);
        return Objects.isNull(oldDbId) ? true : false;
    }
}
