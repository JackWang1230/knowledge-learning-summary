package cn.wr.collect.sync.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BaseDataInitEvent implements Serializable {
    private static final long serialVersionUID = -3680332661466310789L;
    private String tableName;

    private List<Model> models;

    private Class modelClass;

    public BaseDataInitEvent() {
        super();
    }

    public BaseDataInitEvent(String tableName, List<Model> models, Class modelClass) {
        this.tableName = tableName;
        this.models = models;
        this.modelClass = modelClass;
    }
}
