package cn.wr.model;

import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * @author RWang
 * @Date 2022/5/11
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GcConfigSku {

    /** sku编号(同gc_ug_spu_goods.goods_no) */
    @JsonProperty("sku_no")
    private String skuNo;

    /** SKU标题 */
    private String title;

    /** 副标题/卖点介绍*/
    @JsonProperty("sub_title")
    private String subTitle;

    /** 连锁商品名称*/
    @JsonProperty("source_name")
    private String sourceName;

    /** gc_goods_spu.id*/
    @JsonProperty("spu_id")
    private long spuId;

    /** 商品类别/商品类型*/
    @JsonProperty("goods_type")
    private int goodsType;

    /***/
    @JsonProperty("goods_sub_type")
    private int goodsSubType;

    /** 批准文号*/
    @JsonProperty("approval_number")
    private String approvalNumber;

    /** 商品条码 */
    private String barcode;

    /** 亮点*/
    private String highlights;

    /** 外部编码*/
    @JsonProperty("external_code")
    private String externalCode;

    /** 规格名称*/
    @JsonProperty("spec_name")
    private String specName;

    /** 商品原价*/
    @JsonProperty("origin_price")
    private BigDecimal originPrice;

    /** 平台类目*/
    @JsonProperty("platform_cate")
    private String platformCate;

    /** 商户id*/
    @JsonProperty("merchant_id")
    private long merchantId;

    /** 商户名称*/
    @JsonProperty("merchant_name")
    private String merchantName;

    /** 实际发货商ID*/
    @JsonProperty("delivery_merchant_id")
    private int deliveryMerchantId;

    /** 实际发货商名称*/
    @JsonProperty("delivery_merchant_name")
    private String deliveryMerchantName;

    /** sku图文详情*/
    @JsonProperty("details_code")
    private String detailsCode;

    /** 结算扣率类型(1.协议扣率、2.商品独立扣率、3.固定金额)*/
    @JsonProperty("settlement_discount_type")
    private int settlementDiscountType;

    /** 结算扣率*/
    @JsonProperty("settlement_discount")
    private String settlementDiscount;

    /** 商品源id（根据goods_sub_type判断）*/
    @JsonProperty("origin_table_id")
    private String originTableId;

    /** 税编*/
    @JsonProperty("tax_ed")
    private String taxEd;

    /** 税编分类*/
    @JsonProperty("tax_class")
    private String taxClass;

    /** 税率*/
    @JsonProperty("tax_rate")
    private String taxRate;
    private String gmtCreated;
    private String gmtUpdated;

    /** 积分值*/
    private String integral;

    /** sku状态 0 正常  1 下架*/
    private String state;

    /** 库存*/
    private String stock;

    /** 库存单位*/
    @JsonProperty("stock_unit")
    private String stockUnit;

    /** 来源 0 正常添加 1连锁同步  2平台维护 3商家服务平台*/
    private String source;

    /** 限购类型（1.无限制，2.单用户限购）*/
    @JsonProperty("buy_limit_type")
    private String buyLimitType;

    /** 限购数量*/
    @JsonProperty("buy_limit_quantity")
    private String buyLimitQuantity;

    /** 门店id*/
    @JsonProperty("store_id")
    private String storeId;

    /** 门店名称*/
    @JsonProperty("store_name")
    private String storeName;

    /** 打包商品销售价*/
    @JsonProperty("bundle_price")
    private String bundlePrice;

    /** 预约页模板*/
    @JsonProperty("reservation_page_template")
    private String reservationPageTemplate;

    /** 服务有效期类型*/
    @JsonProperty("service_expiration_data_type")
    private String serviceExpirationDataType;

    /** 服务有效期*/
    @JsonProperty("service_expiration_date")
    private String serviceExpirationDate;

    /** 服务预约 （1.无需预约、2.自行联系商家预约、3.通过药联平台预约*/
    @JsonProperty("service_reservation")
    private String serviceReservation;

    /** 核销方式(1.自主核销、2.商家系统通知、3.第三方服务码/卡密)*/
    @JsonProperty("sale_verification_type")
    private String saleVerificationType;

    /** 审核状态 0未审核 1已审核*/
    @JsonProperty("audit_state")
    private String auditState;

    /** 管控状态 （0 正常，1 部分禁用 2 全国禁用*/
    @JsonProperty("control_status")
    private int controlStatus;
}
