package cn.wr.collect.sync.model.gc;

import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class GoodsManagementStore {

    private String stockNo;
    private String skuNo;
    private Long merchantId;
    private Long StoreId;
    private String internalId;

    public GoodsManagementStore convert(ResultSet rs) throws SQLException {
        this.setStockNo(ResultSetConvert.getString(rs, 1));
        this.setSkuNo(ResultSetConvert.getString(rs, 2));
        this.setMerchantId(ResultSetConvert.getLong(rs, 3));
        this.setStoreId(ResultSetConvert.getLong(rs, 4));
        this.setInternalId(ResultSetConvert.getString(rs, 5));
        return this;
    }


}
