package cn.wr.collect.sync.model.chain;

import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Data
public class Goods {
    private Long id;
    private String internalId;
    private String commonName;
    private String tradeCode;
    private String approvalNumber;
    private String form;
    private String pack;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer status;
    private LocalDateTime goodsCreateTime;
    private LocalDateTime goodsUpdateTime;
    private String manufacturer;

    /**
     * 参数转换
     * @param rs
     * @return
     * @throws SQLException
     */
    public Goods convert(ResultSet rs) throws SQLException {
        this.setId(ResultSetConvert.getLong(rs,1));
        this.setInternalId(ResultSetConvert.getString(rs,2));
        this.setCommonName(ResultSetConvert.getString(rs,3));
        this.setTradeCode(ResultSetConvert.getString(rs,4));
        this.setApprovalNumber(ResultSetConvert.getString(rs,5));
        this.setForm(ResultSetConvert.getString(rs,6));
        this.setPack(ResultSetConvert.getString(rs,7));
        this.setPrice(ResultSetConvert.getBigDecimal(rs,8));
        this.setCreatedAt(ResultSetConvert.getLocalDateTime(rs,9));
        this.setUpdatedAt(ResultSetConvert.getLocalDateTime(rs,10));
        this.setStatus(ResultSetConvert.getInt(rs,11));
        this.setGoodsCreateTime(ResultSetConvert.getLocalDateTime(rs,12));
        this.setGoodsUpdateTime(ResultSetConvert.getLocalDateTime(rs,13));
        this.setManufacturer(ResultSetConvert.getString(rs,14));
        return this;
    }
}
