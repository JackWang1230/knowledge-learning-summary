package cn.wr.collect.sync.model;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
public class CorrespondField {
    private String table;
    /**
     * key字段
     */
    private List<Field> keyFields;
    /**
     * 单字段
     */
    private List<Field> singleFields;
    /**
     * 组合字段
     */
    private List<Field> multiFields;

    public CorrespondField() {
        this.keyFields = new ArrayList<>();
        this.singleFields = new ArrayList<>();
        this.multiFields = new ArrayList<>();
    }

    public void addKey(Field field) {
        this.keyFields.add(field);
    }

    public void addSingle(Field field) {
        this.singleFields.add(field);
    }

    public void addMulti(Field field) {
        this.multiFields.add(field);
    }
}
