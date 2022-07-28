package cn.wr.collect.sync.model.stock;

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
@Table(name = "stock_merchant")
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockMerchant implements Model {
    private static final long serialVersionUID = 7626466628547163959L;

    /**
     * 自增id
     */
    @Override
    public Long getId() {
        return organizationId;
    }

    /**
     * 组织id
     */
    @Column(name = "organization_id")
    @QueryField(order = 1)
    private Long organizationId;

    /**
     * 根组织id
     */
    @Column(name = "root_id")
    @QueryField(order = 0)
    private Integer rootId;

    /**
     * 内码
     */
    @Column(name = "internal_code")
    private String internalCode;

    /**
     * 内码名称
     */
    @Column(name = "internal_name")
    private String internalName;

    /**
     * 名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 简称
     */
    @Column(name = "stort_name")
    private String shortName;

    /**
     * 状态
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 更新时间
     */
    @Column(name = "gmt_updated")
    private LocalDateTime gmtUpdated;

    /**
     * 创建时间
     */
    @Column(name = "gmt_created")
    private LocalDateTime gmtCreated;


    /**
     * 组织架构
     */
    @Column(name = "organ_structure")
    private Integer organStructure;

    /**
     * 商户类型
     */
    @Column(name = "business_type")
    private Integer businessType;

    /**
     * 价格状态
     */
    @Column(name = "price_state")
    private Integer priceState;

    /**
     * 库存状态
     */
    @Column(name = "stock_state")
    private Integer stockState;

    /**
     * 对码状态
     */
    @Column(name = "sync_sku_state")
    private Integer syncSkuState;


    /**
     * 参数转换
     * @param rs
     * @return
     * @throws SQLException
     */
    public StockMerchant convert(ResultSet rs) throws SQLException {
        this.organizationId = ResultSetConvert.getLong(rs, 1);
        this.rootId = ResultSetConvert.getInt(rs, 2);
        this.internalCode = ResultSetConvert.getString(rs, 3);
        this.internalName = ResultSetConvert.getString(rs, 4);
        this.name = ResultSetConvert.getString(rs, 5);
        this.shortName = ResultSetConvert.getString(rs, 6);
        this.status = ResultSetConvert.getInt(rs, 7);
        this.organStructure = ResultSetConvert.getInt(rs, 8);
        this.gmtUpdated = ResultSetConvert.getLocalDateTime(rs, 9);
        this.gmtCreated = ResultSetConvert.getLocalDateTime(rs, 10);
        this.priceState = ResultSetConvert.getInt(rs, 11);
        this.stockState = ResultSetConvert.getInt(rs, 12);
        this.syncSkuState = ResultSetConvert.getInt(rs, 13);
        return this;
    }

}
