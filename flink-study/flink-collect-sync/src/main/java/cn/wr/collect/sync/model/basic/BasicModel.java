package cn.wr.collect.sync.model.basic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicModel<T> implements Serializable {
    private static final long serialVersionUID = -2306782034954557761L;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 增/删/改 操作
     */
    private String operate;

    /**
     * 数据
     */
    private T data;

    /**
     * 变更前数据
     */
    private T old;

    /**
     * 变更字段
     */
    private List<String> modFieldList;

    /**
     * 同步类型
     * 0：指定字段同步
     * 1：全量同步
     */
    private Integer syncType = 1;

    public BasicModel(String tableName, String operate, T data) {
        this.tableName = tableName;
        this.operate = operate;
        this.data = data;
    }

    /**
     * 自定义构造方法
     * @param tableName
     * @param operate
     * @param data
     * @param modFieldList
     */
    public BasicModel(String tableName, String operate, T data, List<String> modFieldList) {
        this.tableName = tableName;
        this.operate = operate;
        this.data = data;
        this.modFieldList = modFieldList;
    }

    /**
     * 自定义构造方法
     * @param tableName
     * @param operate
     * @param data
     * @param modFieldList
     * @param syncType
     */
    public BasicModel(String tableName, String operate, T data, List<String> modFieldList, Integer syncType) {
        this.tableName = tableName;
        this.operate = operate;
        this.data = data;
        this.modFieldList = modFieldList;
        this.syncType = syncType;
    }
}
