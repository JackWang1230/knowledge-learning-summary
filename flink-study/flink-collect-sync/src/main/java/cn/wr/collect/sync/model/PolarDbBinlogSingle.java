package cn.wr.collect.sync.model;

import lombok.Data;

import java.util.Map;

@Data
public class PolarDbBinlogSingle extends PolarDbBinlog {
    private static final long serialVersionUID = 4657506288454453157L;

    /**
     * 更新后数据
     */
    private Map<String, Object> data;

    /**
     * 更新前数据
     * 只包含更新字段，未更新字段不在集合中
     */
    private Map<String, Object> old;

    public PolarDbBinlogSingle transfer(PolarDbBinlogBatch batch) {
        this.setId(batch.getId());
        this.id = batch.getId();
        this.database = batch.getDatabase();
        this.table = batch.getTable();
        this.pkNames = batch.getPkNames();
        this.isDdl = batch.getIsDdl();
        this.type = batch.getType();
        this.es = batch.getEs();
        this.ts = batch.getTs();
        this.sql = batch.getSql();
        this.sqlType = batch.getSqlType();
        this.mysqlType = batch.getMysqlType();
        return this;
    }
}
