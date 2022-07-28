package cn.wr.collect.sync.constants;

/**
 * 字段刷新常量
 */
public interface FieldRefreshConstants {
    String[] refresh_partner_goods_fields = {
            /*"approvalNumber",
            "dbId",
            "internalId",
            "tradeCode",*/
    };
    String[] gc_goods_manual_fields = {
            "approvalNumber",
            "enName",
            "pinyinName",
            "indications",
            "cureDisease",
            "pediatricUse",
            "geriatricUse",
            "pregnancyAndNursingMothers",
            "overDosage",
            "drugName",
            "relativeSickness",
            "drugType"
    };

    String[] gc_base_nootc_fields = {
            "approvalNumber",
            "otcType"
    };

    String[] gc_standard_goods_syncrds_fields = {
            "tradeCode",
            "spuId",
            "urls",
            "brand",
            "searchKeywords",
            "status"
    };

    String[] gc_base_spu_img_fields = {
            "approvalNumber",
            "pic",
    };

    String[] partner_goods_img_fields = {
            "dbId",
            "internalId",
            "img",
    };

    String[] partner_goods_info_fields = {
            "dbId",
            "internalId",
            "images",
    };

    String[] gc_goods_dosage_fields = {
            "tradeCode",
            "prescriptionType",
    };


    String[] gc_partner_goods_gift_fields = {
        "dbId",
        "internalId"
    };

    String[] base_goods_fields = {
        "approvalNumber",
        "productName",
        "cateFive",
        "goodsType"
    };

    String[] merchant_goods_category_mapping_fields = {
        "tradeCode",
        "categoryCode"
    };

    String[] gc_goods_spu_attr_syncrds_fields = {
        "spuId",
        "attrId"
    };

    String[] gc_goods_attr_info_syncrds_fields = {
        "id"
    };

    String[] pgc_store_info_fields = {
//            "longitude","latitude"
    };

    String[] organize_base_fields = {
            "rootId",
            "organizationId",
            "isO2O",
            "netType"
    };

    String[] pgc_store_info_increment_fields = {
            "organizationId",
            "storeId",
            "longitude",
            "latitude"
    };

    String[] platform_goods_fields = {
            "merchantId",
            "storeId",
            "channel",
            "goodsInternalId",
            "status"
    };

    String[] gc_goods_overweight_fields = {
            "tradeCode",
            "isOverweight"
    };

    String[] gc_sku_extend_fields = {
            "keyWord",
            "skuNo",
            "title"
    };

//    String[] pgc_merchant_info_fields = {
//            "organizationId","merchantName","status","dbId","organizationName"
//    };
}
