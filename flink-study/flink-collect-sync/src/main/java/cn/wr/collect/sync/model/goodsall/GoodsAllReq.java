package cn.wr.collect.sync.model.goodsall;

import lombok.Data;


@Data
public class GoodsAllReq {

    /**
     * 操作
     */
    private String operate;
    /**
     * 表名
     */
    private String tableName;
    private Long organizeBaseMerchantId;
    private Long organizeBaseStoreId;
    private Integer pgcStoreInfoIncrementStoreId;

    private Long gcGoodsSpuAttrSyncrdsSpuId;

    private Integer gcPartnerStoresAllStoreId;

    private Integer gcPartnerStoresAllMerchantId;
    private String gcPartnerStoresAllChannel;

    private Integer partnerStoreGoodsDbId;

    private String partnerStoreGoodsGroupId;

    private String gcStandardGoodsSyncrdsTradeCode;



}
