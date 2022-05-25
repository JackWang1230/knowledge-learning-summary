package cn.wr.redis;

import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisConfigBase;
import org.apache.flink.streaming.connectors.redis.common.container.RedisCommandsContainer;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple3;

/**
 * @author RWang
 * @Date 2022/5/23
 */

public class RedisSinkDemo<IN> extends RedisSink<IN> {
    private static final long serialVersionUID = 6891852383379224459L;

    private static final Logger LOG = LoggerFactory.getLogger(RedisSink.class);
//    private String additionalKey;
    private RedisDemoMapper<IN> redisSinkMapper;
    private RedisCommand redisCommand;
    private FlinkJedisConfigBase flinkJedisConfigBase;
    private RedisCommandsContainer redisCommandsContainer;

    public RedisSinkDemo(FlinkJedisConfigBase flinkJedisConfigBase, RedisDemoMapper<IN> redisSinkMapper) {
        super(flinkJedisConfigBase, (RedisMapper<IN>) redisSinkMapper);

        Preconditions.checkNotNull(flinkJedisConfigBase, "Redis connection pool config should not be null");
        Preconditions.checkNotNull(redisSinkMapper, "Redis Mapper can not be null");
        Preconditions.checkNotNull(redisSinkMapper.getCommandDescription(), "Redis Mapper data type description can not be null");
        this.flinkJedisConfigBase = flinkJedisConfigBase;
        this.redisSinkMapper = redisSinkMapper;
        RedisCommandDescription redisCommandDescription = redisSinkMapper.getCommandDescription();
        this.redisCommand = redisCommandDescription.getCommand();
//        this.additionalKey = redisCommandDescription.getAdditionalKey();
    }

    @Override
    public void invoke(IN input, Context context) throws Exception {


        String key = this.redisSinkMapper.getKeyFromData(input);
        String value = this.redisSinkMapper.getValueFromData(input);
        String additionKey = this.redisSinkMapper.getAdditionKey(input);

        switch(this.redisCommand) {
            case RPUSH:
                this.redisCommandsContainer.rpush(key, value);
                break;
            case LPUSH:
                this.redisCommandsContainer.lpush(key, value);
                break;
            case SADD:
                this.redisCommandsContainer.sadd(key, value);
                break;
            case SET:
                this.redisCommandsContainer.set(key, value);
                break;
            case PFADD:
                this.redisCommandsContainer.pfadd(key, value);
                break;
            case PUBLISH:
                this.redisCommandsContainer.publish(key, value);
                break;
            case ZADD:
                this.redisCommandsContainer.zadd(additionKey, value, key);
                break;
            case HSET:
                this.redisCommandsContainer.hset(additionKey, key, value);
                break;
            default:
                throw new IllegalArgumentException("Cannot process such data type: " + this.redisCommand);
        }
    }
}
