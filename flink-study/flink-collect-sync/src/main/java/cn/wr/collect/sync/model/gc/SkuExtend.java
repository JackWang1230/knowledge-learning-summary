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
@Table(name = "gc_sku_extend")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkuExtend implements Model {
    private static final long serialVersionUID = 5326738501819915072L;

    @Column(name = "id")
    private Long id;

    @QueryField(order = 1)
    @Column(name = "title")
    @Correspond(type = Correspond.Type.Key, field = {EsFieldConst.merchant_id, EsFieldConst.goods_internal_id})
    private String title;

    @Column(name = "key_word")
    @Correspond(field = {EsFieldConst.spell_word})
    private String keyWord;

    @QueryField(order = 0)
    @Column(name = "sku_no")
    @Correspond(type = Correspond.Type.Key, field = {EsFieldConst.merchant_id, EsFieldConst.goods_internal_id})
    private String skuNo;

    @Column(name = "gmtCreated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtCreated;

    @Column(name = "gmtUpdated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtUpdated;

    /**
     * 参数转换
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public SkuExtend convert(ResultSet rs) throws SQLException {
        this.setId(ResultSetConvert.getLong(rs, 1));
        this.setTitle(ResultSetConvert.getString(rs, 2));
        this.setKeyWord(ResultSetConvert.getString(rs, 3));
        this.setSkuNo(ResultSetConvert.getString(rs, 4));
        this.setGmtCreated(ResultSetConvert.getLocalDateTime(rs, 5));
        this.setGmtUpdated(ResultSetConvert.getLocalDateTime(rs, 6));
        return this;
    }
}
