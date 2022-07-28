package cn.wr.collect.sync.model.kafka;

import java.io.Serializable;

public class TestPartitions implements Serializable {
    Integer id;
    String text;

    public TestPartitions(Integer id, String text) {
        this.id = id;
        this.text = text;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
