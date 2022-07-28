package cn.wr.collect.sync.model.partner;


import cn.wr.collect.sync.common.CustomLocalDateTimeDeserializer;
import cn.wr.collect.sync.common.CustomLocalDateTimeSerializer;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
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

@Data
@Table(name = "partners")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Partners implements Model {
    private static final long serialVersionUID = -4142458629858526661L;

    @Column(name = "id")
    private Long id;

    @Column(name = "common_name")
    private String commonName;

    @Column(name = "full_name")
    private String fullName;

    @QueryField(order = 1)
    @Column(name = "cooperation")
    private String cooperation;

    @Column(name = "dbname")
    private String dbname;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "status")
    private Integer status;

    @Column(name = "created_at")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;

    @Column(name = "start_at")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime startAt;

    @Column(name = "finish_at")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime finishAt;

    @Column(name = "release_at")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime releaseAt;

    @Column(name = "out_id")
    private Integer outId;

    @Column(name = "orderInsert")
    private Integer orderInsert;

    @Column(name = "blacklist_status")
    private Integer blacklistStatus;

    @Column(name = "transiteDiscount")
    private BigDecimal transiteDiscount;

    @Column(name = "type")
    private Integer type;

    @Column(name = "partners_type")
    private String partnersType;

    @Column(name = "channel")
    private String channel;

    @QueryField
    @Column(name = "organizationId")
    private Integer organizationId;

    /**
     * 参数转换
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public Partners convert(ResultSet rs) throws SQLException {
        /*"select `id`, `common_name`, `full_name`, `cooperation`, `dbname`, " +
                " `discount`, `status`, " +
                " (CASE `created_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `created_at` END) AS `created_at`, " +
                " (CASE `updated_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `updated_at` END) AS `updated_at`, " +
                " (CASE `start_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `start_at` END) AS `start_at`, " +
                " (CASE `finish_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `finish_at` END) AS `finish_at`, " +
                " (CASE `release_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `release_at` END) AS `release_at`, " +
                " `out_id`, `organizationId`, `orderInsert`, `blacklist_status`, `transiteDiscount`, `type`, `channel`, `partners_type` "*/
        this.setId(ResultSetConvert.getLong(rs,1));
        this.setCommonName(ResultSetConvert.getString(rs,2));
        this.setFullName(ResultSetConvert.getString(rs,3));
        this.setCooperation(ResultSetConvert.getString(rs,4));
        this.setDbname(ResultSetConvert.getString(rs,5));
        this.setDiscount(ResultSetConvert.getBigDecimal(rs,6));
        this.setStatus(ResultSetConvert.getInt(rs,7));
        this.setCreatedAt(ResultSetConvert.getLocalDateTime(rs,8));
        this.setUpdatedAt(ResultSetConvert.getLocalDateTime(rs,9));
        this.setStartAt(ResultSetConvert.getLocalDateTime(rs,10));
        this.setFinishAt(ResultSetConvert.getLocalDateTime(rs,11));
        this.setReleaseAt(ResultSetConvert.getLocalDateTime(rs,12));
        this.setOutId(ResultSetConvert.getInt(rs,13));
        this.setOrganizationId(ResultSetConvert.getInt(rs,14));
        this.setOrderInsert(ResultSetConvert.getInt(rs,15));
        this.setBlacklistStatus(ResultSetConvert.getInt(rs,16));
        this.setTransiteDiscount(ResultSetConvert.getBigDecimal(rs,17));
        this.setType(ResultSetConvert.getInt(rs,18));
        this.setChannel(ResultSetConvert.getString(rs,19));
        this.setPartnersType(ResultSetConvert.getString(rs,20));
        return this;
    }
}
