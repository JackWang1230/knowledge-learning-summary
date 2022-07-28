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
@Table(name = "organize_base")
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizeBase implements Model {
    private static final long serialVersionUID = -1546523516333777760L;

    /**
     * 自增长主键 门店id
     */
    @Correspond(type = Correspond.Type.Key, field = EsFieldConst.store_id)
    @Column(name = "organizationId")
    @QueryField(order = 1)
    private Long organizationId;

    /**
     * 连锁id
     */
    @Correspond(type = Correspond.Type.Key, field = EsFieldConst.merchant_id)
    @Column(name = "rootId")
    @QueryField(order = 0)
    private Long rootId;

    /**
     * 完整名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 简称
     */
    @Column(name = "shortName")
    private String shortName;

    /**
     * 门店开关状态
     */
    @Column(name = "organizationType")
    private Integer organizationType;

    /**
     * 门店开关状态
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 开关状态
     */
    @Correspond(field = {EsFieldConst.store_status})
    @Column(name = "isO2O")
    private Integer isO2O;

    /**
     * 是否开启dtp商品1为开启0为未开启
     */
    @Column(name = "isDTP")
    private Integer isDTP;

    /**
     * 是否虚拟商户1为是0为否
     */
    // 2020-10-26 删除字段
    /*@Column(name = "isVirtual")
    private Integer isVirtual;*/
    /**
     * 1为普药网络2为dtp网络
     */
    @Correspond(mode = Correspond.Mode.Multi, field = {EsFieldConst.is_dtp_store}) // dtp门店不写入es，使用sync_date代替
    @Column(name = "netType")
    private Integer netType;

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

    @Override
    public Long getId() {
        return organizationId;
    }

    public OrganizeBase convert(ResultSet rs) throws SQLException {
        this.setOrganizationId(ResultSetConvert.getLong(rs, 1));
        this.setRootId(ResultSetConvert.getLong(rs, 2));
        this.setOrganizationType(ResultSetConvert.getInt(rs, 3));
        this.setName(ResultSetConvert.getString(rs, 4));
        this.setShortName(ResultSetConvert.getString(rs, 5));
        this.setStatus(ResultSetConvert.getInt(rs, 6));
        this.setIsO2O(ResultSetConvert.getInt(rs, 7));
        this.setIsDTP(ResultSetConvert.getInt(rs, 8));
        this.setNetType(ResultSetConvert.getInt(rs, 9));
        this.setGmtCreated(ResultSetConvert.getLocalDateTime(rs, 10));
        this.setGmtUpdated(ResultSetConvert.getLocalDateTime(rs, 11));
        return this;
    }
}
