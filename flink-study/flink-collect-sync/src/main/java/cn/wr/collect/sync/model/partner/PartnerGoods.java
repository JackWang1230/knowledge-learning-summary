package cn.wr.collect.sync.model.partner;


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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Table(name = "partner_goods")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerGoods implements Model {
    private static final long serialVersionUID = -4142458629858526661L;

    @Column(name = "id")
    private Long id;

    @QueryField(order = 0)
    @Correspond(type = Correspond.Type.Key)
    @Column(name = "db_id")
    private Integer dbId;

    @Column(name = "table_id")
    private Integer tableId;

    @QueryField(order = 1)
    @Correspond(field = {EsFieldConst.goods_internal_id}, type = Correspond.Type.Key)
    @Column(name = "internal_id")
    private String internalId;

    @Correspond(field = {EsFieldConst.common_name, EsFieldConst.real_common_name}, mode = Correspond.Mode.Multi)
    @Column(name = "common_name")
    private String commonName;

    @Correspond(field = {EsFieldConst.real_trade_code, EsFieldConst.trade_code}, mode = Correspond.Mode.Multi)
    @Column(name = "trade_code")
    private String tradeCode;

    @Correspond(field = {EsFieldConst.approval_number}, mode = Correspond.Mode.Multi)
    @Column(name = "approval_number")
    private String approvalNumber;

    @Correspond(field = {EsFieldConst.form}, mode = Correspond.Mode.Single)
    @Column(name = "form")
    private String form;

    @Correspond(field = {EsFieldConst.pack}, mode = Correspond.Mode.Single)
    @Column(name = "pack")
    private String pack;

    @Correspond(field = {EsFieldConst. base_price}, mode = Correspond.Mode.Single)
    @Column(name = "price")
    private BigDecimal price;

    @Correspond(field = {EsFieldConst.manufacturer}, mode = Correspond.Mode.Single)
    @Column(name = "manufacturer")
    private String manufacturer;

    @Correspond(field = {EsFieldConst.is_off_shelf}, mode = Correspond.Mode.Multi)
    @Column(name = "status")
    private Integer status;

    @Column(name = "goods_create_time")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime goodsCreateTime;

    @Column(name = "goods_update_time")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime goodsUpdateTime;

    @Column(name = "created_at")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;

    @Column(name = "gmtcreated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtcreated;

    @Column(name = "gmtupdated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtupdated;

//    private Integer storeId;

    /**
     * 参数转换
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public PartnerGoods convert(ResultSet rs) throws SQLException {
        this.setId(ResultSetConvert.getLong(rs,1));
        this.setDbId(ResultSetConvert.getInt(rs,2));
        this.setTableId(ResultSetConvert.getInt(rs,3));
        this.setInternalId(ResultSetConvert.getString(rs,4));
        this.setCommonName(ResultSetConvert.getString(rs,5));
        this.setTradeCode(ResultSetConvert.getString(rs,6));
        this.setApprovalNumber(ResultSetConvert.getString(rs,7));
        this.setForm(ResultSetConvert.getString(rs,8));
        this.setPack(ResultSetConvert.getString(rs,9));
        this.setPrice(ResultSetConvert.getBigDecimal(rs,10));
        this.setManufacturer(ResultSetConvert.getString(rs,11));
        this.setStatus(ResultSetConvert.getInt(rs,12));
        this.setGoodsCreateTime(ResultSetConvert.getLocalDateTime(rs,13));
        this.setGoodsUpdateTime(ResultSetConvert.getLocalDateTime(rs,14));
        this.setCreatedAt(ResultSetConvert.getLocalDateTime(rs,15));
        this.setUpdatedAt(ResultSetConvert.getLocalDateTime(rs,16));
        this.setGmtcreated(ResultSetConvert.getLocalDateTime(rs,17));
        this.setGmtupdated(ResultSetConvert.getLocalDateTime(rs,18));
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartnerGoods that = (PartnerGoods) o;
        return id.equals(that.id) &&
                dbId.equals(that.dbId) &&
                tableId.equals(that.tableId) &&
                internalId.equals(that.internalId) &&
                commonName.equals(that.commonName) &&
                tradeCode.equals(that.tradeCode) &&
                approvalNumber.equals(that.approvalNumber) &&
                form.equals(that.form) &&
                pack.equals(that.pack) &&
                price.equals(that.price) &&
                manufacturer.equals(that.manufacturer) &&
                status.equals(that.status) &&
                goodsCreateTime.equals(that.goodsCreateTime) &&
                goodsUpdateTime.equals(that.goodsUpdateTime) &&
                createdAt.equals(that.createdAt) &&
                updatedAt.equals(that.updatedAt) &&
                gmtcreated.equals(that.gmtcreated) &&
                gmtupdated.equals(that.gmtupdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dbId, tableId, internalId, commonName, tradeCode, approvalNumber, form, pack, price, manufacturer, status, goodsCreateTime, goodsUpdateTime, createdAt, updatedAt, gmtcreated, gmtupdated);
    }
}
