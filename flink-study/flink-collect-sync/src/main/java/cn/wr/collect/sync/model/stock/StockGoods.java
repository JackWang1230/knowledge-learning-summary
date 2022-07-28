package cn.wr.collect.sync.model.stock;

import cn.wr.collect.sync.constants.EsFieldConst;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Correspond;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;


@Data
@Table(name = "stock_goods")
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockGoods implements Model {
    private static final long serialVersionUID = 7626466628547163959L;

    @Column(name = "id")
    private Long id;

    @Column(name = "stock_no")
    private String stockNo;

    @Column(name = "sku_no")
    private String skuNo;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "used_amount")
    private BigDecimal usedAmount;

    @Correspond(field = {EsFieldConst.stock_quantity})
    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "unit")
    private String unit;

    @Column(name = "center_state")
    @Correspond(field = {EsFieldConst.is_off_shelf})
    private Integer centerState;

    @Column(name = "sale_state")
    @Correspond(field = {EsFieldConst.is_off_shelf})
    private Integer saleState;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "once")
    private BigDecimal once;

    @Correspond(field = {EsFieldConst.stock_quantity})
    @Column(name = "virtual_quantity")
    private BigDecimal virtualQuantity;

    @Correspond(field = {EsFieldConst.is_virtual_stock, EsFieldConst.stock_quantity})
    @Column(name = "virtual_on")
    private Integer virtualOn;

    @QueryField
    @Correspond(type = Correspond.Type.Key)
    @Column(name = "merchant_id")
    private Long merchantId;

    @QueryField(order = 1)
    @Correspond(type = Correspond.Type.Key)
    @Column(name = "store_id")
    private Long storeId;

    @QueryField(order = 2)
    @Correspond(type = Correspond.Type.Key)
    @Column(name = "internal_id")
    private String internalId;

    @Column(name = "source")
    private Integer source;

    @Column(name = "gmt_updated")
    private LocalDateTime gmtUpdated;

    @Column(name = "gmt_created")
    private LocalDateTime gmtCreated;

    @Column(name = "ttl")
    private Integer ttl;

    @Column(name = "last_get")
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


