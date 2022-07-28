package cn.wr.collect.sync.model.welfare;

import lombok.Data;

import java.io.Serializable;

@Data
public class RedisGoodsSpu implements Serializable {
    private static final long serialVersionUID = -3668559246290437541L;
    private RedisManual redisManual;
}
