package cn.wr.collect.sync.model.gc;


import cn.wr.collect.sync.common.CustomLocalDateTimeDeserializer;
import cn.wr.collect.sync.common.CustomLocalDateTimeSerializer;
import cn.wr.collect.sync.constants.EsFieldConst;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Correspond;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.annotations.Table;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

@Data
@Table(name = "gc_partner_goods_gift")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerGoodsGift implements Model {

    private static final long serialVersionUID = -2198611571950796225L;
    @Column(name = "id")
    @Correspond(field = {EsFieldConst.is_off_shelf}, mode = Correspond.Mode.Multi)
    private Long id;
    @QueryField(order = 0)
    @Correspond(type = Correspond.Type.Key)
    @Column(name = "db_id")
    private Integer dbId;
    @QueryField(order = 1)
    @Correspond(type = Correspond.Type.Key, field = EsFieldConst.goods_internal_id)
    @Column(name = "internal_id")
    private String internalId;
    @Column(name = "gmtCreated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtCreated;
    @Column(name = "gmtUpdated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtUpdated;

}
