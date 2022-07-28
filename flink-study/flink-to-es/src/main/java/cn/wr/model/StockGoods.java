package cn.wr.model;


import cn.wr.utils.ResultSetConvert;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * @author RWang
 * @Date 2022/7/20
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockGoods implements Serializable {

    private static final long serialVersionUID = -7442079184850221779L;
    @JsonProperty("id")
    private Long id;

    @JsonProperty("stock_no")
    private String stockNo;

    @JsonProperty(  "sku_no")
    private String skuNo;

    @JsonProperty(  "total_amount")
    private BigDecimal totalAmount;

    @JsonProperty(  "used_amount")
    private BigDecimal usedAmount;

    @JsonProperty(  "quantity")
    private BigDecimal quantity;

    @JsonProperty(  "unit")
    private String unit;

    @JsonProperty(  "center_state")
    private Integer centerState;

    @JsonProperty(  "sale_state")
    private Integer saleState;

    @JsonProperty(  "start_time")
    private LocalDateTime startTime;

    @JsonProperty(  "end_time")
    private LocalDateTime endTime;

    @JsonProperty( "once")
    private BigDecimal once;

    @JsonProperty( "virtual_quantity")
    private BigDecimal virtualQuantity;

    @JsonProperty( "virtual_on")
    private Integer virtualOn;

    @JsonProperty( "merchant_id")
    private Long merchantId;

    @JsonProperty( "store_id")
    private Long storeId;

    @JsonProperty( "internal_id")
    private String internalId;

    @JsonProperty( "source")
    private Integer source;

    @JsonProperty( "gmt_updated")
    private LocalDateTime gmtUpdated;

    @JsonProperty( "gmt_created")
    private LocalDateTime gmtCreated;

    @JsonProperty( "ttl")
    private Integer ttl;

    @JsonProperty( "last_get")
    private LocalDateTime lastGet;

    /**
     * 参数转换
     * @param rs
     * @return
     * @throws SQLException
     */
    public StockGoods convert(ResultSet rs) throws SQLException {
        this.id = ResultSetConvert.getLong(rs, 1);
        this.stockNo = ResultSetConvert.getString(rs, 2);
        this.skuNo = ResultSetConvert.getString(rs, 3);
        this.totalAmount = ResultSetConvert.getBigDecimal(rs, 4);
        this.usedAmount = ResultSetConvert.getBigDecimal(rs, 5);
        this.quantity = ResultSetConvert.getBigDecimal(rs, 6);
        this.unit = ResultSetConvert.getString(rs, 7);
        this.centerState = ResultSetConvert.getInt(rs, 8);
        this.saleState = ResultSetConvert.getInt(rs, 9);
        this.startTime = ResultSetConvert.getLocalDateTime(rs, 10);
        this.endTime = ResultSetConvert.getLocalDateTime(rs, 11);
        this.once = ResultSetConvert.getBigDecimal(rs, 12);
        this.virtualQuantity = ResultSetConvert.getBigDecimal(rs, 13);
        this.virtualOn = ResultSetConvert.getInt(rs, 14);
        this.merchantId = ResultSetConvert.getLong(rs, 15);
        this.storeId = ResultSetConvert.getLong(rs, 16);
        this.internalId = ResultSetConvert.getString(rs, 17);
        this.source = ResultSetConvert.getInt(rs, 18);
        this.gmtUpdated = ResultSetConvert.getLocalDateTime(rs, 19);
        this.gmtCreated = ResultSetConvert.getLocalDateTime(rs, 20);
        this.ttl = ResultSetConvert.getInt(rs, 21);
        this.lastGet = ResultSetConvert.getLocalDateTime(rs, 22);
        return this;
    }
}
