package cn.wr.collect.sync.model.middledb;

import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PgcStoreInfoShort implements Serializable {
    private static final long serialVersionUID = 956989160697921127L;
    /**
     * dbId
     */
    private Integer dbId;
    /**
     * 连锁id
     */
    private Integer organizationId;
    /**
     * 连锁名称
     */
    private String organizationName;

    public PgcStoreInfoShort convert(ResultSet rs) throws SQLException {
        this.setDbId(ResultSetConvert.getInt(rs, 1));
        this.setOrganizationId(ResultSetConvert.getInt(rs, 2));
        this.setOrganizationName(ResultSetConvert.getString(rs, 3));
        return this;
    }
}
