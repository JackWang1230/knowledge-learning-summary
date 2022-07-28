package cn.wr.collect.sync.model.pricecompare;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceCompare {
    private Long id;
    //连锁id
    private Long merchantId;

    //门店id
    private Long storeId;

    //商品内码
    private String internalId;

    //价格中心销售价
    private BigDecimal priceSalePrice;

    //价格中心基础价
    private BigDecimal priceBasePrice;

    //连锁库销售价
    private BigDecimal partnerSalePrice;

    //连锁库基础价
    private BigDecimal partnerBasePrice;

}
