package cn.wr.collect.sync.model.gc;

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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Data
@Table(name = "pgc_merchant_info")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PgcMerchantInfo implements Model {

    /**
     * 自增长主键
     */
    @Column(name = "id")
    private Long id;

    /**
     * db_id
     */
    @QueryField(order = 0)
    @Column(name = "db_id")
    private Integer dbId;

    /**
     * 连锁id
     */
    @QueryField(order = 1)
    @Column(name = "organization_id")
    private Integer organizationId;

    /**
     * 连锁全称
     */
    @Column(name = "merchant_name")
    private String merchantName;

    /**
     * 连锁简称
     */
    @Column(name = "organization_name")
    private String organizationName;

    /**
     * 状态位
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @Column(name = "gmtCreated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtCreated;
    /**
     * 更新时间
     */
    @Column(name = "gmtUpdated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtUpdated;


    public PgcMerchantInfo convert(ResultSet rs) throws SQLException {
        this.setId(ResultSetConvert.getLong(rs, 1));
        this.setMerchantName(ResultSetConvert.getString(rs, 2));
        this.setOrganizationId(ResultSetConvert.getInt(rs, 3));
        this.setOrganizationName(ResultSetConvert.getString(rs, 4));
        this.setDbId(ResultSetConvert.getInt(rs, 5));
        this.setStatus(ResultSetConvert.getInt(rs, 6));
        this.setGmtCreated(ResultSetConvert.getLocalDateTime(rs, 7));
        this.setGmtUpdated(ResultSetConvert.getLocalDateTime(rs, 8));

        return this;
    }
}
