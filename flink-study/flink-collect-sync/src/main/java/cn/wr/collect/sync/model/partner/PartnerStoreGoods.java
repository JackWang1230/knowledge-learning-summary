package cn.wr.collect.sync.model.partner;

import cn.wr.collect.sync.constants.EsFieldConst;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Correspond;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Data
@Table(name = "partner_store_goods")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerStoreGoods implements Model {

    private static final long serialVersionUID = -2720291895249575607L;
    @Column(name = "id")
    private Long id;

    @QueryField(order = 0)
    @Column(name = "db_id")
    @Correspond(type = Correspond.Type.Key)
    private Integer dbId;

    @Column(name = "table_id")
    private Long tableId;

    @QueryField(order = 2)
    @Column(name = "goods_internal_id")
    @Correspond(field = {EsFieldConst.goods_internal_id}, type = Correspond.Type.Key)
    private String goodsInternalId;

    @QueryField(order = 1)
    @Column(name = "group_id")
    @Correspond(type = Correspond.Type.Key)
    private String groupId;

    @Column(name = "price")
    @Correspond(field = {EsFieldConst.sale_price}, mode = Correspond.Mode.Single)
    private BigDecimal price;

    @Column(name = "status")
    @Correspond(field = {EsFieldConst.is_off_shelf}, mode = Correspond.Mode.Multi)
    private Integer status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "gmtcreated")
    private LocalDateTime gmtcreated;

    @Column(name = "gmtupdated")
    private LocalDateTime gmtupdated;

    @Column(name = "member_price")
    private BigDecimal memberPrice;

    public PartnerStoreGoods convert(ResultSet rs) throws SQLException {
        this.setId(ResultSetConvert.getLong(rs, 1));
        this.setDbId(ResultSetConvert.getInt(rs, 2));
        this.setTableId(ResultSetConvert.getLong(rs, 3));
        this.setGoodsInternalId(ResultSetConvert.getString(rs, 4));
        this.setGroupId(ResultSetConvert.getString(rs, 5));
        this.setPrice(ResultSetConvert.getBigDecimal(rs, 6));
        this.setStatus(ResultSetConvert.getInt(rs, 7));
        this.setCreatedAt(ResultSetConvert.getLocalDateTime(rs, 8));
        this.setUpdatedAt(ResultSetConvert.getLocalDateTime(rs, 9));
        this.setGmtcreated(ResultSetConvert.getLocalDateTime(rs, 10));
        this.setGmtupdated(ResultSetConvert.getLocalDateTime(rs, 11));
        this.setMemberPrice(ResultSetConvert.getBigDecimal(rs, 12));
        return this;
    }
}
