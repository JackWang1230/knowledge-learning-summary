package cn.wr.collect.sync.model.welfare;

import cn.wr.collect.sync.common.CustomLocalDateTimeDeserializer;
import cn.wr.collect.sync.common.CustomLocalDateTimeSerializer;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WelfareElastic implements Serializable {
    private static final long serialVersionUID = 4917280938485747417L;
    /**
     * 唯一标志字段（channel-goodsNo）
     */
    @JSONField(name = "unique_id")
    private String uniqueId;

    /**
     * 渠道
     */
    @JSONField(name = "channel")
    private Integer channel;
    /**
     * skuNo
     */
    @JSONField(name = "goods_no")
    private String goodsNo;
    /**
     * SKU标题
     */
    @JSONField(name = "title")
    private String title;
    /**
     * 副标题/卖点介绍
     */
    @JSONField(name = "sub_title")
    private String subTitle;
    /**
     * 规格名称
     */
    @JSONField(name = "spec_name")
    private String specName;
    /**
     * 售价
     */
    @JSONField(name = "price")
    private BigDecimal price;
    /**
     * 商品类别/商品类型
     */
    @JSONField(name = "goods_type")
    private Integer goodsType;
    /**
     * 商品子类型
     */
    @JSONField(name = "goods_sub_type")
    private Integer goodsSubType;
    /**
     * 头图
     */
    @JSONField(name = "img")
    private String img;
    /**
     * 购买链接
     */
    @JSONField(name = "url")
    private String url;
    /**
     * 上下架状态 0：上架，1:下架
     */
    @JSONField(name = "status")
    private Integer status;
    /**
     * 规格
     */
    @JSONField(name = "form")
    private String form;
    /**
     * 厂家
     */
    @JSONField(name = "manufacturer")
    private String manufacturer;
    /**
     * 药品说明
     */
    @JSONField(name = "indications")
    private String indications;
    /**
     * 划线价商品原价
     */
    @JSONField(name = "origin_price")
    private BigDecimal originPrice;

    /**
     * 同步时间
     */
    @JSONField(name = "sync_date")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime syncDate;
}
