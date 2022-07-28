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
@Table(name = "pgc_store_info")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PgcStoreInfo implements Model {
    private static final long serialVersionUID = -4142458629858526661L;
    /**
     * 自增长主键
     */
    @Column(name = "id")
    private Long id;
    /**
     * 门店id
     */
    @QueryField(order = 1)
    @Correspond(type = Correspond.Type.Key, field = EsFieldConst.store_id)
    @Column(name = "store_id")
    private Integer storeId;
    /**
     * 门店名称
     */
    @Column(name = "store_name")
    private String storeName;
    /**
     * 省code
     */
    @Column(name = "province_id")
    @Correspond(field = {EsFieldConst.province_code})
    private Integer provinceId;
    /**
     * 省
     */
    @Column(name = "province_name")
    @Correspond(field = {EsFieldConst.province_name})
    private String provinceName;
    /**
     * 市code
     */
    @Column(name = "city_id")
    @Correspond(field = {EsFieldConst.city_code})
    private Integer cityId;
    /**
     * 市
     */
    @Column(name = "city_name")
    @Correspond(field = {EsFieldConst.city_name})
    private String cityName;
    /**
     * 区code
     */
    @Column(name = "district_id")
    @Correspond(field = {EsFieldConst.area_code})
    private Integer districtId;
    /**
     * 区
     */
    @Column(name = "district_name")
    @Correspond(field = {EsFieldConst.area_name})
    private String districtName;
    /**
     * 地址
     */
    @Column(name = "address")
    private String address;
    /**
     * 连锁id
     */
    @QueryField(order = 0)
    @Column(name = "organization_id")
    @Correspond(type = Correspond.Type.Key, field = EsFieldConst.merchant_id)
    private Integer organizationId;
    /**
     * 连锁名称
     */
    @Column(name = "organization_name")
    private String organizationName;
    /**
     * 经度
     */
    @Column(name = "longitude")
    private String longitude;
    /**
     * 纬度
     */
    @Column(name = "latitude")
    private String latitude;
    /**
     * 数据库id
     */
    @Column(name = "db_id")
    private Integer dbId;
    /**
     * 价格组id
     */
    @Column(name = "group_id")
    private String groupId;
    /**
     * 状态
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

    public PgcStoreInfo convert(ResultSet rs) throws SQLException {
        this.setId(ResultSetConvert.getLong(rs, 1));
        this.setStoreId(ResultSetConvert.getInt(rs, 2));
        this.setStoreName(ResultSetConvert.getString(rs, 3));
        this.setProvinceId(ResultSetConvert.getInt(rs, 4));
        this.setProvinceName(ResultSetConvert.getString(rs, 5));
        this.setCityId(ResultSetConvert.getInt(rs, 6));
        this.setCityName(ResultSetConvert.getString(rs, 7));
        this.setDistrictId(ResultSetConvert.getInt(rs, 8));
        this.setDistrictName(ResultSetConvert.getString(rs, 9));
        this.setAddress(ResultSetConvert.getString(rs, 10));
        this.setOrganizationId(ResultSetConvert.getInt(rs, 11));
        this.setOrganizationName(ResultSetConvert.getString(rs, 12));
        this.setLongitude(ResultSetConvert.getString(rs, 13));
        this.setLatitude(ResultSetConvert.getString(rs, 14));
        this.setDbId(ResultSetConvert.getInt(rs, 15));
        this.setGroupId(ResultSetConvert.getString(rs, 16));
        this.setStatus(ResultSetConvert.getInt(rs, 17));
        this.setGmtCreated(ResultSetConvert.getLocalDateTime(rs, 18));
        this.setGmtUpdated(ResultSetConvert.getLocalDateTime(rs, 19));

        return this;
    }

}
