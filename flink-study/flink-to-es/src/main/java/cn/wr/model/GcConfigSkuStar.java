package cn.wr.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * @author RWang
 * @Date 2022/5/12
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GcConfigSkuStar extends GcConfigSku {

    /** sku编号(同gc_ug_spu_goods.goods_no) */
    @JsonProperty("sku_no")
    @JSONField(name = "sku_no")
    private String skuNo;

    /** SKU标题 */
    @JSONField(name = "title")
    private String title;

    /** 副标题/卖点介绍*/
    @JsonProperty("sub_title")
    @JSONField(name = "sub_title")
    private String subTitle;

    /** 连锁商品名称*/
    @JsonProperty("source_name")
    @JSONField(name = "source_name")
    private String sourceName;

    /** gc_goods_spu.id*/
    @JsonProperty("spu_id")
    @JSONField(name = "spu_id")
    private long spuId;

    /** 商品类别/商品类型*/
    @JsonProperty("goods_type")
    @JSONField(name = "goods_type")
    private int goodsType;

    /***/
    @JsonProperty("goods_sub_type")
    @JSONField(name = "goods_sub_type")
    private int goodsSubType;

    /** 批准文号*/
    @JsonProperty("approval_number")
    @JSONField(name = "approval_number")
    private String approvalNumber;

    /** 商品条码 */
    @JSONField(name = "barcode")
    private String barcode;

    /** 亮点*/
    @JSONField(name = "highlights")
    private String highlights;

    /** 外部编码*/
    @JsonProperty("external_code")
    @JSONField(name = "external_code")
    private String externalCode;

    /** 规格名称*/
    @JsonProperty("spec_name")
    @JSONField(name = "spec_name")
    private String specName;

    /** 商品原价*/
    @JsonProperty("origin_price")
    @JSONField(name = "origin_price")
    private BigDecimal originPrice;

    /** 平台类目*/
    @JsonProperty("platform_cate")
    @JSONField(name = "platform_cate")
    private String platformCate;

    /** 商户id*/
    @JsonProperty("merchant_id")
    @JSONField(name = "merchant_id")
    private long merchantId;

    /** 商户名称*/
    @JsonProperty("merchant_name")
    @JSONField(name = "merchant_name")
    private String merchantName;

    /** 实际发货商ID*/
    @JsonProperty("delivery_merchant_id")
    @JSONField(name = "delivery_merchant_id")
    private int deliveryMerchantId;

    /** 实际发货商名称*/
    @JsonProperty("delivery_merchant_name")
    @JSONField(name = "delivery_merchant_name")
    private String deliveryMerchantName;

    /** sku图文详情*/
    @JsonProperty("details_code")
    @JSONField(name = "details_code")
    private String detailsCode;

    /** 结算扣率类型(1.协议扣率、2.商品独立扣率、3.固定金额)*/
    @JsonProperty("settlement_discount_type")
    @JSONField(name = "settlement_discount_type")
    private int settlementDiscountType;

    /** 结算扣率*/
    @JsonProperty("settlement_discount")
    @JSONField(name = "settlement_discount")
    private String settlementDiscount;

    /** 商品源id（根据goods_sub_type判断）*/
    @JsonProperty("origin_table_id")
    @JSONField(name = "origin_table_id")
    private String originTableId;

    /** 税编*/
    @JsonProperty("tax_ed")
    @JSONField(name = "tax_ed")
    private String taxEd;

    /** 税编分类*/
    @JsonProperty("tax_class")
    @JSONField(name = "tax_class")
    private String taxClass;

    /** 税率*/
    @JsonProperty("tax_rate")
    @JSONField(name = "tax_rate")
    private String taxRate;
    private String gmtCreated;
    private String gmtUpdated;

    /** 积分值*/
    @JSONField(name = "integral")
    private String integral;

    /** sku状态 0 正常  1 下架*/
    @JSONField(name = "state")
    private String state;

    /** 库存*/
    @JSONField(name = "stock")
    private String stock;

    /** 库存单位*/
    @JsonProperty("stock_unit")
    @JSONField(name = "stock_unit")
    private String stockUnit;

    /** 来源 0 正常添加 1连锁同步  2平台维护 3商家服务平台*/
    @JSONField(name = "source")
    private String source;

    /** 限购类型（1.无限制，2.单用户限购）*/
    @JsonProperty("buy_limit_type")
    @JSONField(name = "buy_limit_type")
    private String buyLimitType;

    /** 限购数量*/
    @JsonProperty("buy_limit_quantity")
    @JSONField(name = "buy_limit_quantity")
    private String buyLimitQuantity;

    /** 门店id*/
    @JsonProperty("store_id")
    @JSONField(name = "store_id")
    private String storeId;

    /** 门店名称*/
    @JsonProperty("store_name")
    @JSONField(name = "store_name")
    private String storeName;

    /** 打包商品销售价*/
    @JsonProperty("bundle_price")
    @JSONField(name = "bundle_price")
    private String bundlePrice;

    /** 预约页模板*/
    @JsonProperty("reservation_page_template")
    @JSONField(name = "reservation_page_template")
    private String reservationPageTemplate;

    /** 服务有效期类型*/
    @JsonProperty("service_expiration_data_type")
    @JSONField(name = "service_expiration_data_type")
    private String serviceExpirationDataType;

    /** 服务有效期*/
    @JsonProperty("service_expiration_date")
    @JSONField(name = "service_expiration_date")
    private String serviceExpirationDate;

    /** 服务预约 （1.无需预约、2.自行联系商家预约、3.通过药联平台预约*/
    @JsonProperty("service_reservation")
    @JSONField(name = "service_reservation")
    private String serviceReservation;

    /** 核销方式(1.自主核销、2.商家系统通知、3.第三方服务码/卡密)*/
    @JsonProperty("sale_verification_type")
    @JSONField(name = "sale_verification_type")
    private String saleVerificationType;

    /** 审核状态 0未审核 1已审核*/
    @JsonProperty("audit_state")
    @JSONField(name = "audit_state")
    private String auditState;

    /** 管控状态*/
    @JsonProperty("control_status")
    @JSONField(name = "control_status")
    private int controlStatus;

    /** 连锁商品名称是否有值（1 有值，0 无值）*/
    @JsonProperty("is_goods_name")
    @JSONField(name = "is_goods_name")
    private int isGoodsName;

    /** 是否符合 批准文号：字符类型：汉字、英文、数字 ｜ 英文、数字（1符合 ，0 不符合）*/
    @JsonProperty("is_approval_number")
    @JSONField(name = "is_approval_number")
    private int isApprovalNumber;

    /** 是否符合 商品条码：字符类型：纯数字 字符长度：8、9、10、12、13、14（1 符合，0 不符合）*/
    @JsonProperty("isTradecode")
    @JSONField(name = "isTradecode")
    private int isTradecode;

    /** 商品规格字段是否存在值（1 有值，0 无值）*/
    @JsonProperty("isSpecName")
    @JSONField(name = "isSpecName")
    private int isSpecName;

    /** 生产厂家字段是否有值 （1 有值，0 无值）*/
    @JsonProperty("isManufacturer")
    @JSONField(name = "isManufacturer")
    private int isManufacturer;



}
