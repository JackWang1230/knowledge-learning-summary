package cn.wr.collect.sync.service;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.function.Compute;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.gc.*;
import cn.wr.collect.sync.model.stock.StockGoods;
import cn.wr.collect.sync.utils.LocationValidUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class SingleFieldService {
    private static final Logger log = LoggerFactory.getLogger(SingleFieldService.class);
    public static final String[] TABLE = {
            /*Table.BaseDataTable.merchant_goods_category_mapping.name(),*/
            Table.BaseDataTable.gc_goods_overweight.name(),
            Table.BaseDataTable.organize_base.name(),
//            Table.BaseDataTable.gc_goods_manual.name(),
//            Table.BaseDataTable.pgc_store_info.name(),
            Table.BaseDataTable.pgc_store_info_increment.name(),
            Table.BaseDataTable.platform_goods.name(),
            Table.BaseDataTable.gc_sku_extend.name(),
            Table.BaseDataTable.gc_config_sku.name(),
            Table.BaseDataTable.stock_goods.name(),
            Table.BaseDataTable.price_store.name(),
            Table.BaseDataTable.price_list.name(),
            Table.BaseDataTable.price_list_details.name(),
    };

    /**
     * es 写入数据赋值
     * @param operate
     * @param table
     * @param data
     * @return
     */
    public ElasticO2O transferO2O(ElasticO2O o2o, String operate, Table.BaseDataTable table, Model data) {
        if (Objects.isNull(o2o) || StringUtils.isBlank(operate) || Objects.isNull(table) || Objects.isNull(data)) {
            return null;
        }
        switch (table) {
            /*case merchant_goods_category_mapping:
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    o2o.setCategoryFrontend(Compute.categoryFrontend((MerchantGoodsCategoryMapping) data));
                }
                break;*/

            case gc_goods_overweight:
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    o2o.setIsOverweight(Compute.isOverweight((GoodsOverweight) data));
                }
                else {
                    o2o.setIsOverweight(0);
                }
                break;

            case organize_base:
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    o2o.setStoreStatus(Compute.storeStatus((OrganizeBase) data));
                    // o2o.setIsDtp(Compute.isDtp((OrganizeBase) data));
                }
                else {
                    o2o.setStoreStatus(null);
                    // o2o.setIsDtp(null);
                }
                break;

            case gc_goods_manual:
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    o2o.setIndications(Compute.indications((GoodsManual) data));
                    o2o.setDrugName(Compute.drugName((GoodsManual) data));
                    o2o.setRelativeSickness(Compute.relativeSickness((GoodsManual) data));
                    /*o2o.setEnName(Compute.enName((GoodsManual) data));
                    o2o.setPinyinName(Compute.pinyinName((GoodsManual) data));
                    o2o.setCureDisease(Compute.cureDisease((GoodsManual) data));
                    o2o.setPediatricUse(Compute.pediatricUse((GoodsManual) data));
                    o2o.setGeriatricUse(Compute.geriatricUse((GoodsManual) data));
                    o2o.setPregnancyAndNursingMothers(Compute.pregnancyAndNursingMothers((GoodsManual) data));
                    o2o.setOverDosage(Compute.overDosage((GoodsManual) data));*/
                }

                break;

            case pgc_store_info:
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    o2o.setProvinceCode(Compute.provinceCode((PgcStoreInfo) data));
                    o2o.setProvinceName(Compute.provinceName((PgcStoreInfo) data));
                    o2o.setCityCode(Compute.cityCode((PgcStoreInfo) data));
                    o2o.setCityName(Compute.cityName((PgcStoreInfo) data));
                    o2o.setAreaCode(Compute.areaCode((PgcStoreInfo) data));
                    o2o.setAreaName(Compute.areaName((PgcStoreInfo) data));
                }

                break;

            case pgc_store_info_increment:
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    if (LocationValidUtil.isLA(((PgcStoreInfoIncrement) data).getLatitude())
                            && LocationValidUtil.isLONG(((PgcStoreInfoIncrement) data).getLongitude())) {
                        o2o.setLocation(((PgcStoreInfoIncrement) data).getLatitude() + SymbolConstants.COMMA_EN
                                + ((PgcStoreInfoIncrement) data).getLongitude());
                    }
                    else {
                        log.info("Latitude or Longitude is not valid:{}", JSON.toJSONString(data));
                    }
                }
                break;

            case platform_goods:
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    o2o.setIswrOffShelf(Compute.iswrOffShelf((PlatformGoods) data));
                }
                else {
                    o2o.setIswrOffShelf(Compute.iswrOffShelf(null));
                }
                break;

            case gc_sku_extend:
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    o2o.setSpellWord(Compute.spellWord((SkuExtend) data));
                }
                break;

            /*case base_goods:
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    o2o.setCategoryOne(Compute.categoryOne((BaseGoods) data));
                    o2o.setCategoryTwo(Compute.categoryTwo((BaseGoods) data));
                    o2o.setCategoryThree(Compute.categoryThree((BaseGoods) data));
                    o2o.setCategoryFour(Compute.categoryFour((BaseGoods) data));
                    o2o.setCategoryFive(Compute.categoryFive((BaseGoods) data));
                }
                break;*/

            case gc_standard_goods_syncrds:
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    o2o.setBrand(Compute.brand((StandardGoodsSyncrds) data));
                    o2o.setSearchKeywords(Compute.searchKeywords((StandardGoodsSyncrds) data));
                    o2o.setIsStandard(true);
                    o2o.setStandardGoodsStatus(Compute.standardGoodsStatus((StandardGoodsSyncrds) data));
                    // o2o.setTradeCode(Compute.tradeCode((StandardGoodsSyncrds) data));
                }
                else {
                    o2o.setIsStandard(false);
                    o2o.setStandardGoodsStatus(false);
                }
                break;

            /*case gc_base_nootc:
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    o2o.setDrugType(Compute.drugType((BaseNootc) data));
                }
                break;*/
            case gc_config_sku:
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    o2o.setGoodsType(Compute.goodsType((ConfigSku) data));
                    o2o.setGoodsSubType(Compute.goodsSubType((ConfigSku) data));
                    o2o.setIsStandardOffShelf(Compute.isStandardOffShelf((ConfigSku) data));
                    o2o.setSkuAuditStatus(Compute.skuAuditStatus((ConfigSku) data));
                }
                else {
                    o2o.setIsStandardOffShelf(Compute.isStandardOffShelf(null));
                    o2o.setSkuAuditStatus(Compute.skuAuditStatus(null));
                }
                break;

            case stock_goods:
                if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                        || StringUtils.equals(CommonConstants.OPERATE_UPDATE, operate)) {
                    o2o.setIsVirtualStock(Compute.isVirtualStock((StockGoods) data));
                    o2o.setStockQuantity(Compute.stockQuantity((StockGoods) data));
                }
                else {
                    o2o.setIsVirtualStock(Compute.isVirtualStock(null));
                    o2o.setStockQuantity(Compute.stockQuantity(null));
                }
                break;

            default:
                return null;
        }
        return o2o;
    }
}
