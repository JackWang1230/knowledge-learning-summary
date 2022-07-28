package cn.wr.collect.sync.model.welfare;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class WelfareGoods implements Serializable {
    private static final long serialVersionUID = 3416396218597803147L;
    /**
     * id
     */
//    private Long id;
    /**
     * sku编号
     */
    private String skuNo;
    /**
     * 返回不同商品的对应的编号
     */
//    private String refNo;
    /**
     * gc_goods_spu.id
     */
//    private Long spuId;
    /**
     * 商品类别/商品类型
     */
    private Integer goodsType;
    /**
     * 商品子类型
     */
    private Integer goodsSubType;
    /**
     * SKU标题
     */
    private String title;
    /**
     * 副标题/卖点介绍
     */
    private String subTitle;
    /**
     * 亮点
     */
//    public String highlights;
    /**
     * 外部编码
     */
//    public String externalCode;
    /**
     * 规格名称
     */
    public String specName;
    /**
     * 商品条码
     */
//    public String barcode;
    /**
     * 商品原价
     */
    public BigDecimal originPrice;
    /**
     * 平台类目
     */
//    public String platformCate;
    /**
     * 库存
     */
//    public BigDecimal stock;
    /**
     * 商户ID
     */
//    private Long merchantId;
    /**
     * 商户名称
     */
//    private String merchantName;
    /**
     * sku图文详情
     */
//    private String detailsCode;
    /**
     * 结算扣率类型(1.协议扣率、2.商品独立扣率、3.固定金额)
     */
//    private Integer settlementDiscountType;
    /**
     * 结算扣率
     */
//    private BigDecimal settlementDiscount;
    /**
     * 商品源id（根据goods_sub_type判断）：增值服务id/优惠券id/会员方案id
     */
//    private String originTableId;
    /**
     * 积分值
     */
//    private BigDecimal integral;
    /**
     * 批准文号
     */
//    private String approvalNumber;
    /**
     * 实际发货商ID
     */
//    private Integer deliveryMerchantId;
    /**
     * 实际发货商名称
     */
//    private String deliveryMerchantName;
    /**
     * 原商品
     */
    // private Object item;
    /**
     * sku或渠道图文信息
     */
    private RedisGoodsDetail redisGoodsDetails;
    /**
     * 打包商品
     */
    // private List<Object> items;
    /**
     * 商品渠道信息
     */
    private List<WelfareChannel> channelList;

    /**
     * spu
     */
    private RedisGoodsSpu redisGoodsSpu;
    /**
     * 限购规则
     */
    // private List<Object> redisBuyLimit;
    /**
     * 属性标签
     */
    // private List<Object> redisAttrs;
}
