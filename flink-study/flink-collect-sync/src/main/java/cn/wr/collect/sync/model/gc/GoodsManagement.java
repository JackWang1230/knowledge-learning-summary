package cn.wr.collect.sync.model.gc;

import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;


@Data
public class GoodsManagement {

    private String skuNo;

    private long merchantId;

    private int onlineDisableStatus;


    public GoodsManagement convert(ResultSet rs) throws SQLException {
        this.setSkuNo(ResultSetConvert.getString(rs, 1));
        this.setMerchantId(ResultSetConvert.getLong(rs, 2));
        this.setOnlineDisableStatus(ResultSetConvert.getInt(rs,3));
        return this;
    }
}
