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
@Table(name = "gc_goods_overweight")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoodsOverweight implements Model {
    private static final long serialVersionUID = 5973725547982231217L;

    /**
     * 主键
     */
    @Column(name = "id")
    private Long id;
    /**
     * 条码
     */
    @QueryField
    @Correspond(type = Correspond.Type.Key, field = EsFieldConst.trade_code)
    @Column(name = "trade_code")
    private String tradeCode;
    /**
     * 是否超重（0-否 1-是）
     */
    @Correspond(field = {EsFieldConst.is_overweight})
    @Column(name = "is_overweight")
    private Integer isOverweight;

    @Column(name = "gmtCreated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtCreated;
    @Column(name = "gmtUpdated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtUpdated;

    @Override
    public Long getId() {
        return id;
    }

    public GoodsOverweight convert(ResultSet rs) throws SQLException {
        this.setId(ResultSetConvert.getLong(rs, 1));
        this.setTradeCode(ResultSetConvert.getString(rs, 2));
        this.setIsOverweight(ResultSetConvert.getInt(rs, 3));
        this.setGmtCreated(ResultSetConvert.getLocalDateTime(rs, 4));
        this.setGmtUpdated(ResultSetConvert.getLocalDateTime(rs, 5));
        return this;
    }
}
