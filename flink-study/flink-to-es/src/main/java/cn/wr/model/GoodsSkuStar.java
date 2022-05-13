package cn.wr.model;

import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author RWang
 * @Date 2022/5/11
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoodsSkuStar {

    /** sku编号(同gc_ug_spu_goods.goods_no) */
    @JsonProperty("sku_no")
    private String skuNo;

    /** 商户id*/
    @JsonProperty("merchant_id")
    private long merchantId;


    /** 连锁商品名称是否有值（1 有值，0 无值）*/
    @JsonProperty("is_goods_name")
    private int isGoodsName;

    /** 是否符合 批准文号：字符类型：汉字、英文、数字 ｜ 英文、数字（1符合 ，0 不符合）*/
    @JsonProperty("is_approval_number")
    private int isApprovalNumber;

    /** 是否符合 商品条码：字符类型：纯数字 字符长度：8、9、10、12、13、14（1 符合，0 不符合）*/
    @JsonProperty("isTradecode")
    private int isTradecode;

    /** 商品规格字段是否存在值（1 有值，0 无值）*/
    @JsonProperty("isSpecName")
    private int isSpecName;

    /** 生产厂家字段是否有值 （1 有值，0 无值）*/
    @JsonProperty("isManufacturer")
    private int isManufacturer;
}
