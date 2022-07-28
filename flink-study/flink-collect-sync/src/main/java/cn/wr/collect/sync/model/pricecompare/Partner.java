package cn.wr.collect.sync.model.pricecompare;

import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class Partner {
    private Long merchantId;

    private Long storeId;

    private String groupId;

    private String dbName;

    public Partner convert(ResultSet rs) throws SQLException {
        this.merchantId = ResultSetConvert.getLong(rs, 1);
        this.storeId = ResultSetConvert.getLong(rs,2);
        this.groupId = ResultSetConvert.getString(rs, 3);
        this.dbName = ResultSetConvert.getString(rs,4);
        return this;
    }
}
