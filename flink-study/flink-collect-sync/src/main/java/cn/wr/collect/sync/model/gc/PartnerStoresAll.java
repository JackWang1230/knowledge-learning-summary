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
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Data
@Table(name = "gc_partner_stores_all")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerStoresAll implements Model {

    private static final long serialVersionUID = -6166523127593658176L;
    @Column(name = "id")
    private Long id;

    @Column(name = "table_id")
    private Integer tableId;

    @QueryField(order = 0)
    @Column(name = "db_id")
    @Correspond(type = Correspond.Type.Key)
    private Integer dbId;

    @Column(name = "merchant_id")
    @Correspond(type = Correspond.Type.Key)
    private Integer merchantId;

    @Column(name = "store_id")
    @Correspond(type = Correspond.Type.Key)
    private Integer storeId;

    @Column(name = "merchant_name")
    private String merchantName;

    @Column(name = "internal_id")
    @QueryField(order = 2)
    @Correspond(type = Correspond.Type.Key)
    private String internalId;

    @QueryField(order = 1)
    @Correspond(type = Correspond.Type.Key)
    @Column(name = "group_id")
    private String groupId;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "address")
    private String address;

    @Column(name = "location")
    private String location;

    @Column(name = "created_at")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateAt;

    @Column(name = "gmtcreated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtcreated;

    @Column(name = "gmtupdated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtupdated;

    @Column(name = "channel")
    @Correspond(type = Correspond.Type.Key)
    private String channel;

    @Column(name = "last_date")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime lastDate;

    @Column(name = "last_pg_date")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime lastPgDate;

    @Column(name = "store_code")
    private String storeCode;

    @Column(name = "out_id")
    private Integer outId;

    @Column(name = "hash_seven")
    /*@Correspond(field = {EsFieldConst.geohash}, mode = Correspond.Mode.Single)*/
    private String hashSeven;

    @Column(name = "baidu_online")
    private Integer baiduOnline;

    public PartnerStoresAll convert(ResultSet rs) throws SQLException {
        // `id`, `table_id`, `db_id`, `merchant_id`, `store_id`, `merchant_name`, `internal_id`, `group_id`,
        // `store_name`, `longitude`, `latitude`, `address`, `location`, `created_at`, `update_at`, `gmtcreated`,
        this.setId(rs.getLong(1));
        this.setTableId(rs.getInt(2));
        this.setDbId(rs.getInt(3));
        this.setMerchantId(rs.getInt(4));
        this.setStoreId(rs.getInt(5));
        this.setMerchantName(rs.getString(6));
        this.setInternalId(rs.getString(7));
        this.setGroupId(rs.getString(8));
        this.setStoreName(rs.getString(9));
        this.setLongitude(rs.getString(10));
        this.setLatitude(rs.getString(11));
        this.setAddress(rs.getString(12));
        this.setLocation(rs.getString(13));
        if (StringUtils.isNotBlank(rs.getString(14)) && !StringUtils.equals("0000-00-00 00:00:00", rs.getString(14))) {
            this.setCreatedAt(null != rs.getTimestamp(14) ? rs.getTimestamp(14).toLocalDateTime() : null);
        }
        if (StringUtils.isNotBlank(rs.getString(15)) && !StringUtils.equals("0000-00-00 00:00:00", rs.getString(15))) {
            this.setUpdateAt(null != rs.getTimestamp(15) ? rs.getTimestamp(15).toLocalDateTime() : null);
        }
        this.setGmtcreated(null != rs.getTimestamp(16) ? rs.getTimestamp(16).toLocalDateTime() : null);
        // `gmtupdated`, `channel`, `last_date`, `last_pg_date`, `store_code`, `out_id`, `hash_seven`, `baidu_online`
        this.setGmtupdated(null != rs.getTimestamp(17) ? rs.getTimestamp(17).toLocalDateTime() : null);
        this.setChannel(rs.getString(18));
        this.setLastDate(null != rs.getTimestamp(19) ? rs.getTimestamp(19).toLocalDateTime() : null);
        this.setLastPgDate(null != rs.getTimestamp(20) ? rs.getTimestamp(20).toLocalDateTime() : null);
        this.setStoreCode(rs.getString(21));
        this.setOutId(rs.getInt(22));
        this.setHashSeven(rs.getString(23));
        this.setBaiduOnline(rs.getInt(24));
        return this;
    }
}
