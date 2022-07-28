package cn.wr.collect.sync.model.welfare;

import lombok.Data;

import java.io.Serializable;

@Data
public class RedisManual implements Serializable {
    private static final long serialVersionUID = 5578295581604889675L;

    private String manufacturer;
    private String indications;
}
