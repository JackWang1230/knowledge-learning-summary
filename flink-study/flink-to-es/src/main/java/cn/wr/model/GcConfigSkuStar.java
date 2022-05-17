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
    @JSONField(name = "is_trade_code")
    private int isTradecode;

    /** 商品规格字段是否存在值（1 有值，0 无值）*/
    @JsonProperty("isSpecName")
    @JSONField(name = "is_spec_name")
    private int isSpecName;

    /** 生产厂家字段是否有值 （1 有值，0 无值）*/
    @JsonProperty("isManufacturer")
    @JSONField(name = "is_manufacturer")
    private int isManufacturer;


}
