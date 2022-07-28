package cn.wr.collect.sync.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class MutationResult implements Serializable {
    private static final long serialVersionUID = 1638796744841411512L;

    /**
     * 操作表名
     */
    private String tableName;

    /**
     * 新纪录
     */
    private Model newRecord;

    /**
     * 原纪录
     */
    private Model oldRecord;

    /**
     * 操作类型
     */
    private String mutationType;

    /**
     * 实体类型
     */
    private Class modelClass;

    public MutationResult() {
        super();
    }

    public MutationResult(String tableName, Model newRecord, Model oldRecord, String mutationType, Class modelClass) {
        this.tableName = tableName;
        this.newRecord = newRecord;
        this.oldRecord = oldRecord;
        this.mutationType = mutationType;
        this.modelClass = modelClass;
    }

    public enum  MutationType{
        INSERT(0),UPDATE(1),DELETE(2);

        private int value;
        MutationType(int value){
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


}
