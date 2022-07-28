package cn.wr.collect.sync.model.pricecompare;

import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class Price implements Model {
    private Long id;

    private String uniqueKey;

    private String groupId;

    private String internalId;

    private BigDecimal salePrice;

    private BigDecimal basePrice;

    public Price convert(ResultSet rs) throws SQLException {
        this.id = ResultSetConvert.getLong(rs, 1);
        this.uniqueKey = ResultSetConvert.getString(rs, 2);
        this.groupId = ResultSetConvert.getString(rs, 3);
        this.internalId = ResultSetConvert.getString(rs, 4);
        this.salePrice = ResultSetConvert.getBigDecimal(rs, 5);
        this.basePrice = ResultSetConvert.getBigDecimal(rs, 6);
        return this;
    }
}
