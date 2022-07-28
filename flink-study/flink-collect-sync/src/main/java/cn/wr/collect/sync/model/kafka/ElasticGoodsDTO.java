package cn.wr.collect.sync.model.kafka;

import cn.wr.collect.sync.model.ElasticO2O;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElasticGoodsDTO implements Serializable {
    private static final long serialVersionUID = 1711651752098263388L;

    /**
     * 连锁id
     */
    private Integer merchantId;
    /**
     * 门店id
     */
    private Integer storeId;
    /**
     * 渠道
     */
    private String channel;
    /**
     * 商品内码
     */
    private String goodsInternalId;
    /**
     * 标准条码
     */
    private String tradeCode;
    /**
     * 位置
     */
    private String location;
    /**
     * 门店状态
     */
    private Integer storeStatus;
    /**
     * 是否DTP商品 0-否 1-是
     */
    private Integer isDtp;
    /**
     * 是否麻黄碱 0:不含麻黄碱 1:含麻黄碱
     */
    private Boolean isEphedrine;
    /**
     * 是否销售 0否 1是
     */
    private Boolean isOffShelf;
    /**
     * es中增加运营上下架字段（is_wr_off_shelf）
     * 同步O2O运营配置的状态字段。默认 false 上架
     */
    private Boolean iswrOffShelf;
    /**
     * 是否超重（0-否 1-是）
     */
    private Integer isOverweight;
    /**
     * 销售价格
     */
    private BigDecimal salePrice;
    /**
     * 是否标准商品 false/true
     */
    private Boolean isStandard;
    /**
     * 是否DTP门店 0-否 1-是
     */
    private Integer isDtpStore;

    public ElasticGoodsDTO transfer(ElasticO2O o2o) {
        this.merchantId = o2o.getMerchantId();
        this.storeId = o2o.getStoreId();
        this.channel = o2o.getChannel();
        this.goodsInternalId = o2o.getGoodsInternalId();
        this.tradeCode = o2o.getRealTradeCode();
        this.location = o2o.getLocation();
        this.storeStatus = o2o.getStoreStatus();
        this.isDtp = o2o.getIsDtp();
        this.isEphedrine = o2o.getIsEphedrine();
        this.isOffShelf = o2o.getIsOffShelf();
        this.iswrOffShelf = o2o.getIswrOffShelf();
        this.isOverweight = o2o.getIsOverweight();
        this.salePrice = o2o.getSalePrice();
        this.isStandard = o2o.getIsStandard();
        this.isDtpStore = o2o.getIsDtpStore();
        return this;
    }
}
