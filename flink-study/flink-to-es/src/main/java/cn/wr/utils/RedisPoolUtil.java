package cn.wr.utils;

import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import static cn.wr.constants.PropertiesConstants.*;

/**
 * @author RWang
 * @Date 2022/5/25
 */

public class RedisPoolUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisPoolUtil.class);
    private static JedisPool jedisPool = null;
    private static JedisPool jedisGoodsAllPool = null;
    private static JedisPool jedisGoodsCenterPool = null;
    // private static String redisConfigFile = "application.properties";
    // 把redis连接对象放到本地线程中
    private static ThreadLocal<Jedis> local = new ThreadLocal<>();
    private static final int RETRY_CNT = 100;

    private RedisPoolUtil() {

    }

    public enum DataBase {
        Collect,
        Search,
        GoodsCenter
    }

    /**
     * 初始化连接池
     *
     * @author corleone
     * @date 2018年11月27日
     */
    public static void initPool(ParameterTool parameterTool) {
        try {
            // 创建jedis池配置实例
            JedisPoolConfig config = new JedisPoolConfig();
            // 设置池配置项值
            config.setMaxTotal(Integer.valueOf(parameterTool.get(REDIS_POOL_MAXTOTAL)));
            config.setMaxIdle(Integer.valueOf(parameterTool.get(REDIS_POOL_MAXIDLE)));
            config.setMaxWaitMillis(parameterTool.getLong(REDIS_POOL_MAXWAITMILLIS));
            config.setTestOnBorrow(true);
            // 根据配置实例化jedis池
            jedisPool = new JedisPool(config, parameterTool.get(REDIS_IP),
                    parameterTool.getInt(REDIS_PORT),
                    parameterTool.getInt(REDIS_TIMEOUT),
                    null == parameterTool.get(REDIS_PASSWORD) || "".equals(parameterTool.get(REDIS_PASSWORD)) ? null : parameterTool.get(REDIS_PASSWORD),
                    parameterTool.getInt(REDIS_DATABASE));
        }
        catch (Exception e) {
            logger.error("RedisPoolUtil collect 连接池初始化失败, error:{}", e);
        }
    }

    /**
     * 初始化连接池
     *
     * @author corleone
     * @date 2018年11月27日
     */
    public static void initGoodsAllPool(ParameterTool parameterTool) {
        try {
            // 创建jedis池配置实例
            JedisPoolConfig config = new JedisPoolConfig();
            // 设置池配置项值
            config.setMaxTotal(Integer.valueOf(parameterTool.get(REDIS_POOL_MAXTOTAL)));
            config.setMaxIdle(Integer.valueOf(parameterTool.get(REDIS_POOL_MAXIDLE)));
            config.setMaxWaitMillis(parameterTool.getLong(REDIS_POOL_MAXWAITMILLIS));
            config.setTestOnBorrow(true);
            // 根据配置实例化jedis池
            jedisGoodsAllPool = new JedisPool(config, parameterTool.get(REDIS_IP),
                    parameterTool.getInt(REDIS_PORT),
                    parameterTool.getInt(REDIS_TIMEOUT),
                    null == parameterTool.get(REDIS_PASSWORD) || "".equals(parameterTool.get(REDIS_PASSWORD)) ? null : parameterTool.get(REDIS_PASSWORD),
                    parameterTool.getInt(REDIS_GOODS_ALL_DATABASE));
        }
        catch (Exception e) {
            logger.error("RedisPoolUtil goodsall 连接池初始化失败, error:{}", e);
        }
    }

    /**
     * 初始化连接池
     *
     * @author corleone
     * @date 2018年11月27日
     */
    public static void initGoodsCenterPool(ParameterTool parameterTool) {
        try {
            // 创建jedis池配置实例
            JedisPoolConfig config = new JedisPoolConfig();
            // 设置池配置项值
            config.setMaxTotal(Integer.valueOf(parameterTool.get(REDIS_POOL_MAXTOTAL)));
            config.setMaxIdle(Integer.valueOf(parameterTool.get(REDIS_POOL_MAXIDLE)));
            config.setMaxWaitMillis(parameterTool.getLong(REDIS_POOL_MAXWAITMILLIS));
            config.setTestOnBorrow(true);
            // 根据配置实例化jedis池
            jedisGoodsCenterPool = new JedisPool(config, parameterTool.get(REDIS_IP),
                    parameterTool.getInt(REDIS_PORT),
                    parameterTool.getInt(REDIS_TIMEOUT),
                    null == parameterTool.get(REDIS_PASSWORD) || "".equals(parameterTool.get(REDIS_PASSWORD)) ? null : parameterTool.get(REDIS_PASSWORD),
                    parameterTool.getInt(REDIS_GOODS_CENTER_DATABASE));
        }
        catch (Exception e) {
            logger.error("RedisPoolUtil goodsall 连接池初始化失败, error:{}", e);
        }
    }


    /**
     * 获取redis实例
     * @param parameterTool
     * @return
     */
    public static Jedis getInstance(ParameterTool parameterTool) {
        int retry = 0;
        while (retry < RETRY_CNT) {
            try {
                Jedis jedis = getJedis(parameterTool);
                if (null != jedis && jedis.isConnected()) {
                    return jedis;
                }
                Thread.sleep(1000L);
            }
            catch (Exception e) {
                logger.error("RedisPoolUtil getInstance Exception:{}", e);
            }
            retry ++;
        }

        // 重试完次数完成后，尝试重新初始化redis连接池
        initPool(parameterTool);
        return getJedis(parameterTool);
    }

    /**
     * 获取redis实例
     * @param parameterTool
     * @return
     */
    public static Jedis getGoodsAllInstance(ParameterTool parameterTool) {
        while (true) {
            try {
                Jedis jedis = getGoodsAllJedis(parameterTool);
                if (null != jedis) {
                    return jedis;
                }

                logger.error("get instance jedis is null");
                Thread.sleep(1000L);
            }
            catch (Exception e) {
                logger.error("RedisPoolUtil getInstance Exception:{}", e);
            }
        }
    }

    /**
     * 获取redis实例
     * @param parameterTool
     * @return
     */
    public static Jedis getGoodsCenterInstance(ParameterTool parameterTool) {
        while (true) {
            try {
                Jedis jedis = getGoodsCenterJedis(parameterTool);
                if (null != jedis) {
                    return jedis;
                }

                logger.error("get instance jedis is null");
                Thread.sleep(1000L);
            }
            catch (Exception e) {
                logger.error("RedisPoolUtil getInstance Exception:{}", e);
            }
        }
    }

    private static Jedis getJedis(ParameterTool parameterTool) {
        if (jedisPool == null || jedisPool.isClosed()) {
            synchronized (RedisPoolUtil.class) {
                if (jedisPool == null || jedisPool.isClosed()) {
                    initPool(parameterTool);
                }
            }
        }
        try {
            return jedisPool.getResource();
        } catch (JedisConnectionException e) {
            logger.error("redis 获取实例失败，error:{}", e);
        }
        return null;
    }

    private static Jedis getGoodsAllJedis(ParameterTool parameterTool) {
        if (jedisGoodsAllPool == null) {
            synchronized (RedisPoolUtil.class) {
                if (jedisGoodsAllPool == null) {
                    initGoodsAllPool(parameterTool);
                }
            }
        }
        try {
            return jedisGoodsAllPool.getResource();
        } catch (JedisConnectionException e) {
            logger.error("redis goodsall 获取实例失败，error:{}", e);
        }
        return null;
    }

    private static Jedis getGoodsCenterJedis(ParameterTool parameterTool) {
        if (jedisGoodsCenterPool == null) {
            synchronized (RedisPoolUtil.class) {
                if (jedisGoodsCenterPool == null) {
                    initGoodsCenterPool(parameterTool);
                }
            }
        }
        try {
            return jedisGoodsCenterPool.getResource();
        } catch (JedisConnectionException e) {
            logger.error("redis goods center 获取实例失败，error:{}", e);
        }
        return null;
    }

    public static Jedis instance(DataBase dataBase, ParameterTool tool) {
        switch (dataBase) {
            case Collect:
                return getInstance(tool);
            case Search:
                return getGoodsAllInstance(tool);
            case GoodsCenter:
                return getGoodsCenterInstance(tool);
            default:
                logger.error("RedisPoolUtil database is not valid: {}", dataBase);
                break;
        }
        return null;
    }

    public static void closeConn(DataBase dataBase, Jedis jedis) {
        switch (dataBase) {
            case Collect:
                closeConn(jedis);
                break;
            case Search:
                closeGoodsAllConn(jedis);
                break;
            case GoodsCenter:
                closeGoodsCenterConn(jedis);
                break;
            default:
                logger.error("RedisPoolUtil database is not valid: {}", dataBase);
                break;
        }
    }

    /**
     * 关闭连接
     * @param jedis
     */
    public static void closeConn(Jedis jedis) {
        try {
            if (jedis != null) {
                // 过期方法，高并发情况下，redis异常会导致连接释放不完全，多个线程共享连接，导致数据错乱
                // jedisPool.returnResource(jedis);
                jedis.close();
            }
        }
        catch (Exception e) {
            logger.error("redis 关闭连接异常，error:{}", e);
        }
    }

    /**
     * 关闭连接
     * @param jedis
     */
    public static void closeGoodsAllConn(Jedis jedis) {
        try {
            if (jedis != null) {
                // 过期方法，高并发情况下，redis异常会导致连接释放不完全，多个线程共享连接，导致数据错乱
                // jedisGoodsAllPool.returnResource(jedis);
                jedis.close();
            }
        }
        catch (Exception e) {
            logger.error("redis 关闭连接异常，error:{}", e);
        }
    }

    /**
     * 关闭连接
     * @param jedis
     */
    public static void closeGoodsCenterConn(Jedis jedis) {
        try {
            if (jedis != null) {
                // 过期方法，高并发情况下，redis异常会导致连接释放不完全，多个线程共享连接，导致数据错乱
                // jedisGoodsAllPool.returnResource(jedis);
                jedis.close();
            }
        }
        catch (Exception e) {
            logger.error("redis 关闭连接异常，error:{}", e);
        }
    }

    /**
     * 关闭redis链接池
     */
    public static void closePool() {
        logger.info("RedisPoolUtil close pool success");
        try {
            if (jedisPool != null) {
                jedisPool.close();
            }
        }
        catch (Exception e) {
            logger.error("redis pool 关闭连接异常，error:{}", e);
        }

    }
}
