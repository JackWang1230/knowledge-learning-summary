package cn.wr.collect.sync.model;

import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;


@Data
public class CompareData {
    private Integer dbId;
    private String groupId;

    public CompareData convert(ResultSet resultSet) throws SQLException {
        this.setDbId(ResultSetConvert.getInt(resultSet, 1));
        this.setGroupId(ResultSetConvert.getString(resultSet, 2));
        return this;
    }
}
