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
@Table(name = "partner_goods_img")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerGoodsImg implements Model {

    private static final long serialVersionUID = 2212431296203841084L;
    @Column(name = "id")
    private Long id;
    @Column(name = "merchant_id")
    private Integer merchantId;
    @QueryField(order = 0)
    @Column(name = "db_id")
    @Correspond(type = Correspond.Type.Key)
    private Integer dbId;
    @QueryField(order = 1)
    @Column(name = "internal_id")
    @Correspond(type = Correspond.Type.Key, field = EsFieldConst.goods_internal_id)
    private String internalId;
    @Column(name = "img")
    @Correspond(field = {EsFieldConst.img}, mode = Correspond.Mode.Multi)
    private String img;
    @Column(name = "gmtCreated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtCreated;
    @Column(name = "gmtUpdated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtUpdated;

}
