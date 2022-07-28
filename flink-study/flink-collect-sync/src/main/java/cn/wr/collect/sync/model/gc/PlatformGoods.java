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
import java.util.Objects;

@Data
@Table(name = "platform_goods")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlatformGoods implements Model {
    private static final long serialVersionUID = -5092726071012399492L;

    @Column(name = "id")
    private Long id;

    @QueryField(order = 0)
    @Correspond(type = Correspond.Type.Key, field = EsFieldConst.merchant_id)
    @Column(name = "merchantId")
    private Long merchantId;

    @QueryField(order = 1)
    @Correspond(type = Correspond.Type.Key, field = EsFieldConst.store_id)
    @Column(name = "storeId")
    private Long storeId;

    @QueryField(order = 3)
    @Column(name = "goodsInternalId")
    @Correspond(type = Correspond.Type.Key, field = EsFieldConst.goods_internal_id)
    private String goodsInternalId;

    @QueryField(order = 2)
    @Column(name = "channel")
    @Correspond(type = Correspond.Type.Key, field = EsFieldConst.channel)
    private String channel;

    @Column(name = "status")
    @Correspond(field = EsFieldConst.is_wr_off_shelf)
    private Integer status;

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
    public PlatformGoods convert(ResultSet rs) throws SQLException {
        this.setId(ResultSetConvert.getLong(rs, 1));
        this.setMerchantId(ResultSetConvert.getLong(rs, 2));
        this.setStoreId(ResultSetConvert.getLong(rs, 3));
        this.setGoodsInternalId(ResultSetConvert.getString(rs, 4));
        this.setChannel(ResultSetConvert.getString(rs, 5));
        this.setStatus(ResultSetConvert.getInt(rs, 6));
        this.setGmtCreated(ResultSetConvert.getLocalDateTime(rs, 7));
        this.setGmtUpdated(ResultSetConvert.getLocalDateTime(rs, 8));
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PlatformGoods that = (PlatformGoods) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(merchantId, that.merchantId) &&
                Objects.equals(storeId, that.storeId) &&
                Objects.equals(goodsInternalId, that.goodsInternalId) &&
                Objects.equals(channel, that.channel) &&
                Objects.equals(status, that.status) &&
                Objects.equals(gmtCreated, that.gmtCreated) &&
                Objects.equals(gmtUpdated, that.gmtUpdated);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id, merchantId, storeId, goodsInternalId, channel, status, gmtCreated, gmtUpdated);
    }
}
