package cn.wr.collect.sync.model.partner;

import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Data
@Table(name = "partner_stores")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerStores implements Model {
    private static final long serialVersionUID = 6849665000098488859L;
    @Column(name = "id")
    private Long id;

    @QueryField(order = 0)
    @Column(name = "db_id")
    private Integer dbId;

    @Column(name = "table_id")
    private Long tableId;

    @QueryField(order = 2)
    @Column(name = "internal_id")
    private String internalId;

    @QueryField(order = 1)
    @Column(name = "group_id")
    private String groupId;

    @Column(name = "common_name")
    private String commonName;

    @Column(name = "number")
    private String number;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "status")
    private Integer status;

    @Column(name = "out_id")
    private Long outId;

    @Column(name = "province")
    private String province;

    @Column(name = "city")
    private String city;

    @Column(name = "area")
    private String area;

    @Column(name = "business_time")
    private String businessTime;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "gmtcreated")
    private LocalDateTime gmtcreated;

    @Column(name = "gmtupdated")
    private LocalDateTime gmtupdated;

    // `id`, `db_id`, `table_id`, `internal_id`, `group_id`, `common_name`, `number`, `address`, `phone`, `status`,
    // `out_id`, `province`, `city`, `area`, `business_time`, `longitude`, `latitude`, `created_at`, `updated_at`,
    // `gmtcreated`, `gmtupdated`
    public PartnerStores convert(ResultSet rs) throws SQLException {
        this.setId(ResultSetConvert.getLong(rs, 1));
        this.setDbId(ResultSetConvert.getInt(rs, 2));
        this.setTableId(ResultSetConvert.getLong(rs, 3));
        this.setInternalId(ResultSetConvert.getString(rs, 4));
        this.setGroupId(ResultSetConvert.getString(rs, 5));
        this.setCommonName(ResultSetConvert.getString(rs, 6));
        this.setNumber(ResultSetConvert.getString(rs, 7));
        this.setAddress(ResultSetConvert.getString(rs, 8));
        this.setPhone(ResultSetConvert.getString(rs, 9));
        this.setStatus(ResultSetConvert.getInt(rs, 10));
        this.setOutId(ResultSetConvert.getLong(rs, 11));
        this.setProvince(ResultSetConvert.getString(rs, 12));
        this.setCity(ResultSetConvert.getString(rs, 13));
        this.setArea(ResultSetConvert.getString(rs, 14));
        this.setBusinessTime(ResultSetConvert.getString(rs, 15));
        this.setLongitude(ResultSetConvert.getString(rs, 16));
        this.setLatitude(ResultSetConvert.getString(rs, 17));
        this.setCreatedAt(ResultSetConvert.getLocalDateTime(rs, 18));
        this.setUpdatedAt(ResultSetConvert.getLocalDateTime(rs, 19));
        this.setGmtcreated(ResultSetConvert.getLocalDateTime(rs, 20));
        this.setGmtupdated(ResultSetConvert.getLocalDateTime(rs, 21));
        return this;
    }
}
