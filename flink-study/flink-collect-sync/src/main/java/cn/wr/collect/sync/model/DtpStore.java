package cn.wr.collect.sync.model;

import cn.wr.collect.sync.model.gc.ConfigSku;
import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class DtpStore {
    /**
     * db_id
     */
    private Integer dbId;
    /**
     * store_id
     */
    private Integer storeId;

    /**
     * 参数转换
     * @param rs
     * @return
     * @throws SQLException
     */
    public DtpStore convert(ResultSet rs) throws SQLException {
        this.setDbId(ResultSetConvert.getInt(rs, 1));
        this.setStoreId(ResultSetConvert.getInt(rs, 2));
        return this;
    }
}
