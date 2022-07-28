package cn.wr.collect.sync.model.gc;

import cn.wr.collect.sync.common.CustomLocalDateTimeDeserializer;
import cn.wr.collect.sync.common.CustomLocalDateTimeSerializer;
import cn.wr.collect.sync.constants.EsFieldConst;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Correspond;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Data
@Table(name = "gc_goods_spu_attr_syncrds")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoodsSpuAttrSyncrds implements Model {
    private static final long serialVersionUID = 4896187704426536862L;
    @Column(name = "id")
    private Long id;

    @QueryField
    @Correspond(type = Correspond.Type.Key, field = EsFieldConst.trade_code)
    @Column(name = "bar_code")
    private String barCode;

    @Column(name = "spu_id")
    private Long spuId;

    @QueryField(order = 1)
    @Column(name = "attr_id")
    @Correspond(type = Correspond.Type.Both,
            mode = Correspond.Mode.Multi,
            field = {EsFieldConst.is_ephedrine, EsFieldConst.is_double, EsFieldConst.attr_ids})
    private Long attrId;

    @Column(name = "gmtCreated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtCreated;

    @Column(name = "gmtUpdated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtUpdated;

    @Column(name = "goods_name")
    private String goodsName;

    public GoodsSpuAttrSyncrds convert(ResultSet rs) throws SQLException {
        this.setId(ResultSetConvert.getLong(rs, 1));
        this.setBarCode(ResultSetConvert.getString(rs, 2));
        this.setAttrId(ResultSetConvert.getLong(rs, 3));
        this.setSpuId(ResultSetConvert.getLong(rs, 4));
        this.setGmtCreated(ResultSetConvert.getLocalDateTime(rs, 5));
        this.setGmtUpdated(ResultSetConvert.getLocalDateTime(rs, 6));
        this.setGoodsName(ResultSetConvert.getString(rs, 7));
        return this;
    }
}
