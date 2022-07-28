package cn.wr.collect.sync.model;

import java.io.Serializable;

public class MetricItem<T> implements Serializable {
    private static final long serialVersionUID = -8550766631285969353L;
    private String tableName;
    private String operate;
    private T item;

    public MetricItem() {
    }

    public MetricItem(String tableName, String operate, T item) {
        this.tableName = tableName;
        this.operate = operate;
        this.item = item;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "MetricItem{" +
                "tableName='" + tableName + '\'' +
                ", operate='" + operate + '\'' +
                ", item=" + item +
                '}';
    }
}
