package cn.wr.collect.sync.model.gc;

import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class DisableStore {
    /**
     * merchant_id+store_id+internal_id
     */
    private String stockNo;

    /**
     * skuno
     */
    private String skuNo;

    /**
     * 连锁id
     */
    private Long merchantId;

    /**
     * 门店id
     */
    private Long storeId;

    /**
     * 商品内码
     */
    private String internalId;

    public DisableStore convert(ResultSet rs) throws SQLException {
        this.setStockNo(ResultSetConvert.getString(rs, 1));
        this.setSkuNo(ResultSetConvert.getString(rs, 2));
        this.setMerchantId(ResultSetConvert.getLong(rs, 3));
        this.setStoreId(ResultSetConvert.getLong(rs, 4));
        this.setInternalId(ResultSetConvert.getString(rs, 5));
        return this;
    }
}
