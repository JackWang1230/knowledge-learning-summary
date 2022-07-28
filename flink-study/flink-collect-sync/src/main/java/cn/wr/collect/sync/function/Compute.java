package cn.wr.collect.sync.function;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.model.BasicTimeGoods;
import cn.wr.collect.sync.model.BasicTimeStoreGoods;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Correspond;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.gc.*;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.model.partner.PartnerGoodsInfo;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import cn.wr.collect.sync.model.partner.Partners;
import cn.wr.collect.sync.model.price.PriceListDetails;
import cn.wr.collect.sync.model.price.PriceStore;
import cn.wr.collect.sync.model.stock.StockGoods;
import cn.wr.collect.sync.utils.LocationValidUtil;
import cn.wr.collect.sync.utils.RegexUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.CommonConstants.*;
import static cn.wr.collect.sync.constants.SymbolConstants.HOR_LINE;

public class Compute {

    public static boolean merchantNotValid(PriceStore ps) {
        return Objects.isNull(ps) || Objects.isNull(ps.getOrganizationId()) || Objects.isNull(ps.getStoreId())
                || ps.getOrganizationId().equals(0L) || ps.getStoreId().equals(0L);
    }

    public static boolean merchantNotValid(PriceListDetails pld) {
        return Objects.isNull(pld) || Objects.isNull(pld.getOrganizationId()) || pld.getOrganizationId().equals(0L);
    }

    public static String skuCode(PartnerStoresAll stores, PartnerGoods goods) {
        return Objects.nonNull(stores) && Objects.nonNull(goods)
                ? stores.getMerchantId() + HOR_LINE + stores.getStoreId() + HOR_LINE + stores.getChannel() + HOR_LINE + goods.getInternalId()
                : null;
    }

    public static String skuCode(Long merchantId, Long storeId, String channel, String goodsInternalId) {
        return Objects.nonNull(merchantId) && Objects.nonNull(storeId) && Objects.nonNull(channel) && StringUtils.isNotBlank(goodsInternalId)
                ? merchantId + HOR_LINE + storeId + HOR_LINE + channel + HOR_LINE + goodsInternalId
                : null;
    }

    public static String skuCodeFromPrice(PriceStore priceStore, PriceListDetails priceListDetails) {
        if (merchantNotValid(priceStore) || merchantNotValid(priceListDetails)
                || StringUtils.isBlank(priceListDetails.getInternalId())) {
            return null;
        }

        return priceStore.getOrganizationId() + HOR_LINE + priceStore.getStoreId() + HOR_LINE + CHANNEL_O2O + HOR_LINE + priceListDetails.getInternalId();
    }

    public static String priceRedisKey(String skuCode, BigDecimal salePrice, BigDecimal basePrice) {
        return skuCode + HOR_LINE + salePrice + HOR_LINE + basePrice;
    }

    /*public static String categoryOne(BaseGoods baseGoods) {
        if (Objects.isNull(baseGoods)
                || StringUtils.isBlank(baseGoods.getCateFive())
                || baseGoods.getCateFive().length() < 1) {
            return null;
        }
        return baseGoods.getCateFive().substring(0, 1);
    }*/

    /*public static String categoryTwo(BaseGoods baseGoods) {
        if (Objects.isNull(baseGoods)
                || StringUtils.isBlank(baseGoods.getCateFive())
                || baseGoods.getCateFive().length() < 2) {
            return null;
        }
        return baseGoods.getCateFive().substring(0, 2);
    }*/

    /*public static String categoryThree(BaseGoods baseGoods) {
        if (Objects.isNull(baseGoods)
                || StringUtils.isBlank(baseGoods.getCateFive())
                || baseGoods.getCateFive().length() < 4) {
            return null;
        }
        return baseGoods.getCateFive().substring(0, 4);
    }*/

    /*public static String categoryFour(BaseGoods baseGoods) {
        if (Objects.isNull(baseGoods)
                || StringUtils.isBlank(baseGoods.getCateFive())
                || baseGoods.getCateFive().length() < 5) {
            return null;
        }
        return baseGoods.getCateFive().substring(0, 5);
    }*/

   /* public static String categoryFive(BaseGoods baseGoods) {
        if (Objects.isNull(baseGoods)
                || StringUtils.isBlank(baseGoods.getCateFive())
                || baseGoods.getCateFive().length() < 6) {
            return null;
        }
        return baseGoods.getCateFive().substring(0, 6);
    }*/

    /*public static String enName(GoodsManual manual) {
        return Objects.nonNull(manual) ? manual.getEnName() : null;
    }*/

    /*public static String pinyinName(GoodsManual manual) {
        return Objects.nonNull(manual) ? manual.getPinyinName() : null;
    }*/

    public static String indications(GoodsManual manual) {
        return Objects.nonNull(manual) ? manual.getIndications() : null;
    }

    /*public static String cureDisease(GoodsManual manual) {
        return Objects.nonNull(manual) ? manual.getCureDisease() : null;
    }*/

    /*public static String pediatricUse(GoodsManual manual) {
        return Objects.nonNull(manual) ? manual.getPediatricUse() : null;
    }*/

    /*public static String geriatricUse(GoodsManual manual) {
        return Objects.nonNull(manual) ? manual.getGeriatricUse() : null;
    }*/

    /*public static String pregnancyAndNursingMothers(GoodsManual manual) {
        return Objects.nonNull(manual) ? manual.getPregnancyAndNursingMothers() : null;
    }*/

    /*public static String overDosage(GoodsManual manual) {
        return Objects.nonNull(manual) ? manual.getOverDosage() : null;
    }*/

    public static String drugName(GoodsManual manual) {
        return Objects.nonNull(manual) ? manual.getDrugName() : null;
    }

    public static String relativeSickness(GoodsManual manual) {
        return Objects.nonNull(manual) ? manual.getRelativeSickness() : null;
    }

    public static String commonName(GoodsManual goodsManual, BaseGoods baseGoods, PartnerGoods goods) {
        if (Objects.nonNull(goodsManual) && StringUtils.isNotBlank(goodsManual.getCommonName())) {
            return goodsManual.getCommonName();
        }
        if (Objects.nonNull(baseGoods) && StringUtils.isNotBlank(baseGoods.getProductName())) {
            return baseGoods.getProductName();
        }
        if (Objects.nonNull(goods)) {
            return goods.getCommonName();
        }
        return null;
    }

    public static Integer dbId(PartnerGoods goods) {
        return Objects.nonNull(goods) ? goods.getDbId(): null;
    }

    public static String goodsInternalId(PartnerGoods goods) {
        return Objects.nonNull(goods) ? goods.getInternalId(): null;
    }

    public static String approvalNumber(PartnerGoods goods, GoodsSpu goodsSpu) {
        return Objects.nonNull(goodsSpu) && StringUtils.isNotBlank(goodsSpu.getApprovalNumber())
                ? goodsSpu.getApprovalNumber()
                : goods.getApprovalNumber();
    }

    public static String approvalNumber(String approvalNumber, GoodsSpu goodsSpu) {
        return Objects.nonNull(goodsSpu) && StringUtils.isNotBlank(goodsSpu.getApprovalNumber())
                ? goodsSpu.getApprovalNumber()
                : approvalNumber;
    }

    public static String realTradeCode(PartnerGoods goods) {
        return Objects.nonNull(goods) ? goods.getTradeCode(): null;
    }

    public static String tradeCode(StandardGoodsSyncrds standard, Integer merchantId, String goodsInternalId) {
        return Objects.nonNull(standard) ? standard.getTradeCode() : merchantId + goodsInternalId;
    }

    /*public static String tradeCode(StandardGoodsSyncrds standard) {
        return Objects.nonNull(standard) ? standard.getTradeCode() : null;
    }*/

    public static String tradeCode(ConfigSku configSku, StandardGoodsSyncrds standard) {
        // goods.trade_code <> gc_config_sku.trade_code
        // goods.trade_code is null
        // gc_config_sku.trade_code is null
        // gc_standard_goods.trade_code is null

        // 优先取config_sku 自建码
        if (Objects.nonNull(configSku) && StringUtils.isNotBlank(configSku.getBarcode())) {
            return configSku.getBarcode();
        }
        // 再取标准商品库
        if (Objects.nonNull(standard) && StringUtils.isNotBlank(standard.getTradeCode())) {
            return standard.getTradeCode();
        }
        // 最后取搜索自建码
        return null;
    }

    public static String tradeCode(String tradeCode, Integer merchantId, String goodsInternalId) {
        return StringUtils.isNotBlank(tradeCode) ? tradeCode : merchantId + goodsInternalId;
    }

    public static String tradeCodeLess8(String tradeCode, Integer merchantId, String goodsInternalId) {
        return StringUtils.isNotBlank(tradeCode) && tradeCode.length() >= 8 ? tradeCode : merchantId + goodsInternalId;
    }

    public static String tradeCodeLess8(StandardGoodsSyncrds standard) {
        return Objects.nonNull(standard) && StringUtils.isNotBlank(standard.getTradeCode())
                && standard.getTradeCode().length() >= 8 ? standard.getTradeCode() : null;
    }


    public static boolean isStandard(StandardGoodsSyncrds standard) {
        return Objects.nonNull(standard);
    }

    public static String img(StandardGoodsSyncrds standard) {
        return Objects.nonNull(standard) ? standard.getUrls() : null;
    }

    public static String img(PartnerGoodsImg goodsImg) {
        return Objects.nonNull(goodsImg) && StringUtils.isNotBlank(goodsImg.getImg()) ? goodsImg.getImg() : null;
    }

    public static String img(BaseSpuImg baseSpuImg) {
        return Objects.nonNull(baseSpuImg) ? baseSpuImg.getPic() : null;
    }

    public static String img(PartnerGoodsInfo goodsInfo) {
        return Objects.nonNull(goodsInfo) && StringUtils.isNotBlank(goodsInfo.getImages()) ? goodsInfo.getImages() : null;
    }

    public static String brand(StandardGoodsSyncrds standard) {
        return Objects.nonNull(standard) ? standard.getBrand() : null;
    }

    public static String searchKeywords(StandardGoodsSyncrds standard) {
        return Objects.nonNull(standard) ? standard.getSearchKeywords() : null;
    }

    public static String realCommonName(PartnerGoods goods) {
        return Objects.nonNull(goods) ? goods.getCommonName(): null;
    }

    public static BigDecimal basePrice(PartnerGoods goods) {
        return Objects.nonNull(goods) ? goods.getPrice() : null;
    }

    public static BigDecimal salePrice(PartnerStoreGoods storeGoods) {
        return Objects.nonNull(storeGoods) ? storeGoods.getPrice() : null;
    }

    public static BigDecimal salePrice(PartnerGoods goods) {
        return Objects.nonNull(goods) ? goods.getPrice() : null;
    }

    public static String provinceCode(PgcStoreInfo storeInfo) {
        return Objects.nonNull(storeInfo) && Objects.nonNull(storeInfo.getProvinceId())
                ? String.valueOf(storeInfo.getProvinceId())
                : null;
    }

    public static String provinceName(PgcStoreInfo storeInfo) {
        return Objects.nonNull(storeInfo)
                ? storeInfo.getProvinceName()
                : null;
    }

    public static String cityCode(PgcStoreInfo storeInfo) {
        return Objects.nonNull(storeInfo) && Objects.nonNull(storeInfo.getCityId())
                ? String.valueOf(storeInfo.getCityId())
                : null;
    }

    public static String cityName(PgcStoreInfo storeInfo) {
        return Objects.nonNull(storeInfo)
                ? storeInfo.getCityName()
                : null;
    }

    public static String areaCode(PgcStoreInfo storeInfo) {
        return Objects.nonNull(storeInfo) && Objects.nonNull(storeInfo.getDistrictId())
                ? String.valueOf(storeInfo.getDistrictId())
                : null;
    }

    public static String areaName(PgcStoreInfo storeInfo) {
        return Objects.nonNull(storeInfo)
                ? storeInfo.getDistrictName()
                : null;
    }

    public static String form(PartnerGoods goods) {
        return Objects.nonNull(goods) ? goods.getForm() : null;
    }

    public static String manufacturer(PartnerGoods goods) {
        return Objects.nonNull(goods) ? goods.getManufacturer() : null;
    }

    public static String pack(PartnerGoods goods) {
        return Objects.nonNull(goods) ? goods.getPack() : null;
    }

    /*public static Integer drugType(BaseNootc nootc) {
        return Objects.nonNull(nootc) ? nootc.getOtcType() : null;
    }*/

    /*public static String geohash(PartnerStoresAll stores) {
        return Objects.nonNull(stores) ? stores.getHashSeven() : null;
    }*/

    public static String channel(PartnerStoresAll stores) {
        return Objects.nonNull(stores) ? stores.getChannel() : null;
    }
    public static Integer merchantId(PartnerStoresAll stores) {
        return Objects.nonNull(stores) ? stores.getMerchantId() : null;
    }
    public static Integer storeId(PartnerStoresAll stores) {
        return Objects.nonNull(stores) ? stores.getStoreId() : null;
    }

    public static boolean iswrOffShelf(PlatformGoods platformGoods) {
        return Objects.nonNull(platformGoods) && Objects.equals(IS_WR_OFF_SHELF_OFF, platformGoods.getStatus());
    }

    /*public static boolean isOffShelf(PartnerGoodsGift goodsGift, Boolean isOffShelf) {
        if (Objects.isNull(isOffShelf)) {
            return Objects.nonNull(goodsGift) && Objects.nonNull(goodsGift.getId());
        }
        return (Objects.nonNull(goodsGift) && Objects.nonNull(goodsGift.getId())) || isOffShelf;
    }*/

    /*public static boolean isOffShelf(PartnerGoodsGift goodsGift, String isOffShelf) {
        if (StringUtils.isBlank(isOffShelf)) {
            return Objects.nonNull(goodsGift) && Objects.nonNull(goodsGift.getId());
        }
        return (Objects.nonNull(goodsGift) && Objects.nonNull(goodsGift.getId())) || StringUtils.equals("1", isOffShelf);
    }*/

    public static boolean isOffShelf(PartnerGoods goods, ElasticO2O o2o) {
        return GOODS_STATUS_OFF.equals(goods.getStatus())
                || (Objects.nonNull(o2o.getIsOffShelf()) && o2o.getIsOffShelf());
    }

    public static boolean isOffShelf(PartnerGoods goods, PartnerStoreGoods storeGoods, ElasticO2O o2o) {
        return GOODS_STATUS_OFF.equals(goods.getStatus())
                || Objects.nonNull(storeGoods) && GOODS_STATUS_OFF.equals(storeGoods.getStatus())
                || (Objects.nonNull(o2o.getIsOffShelf()) && o2o.getIsOffShelf());
    }

    public static boolean isOffShelf(StockGoods stockGoods, PartnerGoods goods, PartnerStoreGoods storeGoods) {
        if (Objects.nonNull(stockGoods) && Objects.nonNull(stockGoods.getCenterState())) {
            return !CENTER_STATE_ON.equals(stockGoods.getCenterState());
        }
        if (Objects.nonNull(stockGoods) && Objects.nonNull(stockGoods.getSaleState())) {
            return GOODS_STATUS_OFF.equals(stockGoods.getSaleState());
        }
        return Objects.isNull(goods) || GOODS_STATUS_OFF.equals(goods.getStatus())
                || Objects.isNull(storeGoods) || GOODS_STATUS_OFF.equals(storeGoods.getStatus());
    }

    /**
     * 是否Dtp门店
     * @param base
     * @return
     */
    public static Integer isDtpStore(OrganizeBase base) {
        if (Objects.isNull(base)) {
            return CommonConstants.IS_DTP_STORE_FALSE;
        }
        if (CommonConstants.NET_TYPE_2.equals(base.getNetType())) {
            return CommonConstants.IS_DTP_STORE_TRUE;
        }
        return CommonConstants.IS_DTP_STORE_FALSE;
    }

    public static Integer isDtp(Collection<String> attrIds) {
        return attrIds.stream().anyMatch(item ->
                Arrays.stream(DTP_ATTR_IDS).anyMatch(id -> item.equals(String.valueOf(id)))) ? 1 : 0;
    }

    public static Integer isDtpLong(Collection<Long> attrIds) {
        return attrIds.stream().anyMatch(item ->
                Arrays.asList(DTP_ATTR_IDS).contains(item)) ? 1 : 0;
    }

    public static boolean isPrescription(BaseNootc nootc, GoodsDosage dosage) {
        return (Objects.nonNull(dosage) && Objects.nonNull(dosage.getPrescriptionType()) && (1 == dosage.getPrescriptionType() || 5 == dosage.getPrescriptionType()))
                || ((Objects.isNull(dosage) || Objects.isNull(dosage.getPrescriptionType()))
                    && Objects.nonNull(nootc) && Objects.nonNull(nootc.getOtcType()) && 1 == nootc.getOtcType());
    }

    public static Boolean isPrescription(Collection<String> attrIds) {
        return attrIds.stream().anyMatch(item ->
                Arrays.stream(IS_PRESCRIPTION_IDS).anyMatch(id -> item.equals(String.valueOf(id)))) ? true : false;
    }

    public static boolean isEphedrine(GoodsAttrInfoSyncrds attr) {
        return Objects.nonNull(attr) && Arrays.stream(CommonConstants.EPHEDRINE_ATTR_IDS).anyMatch(i -> i.equals(attr.getId()));
    }

    public static boolean isDouble(GoodsAttrInfoSyncrds attr) {
        return Objects.nonNull(attr) && Arrays.stream(CommonConstants.DOUBLE_ATTR_IDS).anyMatch(i -> i.equals(attr.getId()));
    }

    /*public static String categoryFrontend(MerchantGoodsCategoryMapping mgcm) {
        return Objects.nonNull(mgcm) ? mgcm.getCategoryCode() : null;
    }*/

    public static Integer isOverweight(GoodsOverweight goodsOverweight) {
        return Objects.nonNull(goodsOverweight) ? goodsOverweight.getIsOverweight() : 0;
    }

    public static boolean standardGoodsStatus(StandardGoodsSyncrds syncrds) {
        return Objects.nonNull(syncrds) && STANDARD_GOODS_STATUS_PASS.equals(syncrds.getStatus());
    }

    public static BigDecimal salesVolume(GoodsSales goodsSales) {
        return Objects.isNull(goodsSales) || Objects.isNull(goodsSales.getQuantity())
                ? new BigDecimal("0")
                : new BigDecimal(Double.toString(goodsSales.getQuantity()));
    }

    public static BigDecimal fullSalesVolume(GoodsFullSales goodsFullSales) {
        return Objects.isNull(goodsFullSales) || Objects.isNull(goodsFullSales.getQuantity())
                ? new BigDecimal("0")
                : new BigDecimal(Double.toString(goodsFullSales.getQuantity()));
    }

    public static String location(PgcStoreInfoIncrement info) {
        return (Objects.nonNull(info)
                && LocationValidUtil.isLA(info.getLatitude()) && LocationValidUtil.isLONG(info.getLongitude()))
                ? info.getLatitude() + "," + info.getLongitude()
                : null;
    }

    public static Integer storeStatus(OrganizeBase base) {
        return Objects.nonNull(base)
                ? base.getIsO2O()
                : null;
    }

    public static String spellWord(SkuExtend skuExtend) {
        return Objects.nonNull(skuExtend) ? skuExtend.getKeyWord() : null;
    }

    public static List<Long> cates(List<Long> cates) {
        if (CollectionUtils.isEmpty(cates)) {
            return Collections.emptyList();
        }
        return cates;
    }

    public static List<String> cateIds(List<Long> cates) {
        if (CollectionUtils.isEmpty(cates)) {
            return Collections.emptyList();
        }
        return cates.stream().map(String::valueOf).collect(Collectors.toList());
    }

    public static List<Long> attrs(GoodsAttrInfoSyncrds attr) {
        if (!ATTR_DELETED_ENABLE.equals(attr.getDeleted())) {
            return Collections.emptyList();
        }
        List<Long> attrs = new ArrayList<>();
        attrs.add(attr.getId());
        if (StringUtils.isNotBlank(attr.getPids())) {
            String[] split = attr.getPids().split(SymbolConstants.COMMA_EN);
            if (split.length > 0) {
                attrs.addAll(Arrays.stream(split).filter(StringUtils::isNotBlank)
                        .map(Long::valueOf).collect(Collectors.toSet()));
            }
        }
        return attrs;
    }

    public static List<Long> attrs(List<Long> attrs) {
        if (CollectionUtils.isEmpty(attrs)) {
            return Collections.emptyList();
        }
        return attrs;
    }

    public static List<String> attrIds(List<Long> attrs) {
        if (CollectionUtils.isEmpty(attrs)) {
            return Collections.emptyList();
        }
        return attrs.stream().map(String::valueOf).collect(Collectors.toList());
    }

    public static Integer goodsType(ConfigSku configSku) {
        return Objects.nonNull(configSku) ? configSku.getGoodsType() : null;
    }

    public static Integer goodsSubType(ConfigSku configSku) {
        return Objects.nonNull(configSku) ? configSku.getGoodsSubType() : null;
    }

    public static Boolean isStandardOffShelf(ConfigSku configSku) {
        return Objects.nonNull(configSku) && Objects.nonNull(configSku.getState())
                && IS_STANDARD_OFF_SHELF.equals(configSku.getState());
    }

    public static Integer dbId(Partners partners) {
        if (Objects.isNull(partners)) {
            return null;
        }
        // cooperation 字段 o2o 开头的去除
        if (StringUtils.isNotBlank(partners.getCooperation())
            && partners.getCooperation().startsWith("o2o")) {
            return null;
        }
        String dbname = partners.getDbname();
        if (!dbname.startsWith("partner_common_")) {
            return null;
        }
        String dbId = dbname.substring(15);
        if (RegexUtil.isNumeric(dbId)) {
            return Integer.valueOf(dbId);
        }
        return null;
    }

    public static Integer merchantId(Partners partners) {
        if (Objects.isNull(partners) || Objects.isNull(partners.getOrganizationId())
            || partners.getOrganizationId().equals(0)) {
            return null;
        }
        return partners.getOrganizationId();
    }

    public static String fullName(Partners partners) {
        return Objects.isNull(partners) || StringUtils.isBlank(partners.getFullName()) ? null : partners.getFullName();
    }

    public static Integer skuAuditStatus(ConfigSku configSku) {
        return Objects.isNull(configSku) || Objects.isNull(configSku.getAuditState()) ? 0 : configSku.getAuditState();
    }

    public static BigDecimal stockQuantity(StockGoods stockGoods) {
        if (Objects.isNull(stockGoods)) {
            return new BigDecimal("0");
        }
        if (STOCK_VIRTUAL_ON.equals(stockGoods.getVirtualOn())) {
            return Objects.nonNull(stockGoods.getVirtualQuantity()) ? stockGoods.getVirtualQuantity() : new BigDecimal("0");
        }
        return Objects.nonNull(stockGoods.getQuantity()) ? stockGoods.getQuantity() : new BigDecimal("0");
    }

    public static Integer isVirtualStock(StockGoods stockGoods) {
        return Objects.isNull(stockGoods) || Objects.isNull(stockGoods.getVirtualOn()) ? STOCK_VIRTUAL_OFF : stockGoods.getVirtualOn();
    }

    public static String operate(String operate) {
        if (StringUtils.isBlank(operate)) {
            return null;
        }
        switch (operate) {
            case OPERATE_INSERT:
            case OPERATE_UPDATE_INSERT:
                return OPERATE_SHORT_INSERT;
            case OPERATE_UPDATE:
                return OPERATE_SHORT_UPDATE;
            case OPERATE_DELETE:
            case OPERATE_UPDATE_DELETE:
                return OPERATE_SHORT_DELETE;
            default:
                return null;
        }
    }


    /**
     * 获取数据变更情况 0-无关字段变更 1-仅单字段变更 2-仅组合字段变更 3-单字段&组合字段变更 4-key变更
     * @param operate
     * @param tableName
     * @param modFieldList
     * @return
     */
    public static int computeCondition(String operate, String tableName, List<String> modFieldList) {
        if (StringUtils.equals(CommonConstants.OPERATE_INSERT, operate)
                || StringUtils.equals(CommonConstants.OPERATE_DELETE, operate)
                || StringUtils.equals(CommonConstants.OPERATE_UPDATE_DELETE, operate)
                || StringUtils.equals(CommonConstants.OPERATE_UPDATE_INSERT, operate)) {
            return CommonConstants.MOD_FIELD_KEY;
        }

        if (CollectionUtils.isEmpty(modFieldList)) {
            return CommonConstants.MOD_FIELD_NONE;
        }

        boolean single = false;
        boolean multi = false;
        Class<? extends Model> clazz = Table.BaseDataTable.getClazz(tableName);
        if (Objects.isNull(clazz)) {
            return CommonConstants.MOD_FIELD_NONE;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Correspond.class) || !field.isAnnotationPresent(Column.class)) {
                continue;
            }

            Column column = field.getDeclaredAnnotation(Column.class);
            boolean contains = modFieldList.contains(column.name());
            if (!contains) {
                continue;
            }

            Correspond correspond = field.getDeclaredAnnotation(Correspond.class);
            if (Correspond.Type.Key == correspond.type() || Correspond.Type.Both == correspond.type()) {
                return CommonConstants.MOD_FIELD_KEY;
            }
            if (Correspond.Mode.Single == correspond.mode()) {
                single = true;
            }
            if (Correspond.Mode.Multi == correspond.mode()) {
                multi = true;
            }
            if (single && multi) {
                break;
            }
        }

        // 单字段&组合字段变更
        if (multi && single) {
            return CommonConstants.MOD_FIELD_BOTH;
        }
        // 多字段变更
        if (multi) {
            return CommonConstants.MOD_FIELD_MULTI;
        }
        // 单字段变更
        if (single) {
            return CommonConstants.MOD_FIELD_SINGLE;
        }
        // 无关字段变更
        return CommonConstants.MOD_FIELD_NONE;
    }

    public static boolean isFilterDbId(Model model, List<Integer> filterDbIdList) {
        if (model instanceof BasicTimeGoods) {
            return filterDbIdList.contains(((BasicTimeGoods) model).getDbId());
        }
        else if (model instanceof BasicTimeStoreGoods) {
            return filterDbIdList.contains(((BasicTimeStoreGoods) model).getDbId());
        }
        else if (model instanceof PartnerGoods) {
            return filterDbIdList.contains(((PartnerGoods) model).getDbId());
        }
        else if (model instanceof PartnerStoreGoods) {
            return filterDbIdList.contains(((PartnerStoreGoods) model).getDbId());
        }
        return true;
    }

    public static String goodsHbsKey(PartnerGoods goods) {
        if (Objects.isNull(goods) || Objects.isNull(goods.getDbId()) || StringUtils.isBlank(goods.getInternalId())) {
            return null;
        }
        return goods.getDbId() + HOR_LINE + goods.getInternalId();
    }

    public static String storeGoodsHbsKey(PartnerStoreGoods storeGoods) {
        if (Objects.isNull(storeGoods) || Objects.isNull(storeGoods.getDbId())
                || StringUtils.isBlank(storeGoods.getGroupId())
                || StringUtils.isBlank(storeGoods.getGoodsInternalId())) {
            return null;
        }
        return storeGoods.getDbId() + HOR_LINE + storeGoods.getGroupId() + HOR_LINE + storeGoods.getGoodsInternalId();
    }

    public static String stockGoodsHbsKey(StockGoods stockGoods) {
        if (Objects.isNull(stockGoods) || Objects.isNull(stockGoods.getMerchantId())
                || Objects.isNull(stockGoods.getStoreId())
                || StringUtils.isBlank(stockGoods.getInternalId())) {
            return null;
        }
        return stockGoods.getMerchantId() + HOR_LINE + stockGoods.getStoreId() + HOR_LINE + stockGoods.getInternalId();
    }


    public static String priceListDetailsHbsKey(StockGoods stockGoods) {
        if (Objects.isNull(stockGoods) || Objects.isNull(stockGoods.getMerchantId())
                || Objects.isNull(stockGoods.getStoreId())
                || StringUtils.isBlank(stockGoods.getInternalId())) {
            return null;
        }
        return stockGoods.getMerchantId() + HOR_LINE + stockGoods.getStoreId() + HOR_LINE + stockGoods.getInternalId();
    }





    public static void main(String[] args) {
        /*Integer goodsStatus = 1;
        Integer storeGoodsStatus = 0;
        boolean status = true;
        PartnerStoreGoods storeGoods = null;
        System.out.println(GOODS_STATUS_OFF.equals(goodsStatus)
                || Objects.nonNull(storeGoods) && GOODS_STATUS_OFF.equals(storeGoodsStatus)
                || status);*/
        String dbname = "partner_common_1078";
        System.out.println(dbname.substring(15));
    }
}
