package cn.wr.collect.sync.utils;

import cn.wr.collect.sync.model.Cache;
import cn.wr.collect.sync.model.gc.BaseGoods;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MemoryCacheUtil<T> {
    private static final Logger log = LoggerFactory.getLogger(MemoryCacheUtil.class);

    /**
     * 缓存集合
     */
    private static ConcurrentHashMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

    /**
     * 缓存失效间隔（ms）
     */
    private static final long EXPIRE_INTERVAL = 10 * 1000;

    /**
     * 缓存最大数量
     */
    private static final long CACHE_MAX_SIZE = 1000000;

    static {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> expire(EXPIRE_INTERVAL),
                0, EXPIRE_INTERVAL, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T get(String key) {
        Cache<T> cache = (Cache<T>) cacheMap.get(key);
        if (Objects.isNull(cache))
            return null;
        return cache.getValue();
    }

    /**
     * 缓存
     * @param key
     * @param t
     * @param <T>
     */
    public static <T> void put(String key, T t) {
        if (cacheMap.size() > CACHE_MAX_SIZE) {
            return;
        }
        cacheMap.put(key, new Cache<>(System.currentTimeMillis(), t));
    }

    /**
     * 清理过期数据
     * @param expireTime
     */
    private static synchronized void expire(long expireTime) {
        log.info("MemoryCacheUtil expire start, expireTime: {}", expireTime);
        cacheMap.values().removeIf(item -> Objects.isNull(item) || item.getTime() >= expireTime);
        if (cacheMap.size() > CACHE_MAX_SIZE) {
            expire(calculateExpireTime(EXPIRE_INTERVAL >> 2));
        }
        log.info("MemoryCacheUtil expire complete, expireTime: {}", expireTime);
    }

    /**
     * 获取失效时间
     * @param interval
     * @return
     */
    private static long calculateExpireTime(long interval) {
        return System.currentTimeMillis() - interval;
    }

    public static void main(String[] args) throws Exception {
        BaseGoods baseGoods = new BaseGoods();
        baseGoods.setId(1L);
        baseGoods.setApprovalNumber("approvalNumber");
        MemoryCacheUtil.put("test", baseGoods);
        BaseGoods goods1 = MemoryCacheUtil.get("test");
        System.out.println("> " + JSON.toJSONString(goods1));

        MemoryCacheUtil.put("test", baseGoods);
        BaseGoods goodss = MemoryCacheUtil.get("test");
        System.out.println("> " + JSON.toJSONString(goods1));

        Thread.sleep(1000 * 15);

        BaseGoods goods2 = MemoryCacheUtil.get("test");
        System.out.println(">> " + JSON.toJSONString(goods2));

        MemoryCacheUtil.put("test", baseGoods);

        BaseGoods goods3 = MemoryCacheUtil.get("test");
        System.out.println(">>> " + JSON.toJSONString(goods3));
    }
}
