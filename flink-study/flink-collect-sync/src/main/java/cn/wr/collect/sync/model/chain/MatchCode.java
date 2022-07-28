package cn.wr.collect.sync.model.chain;

import lombok.Data;

@Data
public class MatchCode {

    /**
     * 连锁库类型 1-普通连锁库 2-连锁单体库
     */
    private Integer dbType;
    /**
     * 连锁库名
     */
    private String dbname;
    /**
     * 商品内码
     */
    private String internalId;
    /**
     * 标准条码
     */
    private String tradeCode;

    public MatchCode(Integer dbType, String dbname, String internalId, String tradeCode) {
        this.dbType = dbType;
        this.dbname = dbname;
        this.internalId = internalId;
        this.tradeCode = tradeCode;
    }
}
