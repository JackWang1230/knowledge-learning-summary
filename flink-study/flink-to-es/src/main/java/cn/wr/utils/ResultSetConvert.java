package cn.wr.utils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * jdbc ResultSet 参数转换
 */
public class ResultSetConvert {
    public static String getString(ResultSet rs, int index) throws SQLException {
        return rs.getString(index);
    }

    public static Integer getInt(ResultSet rs, int index) throws SQLException {
        return null == rs.getObject(index) ? null : rs.getInt(index);
    }
    public static Long getLong(ResultSet rs, int index) throws SQLException {
        return null == rs.getObject(index) ? null : rs.getLong(index);
    }

    public static Double getDouble(ResultSet rs, int index) throws SQLException {
        return null == rs.getObject(index) ? null : rs.getDouble(index);
    }

    public static Float getFloat(ResultSet rs, int index) throws SQLException {
        return null == rs.getObject(index) ? null : rs.getFloat(index);
    }

    public static BigDecimal getBigDecimal(ResultSet rs, int index) throws SQLException {
        return null == rs.getObject(index) ? null : rs.getBigDecimal(index);
    }

    public static LocalDateTime getLocalDateTime(ResultSet rs, int index) throws SQLException {
        return null == rs.getObject(index) ? null : rs.getTimestamp(index).toLocalDateTime();
    }
}
