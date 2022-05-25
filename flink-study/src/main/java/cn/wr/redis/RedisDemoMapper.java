package cn.wr.redis;

import org.apache.flink.api.common.functions.Function;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;

import java.io.Serializable;

public interface RedisDemoMapper<T> extends Function, Serializable {


    RedisCommandDescription getCommandDescription();

    String getKeyFromData(T var1);

    String getValueFromData(T var1);

    String getAdditionKey(T var1);
}
