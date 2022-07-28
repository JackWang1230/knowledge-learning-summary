package cn.wr.collect.sync.model.goodsall;

import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;


@Data
public class GoodsAllData {

    private String tradeCode;

    private Integer merchantId;

    private Integer storeId;

    private String longitude;

    private String latitude;

    private String oldLongitude;

    private String oldLatitude;



    public GoodsAllData() {
    }

    public GoodsAllData(String tradeCode, Integer merchantId, Integer storeId, String longitude, String latitude) {
        this.tradeCode = tradeCode;
        this.merchantId = merchantId;
        this.storeId = storeId;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * 参数转换
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public GoodsAllData convert(ResultSet rs) throws SQLException {
        this.setTradeCode(ResultSetConvert.getString(rs, 1));
        this.setMerchantId(ResultSetConvert.getInt(rs, 2));
        this.setStoreId(ResultSetConvert.getInt(rs, 3));
        this.setLatitude(ResultSetConvert.getString(rs, 4));
        this.setLongitude(ResultSetConvert.getString(rs, 5));
        return this;
    }
}
