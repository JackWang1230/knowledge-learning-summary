package cn.wr.collect.sync.model.gc;

import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class ConfigSkuSource {
    /**
     * skuno
     */
    private String skuNo;

    /**
     * 连锁id
     */
    private Long merchantId;

    /**
     * 管控状态
     */
    private Integer controlStatus;

    public ConfigSkuSource convert(ResultSet rs) throws SQLException {
        this.setSkuNo(ResultSetConvert.getString(rs, 1));
        this.setMerchantId(ResultSetConvert.getLong(rs, 2));
        this.setControlStatus(ResultSetConvert.getInt(rs,3));
        return this;
    }
}
