package cn.wr.collect.sync.model.gc;


import cn.wr.collect.sync.common.CustomLocalDateTimeDeserializer;
import cn.wr.collect.sync.common.CustomLocalDateTimeSerializer;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.annotations.Table;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

@Data
@Table(name = "gc_goods_sales_statistics_merchant")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoodsSalesStatisticsMerchant implements Model {

    private static final long serialVersionUID = 3815964497882660350L;
    @Column(name = "id")
    private Long id;

    @QueryField(order = 0)
    @Column(name = "merchant_id")
    private Long merchantId;

    @QueryField(order = 1)
    @Column(name = "internal_id")
    private String internalId;

    @Column(name = "item_type")
    private Integer itemType;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "gmtcreated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtcreated;

    @Column(name = "gmtupdated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtupdated;

    @Column(name = "deleted")
    private Integer deleted;

}
