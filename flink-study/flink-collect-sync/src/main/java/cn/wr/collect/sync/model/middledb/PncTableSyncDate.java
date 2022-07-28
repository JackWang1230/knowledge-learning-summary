package cn.wr.collect.sync.model.middledb;

import cn.wr.collect.sync.common.CustomLocalDateTimeDeserializer;
import cn.wr.collect.sync.common.CustomLocalDateTimeSerializer;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Table;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.sql.ResultSet;
import java.time.LocalDateTime;

@Data
@Table(name = "pnc_table_sync_date")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PncTableSyncDate implements Serializable {
    private static final long serialVersionUID = 2879088399814036195L;
    /**
     * 自增长主键
     */
    @Column(name = "id")
    private Integer id;
    /**
     * 同步表名
     */
    @Column(name = "table_name")
    private String tableName;
    /**
     * 最后同步时间
     */
    @Column(name = "last_sync_date")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime lastSyncDate;
    /**
     * 更新时间
     */
    @Column(name = "gmtUpdated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtUpdated;
    /**
     * 创建时间
     */
    @Column(name = "gmtCreated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtCreated;

    public PncTableSyncDate convert(ResultSet rs) throws Exception {
        this.setId(rs.getInt(1));
        this.setTableName(rs.getString(2));
        this.setLastSyncDate(null != rs.getTimestamp(3) ? rs.getTimestamp(3).toLocalDateTime() : null);
        this.setGmtUpdated(null != rs.getTimestamp(4) ? rs.getTimestamp(4).toLocalDateTime() : null);
        this.setGmtCreated(null != rs.getTimestamp(5) ? rs.getTimestamp(5).toLocalDateTime() : null);
        return this;
    }
}
