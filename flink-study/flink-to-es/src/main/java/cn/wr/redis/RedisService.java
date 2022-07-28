package cn.wr.redis;

import cn.wr.utils.RedisPoolUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author RWang
 * @Date 2022/7/20
 */

public class RedisService {

    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);
    private final ParameterTool parameterTool;
    public RedisService(ParameterTool parameterTool) {
        this.parameterTool = parameterTool;
    }


    /**
     * 清空初始化数据
     */
    public void clearRedis(String key) {
        while (true) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                ScanParams sp = new ScanParams();
                sp.match(key + "*");
                sp.count(100000);
                String cursor = "0";
                while (true) {
                    ScanResult<String> scan = jedis.scan(cursor, sp);
                    List<String> result = scan.getResult();
                    cursor = scan.getStringCursor();
                    if (CollectionUtils.isNotEmpty(result)) {
                        Long del = jedis.del(result.toArray(new String[result.size()]));
                        logger.info("RedisService clearRedis size:{} del:{}", result.size(), del);
                    }
                    if ("0".equals(cursor)) {
                        return;
                    }
                }
            }
            catch (Exception e) {
                logger.error("RedisService clearRedis Exception:{}", e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
                logger.info("RedisService clearRedis complete key:{}", key);
            }
        }
    }

    public List<String> scan(RedisPoolUtil.DataBase dataBase, String keyPattern) {
        Jedis jedis = null;
        List<String> keyList = new ArrayList<>();
        try {
            jedis = RedisPoolUtil.instance(dataBase, parameterTool);
            ScanParams sp = new ScanParams();
            sp.match(keyPattern + "*");
            sp.count(100000);
            String cursor = "0";
            while (true) {
                ScanResult<String> scan = jedis.scan(cursor, sp);
                List<String> result = scan.getResult();
                cursor = scan.getStringCursor();
                if (CollectionUtils.isNotEmpty(result)) {
                    keyList.addAll(result);
                }
                if ("0".equals(cursor)) {
                    return keyList;
                }
            }
        }
        catch (Exception e) {
            logger.error("RedisService scan Exception:{}", e);
        }
        finally {
            RedisPoolUtil.closeConn(jedis);
            logger.info("RedisService scan complete key:{}", keyPattern);
        }
        return keyList;
    }

    /**
     * 设值
     * @param key
     * @param obj
     */
    public String set(String key, String obj) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                return jedis.set(key, obj);
            }
            catch (Exception e) {
                logger.error("RedisService set key:{} Exception:{}", key, e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * 设值
     * @param key
     * @param obj
     */
    public String set(String key, String obj, String timeType, long time) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                Boolean exists = jedis.exists(key);
                if (null != exists && exists) {
                    return jedis.set(key, obj, "XX", timeType, time);
                }
                return jedis.set(key, obj, "NX", timeType, time);
            }
            catch (Exception e) {
                logger.error("RedisService set key:{} Exception:{}", key, e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * 取值
     * @param key
     * @return
     */
    public String get(String key) {

        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                return jedis.get(key);
            }
            catch (Exception e) {
                logger.error("RedisService get key:{} Exception:{}", key, e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * 删除
     * @param key
     */
    public Long delete(String key) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                Long del = jedis.del(key);
                logger.info("RedisService deleteSet key:{}, delRow:{}", key, del);
                return del;
            }
            catch (Exception e) {
                logger.error("RedisService delete Exception:{}", e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * 删除
     * @param key
     */
    public Long delete(RedisPoolUtil.DataBase dataBase, String key) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.instance(dataBase, parameterTool);
                Long del = jedis.del(key);
                logger.info("RedisService deleteSet key:{}, delRow:{}", key, del);
                return del;
            }
            catch (Exception e) {
                logger.error("RedisService delete Exception:{}", e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * 删除
     * @param keys
     */
    public Long delete(String[] keys) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                Long del = jedis.del(keys);
                logger.info("RedisService deleteSet key:{}, delRow:{}", keys, del);
                return del;
            }
            catch (Exception e) {
                logger.error("RedisService delete Exception:{}", e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * 删除set
     *
     * @param key
     */
    public Long deleteSet(String key, List<String> list) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                Long srem = jedis.srem(key, list.toArray(new String[list.size()]));
                logger.info("RedisService deleteSet key:{}, obj:{} delRow:{}", key, list, srem);
                return srem;
            }
            catch (Exception e) {
                logger.error("RedisService deleteSet key:{} Exception:{}", key, e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * 删除set
     *
     * @param key
     */
    public Long deleteSet(RedisPoolUtil.DataBase dataBase, String key, List<String> list) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.instance(dataBase, parameterTool);
                Long srem = jedis.srem(key, list.toArray(new String[list.size()]));
                logger.info("RedisService deleteSet dataBase:{} key:{}, obj:{} delRow:{}", dataBase, key, list, srem);
                return srem;
            }
            catch (Exception e) {
                logger.error("RedisService deleteSet key:{} Exception:{}", key, e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * 删除set
     * @param key
     * @param value
     */
    public Long deleteSet(String key, String value) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                Long srem = jedis.srem(key, value);
                logger.info("RedisService deleteSet key:{}, value:{} delRow:{}", key, value, srem);
                return srem;
            }
            catch (Exception e) {
                logger.error("RedisService deleteSet Exception:{}", e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * 删除set
     * @param key
     * @param value
     */
    public Long deleteSet(RedisPoolUtil.DataBase dataBase, String key, String value) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.instance(dataBase, parameterTool);
                Long srem = jedis.srem(key, value);
                logger.info("RedisService deleteSet db:{} key:{}, value:{} delRow:{}", dataBase, key, value, srem);
                return srem;
            }
            catch (Exception e) {
                logger.error("RedisService deleteSet Exception:{}", e);
            }
            finally {
                RedisPoolUtil.closeConn(dataBase, jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * 新增set
     *
     * @param key
     * @param obj
     */
    public Long addSet(String key, String obj) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                return jedis.sadd(key, obj);
            }
            catch (Exception e) {
                logger.error("RedisService addSet key:{} Exception:{}", key, e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * 新增set
     *
     * @param key
     * @param obj
     */
    public Long addSet(String key, List<String> obj) {
        if (CollectionUtils.isEmpty(obj)) {
            return null;
        }
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                return jedis.sadd(key, obj.toArray(new String[obj.size()]));
            }
            catch (Exception e) {
                logger.error("RedisService addSet key:{} Exception:{}", key, e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * smember
     * @param key
     * @return
     */
    public Set<String> smember(String key) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                return jedis.smembers(key);
            }
            catch (Exception e) {
                logger.error("RedisService smembers key:{} Exception:{}", key, e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * smember
     * @param key
     * @return
     */
    public Set<String> smember(RedisPoolUtil.DataBase dataBase, String key) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.instance(dataBase, parameterTool);
                return jedis.smembers(key);
            }
            catch (Exception e) {
                logger.error("RedisService smembers db:{} key:{} Exception:{}", dataBase, key, e);
            }
            finally {
                RedisPoolUtil.closeConn(dataBase, jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * smember
     * @param key
     * @return
     */
    public List<String> srandmember(String key, int end) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                return jedis.srandmember(key, end);
            }
            catch (Exception e) {
                logger.error("RedisService lrange key:{} Exception:{}", key, e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * 查询set
     * @param key
     * @return
     */
 /*   public List<PgConcatParams> smembers(String key) {
        Set<String> smembers = smember(key);
        if (CollectionUtils.isEmpty(smembers)) {
            LOGGER.info("RedisService smembers is empty key:{}", key);
            return Collections.emptyList();
        }
        List<PgConcatParams> collect = smembers.stream().map(e -> JSON.parseObject(e, PgConcatParams.class))
                .collect(Collectors.toList());
        LOGGER.info("RedisService smembers key:{} json:{}", key, JSON.toJSONString(collect));
        return collect;
    }

    *//**
     * 查询set
     * @param key
     * @return
     *//*
    public List<PgConcatParams> srandmembers(String key, int count) {
        List<String> srandmember = srandmember(key, count);
        if (CollectionUtils.isEmpty(srandmember)) {
            LOGGER.info("RedisService srandmember is empty key:{}", key);
            return Collections.emptyList();
        }
        List<PgConcatParams> collect = srandmember.stream().map(e -> JSON.parseObject(e, PgConcatParams.class))
                .collect(Collectors.toList());
        LOGGER.info("RedisService srandmember key:{} json:{}", key, JSON.toJSONString(collect));
        return collect;
    }

    *//**
     * 查询set
     * @param key
     * @return
     *//*
    public List<GoodsCenterTime> srandMembersGoodsCenter(String key, int count) {
        List<String> srandmember = srandmember(key, count);
        if (CollectionUtils.isEmpty(srandmember)) {
            LOGGER.info("RedisService srandmember is empty key:{}", key);
            return Collections.emptyList();
        }
        List<GoodsCenterTime> collect = srandmember.stream().map(e -> JSON.parseObject(e, GoodsCenterTime.class))
                .collect(Collectors.toList());
        LOGGER.info("RedisService srandMembersGoodsCenter key:{} json:{}", key, JSON.toJSONString(collect));
        return collect;
    }*/

    /**
     * 删除 hash
     *
     * @param key
     * @param fields
     */
    public Long deleteHash(String key, String fields) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                Long hdel = jedis.hdel(key, fields);
                logger.info("RedisService deleteHash key:{}, obj:{}, delRow:{}", key, fields, hdel);
                return hdel;
            }
            catch (Exception e) {
                logger.error("RedisService deleteHash key:{} Exception:{}", key, e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * 新增hash
     *
     * @param key
     * @param field
     * @param value
     */
    public Long addHash(String key, String field, String value) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                return jedis.hset(key, field, value);
            }
            catch (Exception e) {
                logger.error("RedisService addHash key:{} Exception:{}", key, e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * hash 自增
     *
     * @param key
     * @param field
     * @param value
     */
    public Long hincrBy(String key, String field, long value) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                return jedis.hincrBy(key, field, value);
            } catch (Exception e) {
                logger.error("RedisService hincrBy key:{} Exception:{}", key, e);
            } finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i++;
        }
        return null;
    }

    /**
     * 设置key 超时
     * @param key
     * @param time
     * @return
     */
    public Long expire(String key, int time) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                return jedis.expire(key, time);
            } catch (Exception e) {
                logger.error("RedisService exists key:{} Exception:{}", key, e);
            } finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i++;
        }
        return null;
    }

    /**
     * 判断key是否存在
     * @param key
     * @return
     */
    public Boolean exists(String key) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                return jedis.exists(key);
            } catch (Exception e) {
                logger.error("RedisService exists key:{} Exception:{}", key, e);
            } finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i++;
        }
        return null;
    }

    /**
     * 新增hash
     *
     * @param key
     * @param value
     */
    public String addHash(String key, Map<String, String> value) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                return jedis.hmset(key, value);
            }
            catch (Exception e) {
                logger.error("RedisService addHash Exception:{}", e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * hget
     * @param key
     * @param field
     * @return
     */
    private String hget(String key, String field) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                return jedis.hget(key, field);
            }
            catch (Exception e) {
                logger.error("RedisService hget key:{} error:{}", key, e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * hgetall
     * @param key
     * @return
     */
    private Map<String, String> hgetAll(String key) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                return jedis.hgetAll(key);
            }
            catch (Exception e) {
                logger.error("RedisService hgetAll key:{} error:{}", key, e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * hkeys
     * @param key
     * @return
     */
    private Set<String> hkeys(String key) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                return jedis.hkeys(key);
            }
            catch (Exception e) {
                logger.error("RedisService hkeys key:{} error:{}", key, e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

   /* *//**
     * 查询
     *
     * @param dbId
     * @param groupId
     * @return
     *//*
    public List<PartnerStoresAll> queryPartnerStoresAll(Integer dbId, String groupId) {
        if (Objects.isNull(dbId) || StringUtils.isBlank(groupId)) {
            return Collections.emptyList();
        }
        String key = RedisConstant.COLLECT_CACHE_GC_PARTNER_STORES_ALL + "dbId" + dbId;
        String field = "groupId" + groupId;

        String json = null;
        try {
            *//*json = get(RedisConstant.COLLECT_CACHE_GC_GOODS_MANUAL + "approvalNumber" + approvalNumber);
            return JSON.parseObject(json, GoodsManual.class);*//*
            json = hget(key, field);
            List<PartnerStoresAll> list = JSON.parseArray(json, PartnerStoresAll.class);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryPartnerStoresAll key:{} field:{} json:{}", key, field, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }

    *//**
     * 查询 连锁门店
     * gc_partner_stores_all
     *
     * @param dbId
     * @return
     *//*
    public List<PartnerStoresAll> queryPartnerStoresAll(Integer dbId) {
        if (Objects.isNull(dbId)) {
            return Collections.emptyList();
        }
        Map<String, String> map = null;
        try {
            map = hgetAll(RedisConstant.COLLECT_CACHE_GC_PARTNER_STORES_ALL + "dbId" + dbId);
            // LOGGER.info("query partner stores map: {}", JSON.toJSONString(map));
            if (Objects.isNull(map)) {
                return Collections.emptyList();
            }
            return map.entrySet().parallelStream()
                    .flatMap(e ->  JSON.parseArray(e.getValue(), PartnerStoresAll.class).stream())
                    .collect(Collectors.toList());
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryPartnerStoresAll params:{} json:{}", dbId, map);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return Collections.emptyList();
    }

    *//**
     * 查询连锁门店数据
     * @param dbId
     * @return
     *//*
    public List<PartnerStores> queryPartnerStores(Integer dbId) {
        if (Objects.isNull(dbId)) {
            return Collections.emptyList();
        }
        String key = COLLECT_CACHE_PARTNER_STORES + "dbId" + dbId;
        Map<String, String> map = null;
        try {
            map = hgetAll(key);
            if (Objects.isNull(map)) {
                return Collections.emptyList();
            }
            return map.entrySet().parallelStream()
                    .map(e ->  JSON.parseObject(e.getValue(), PartnerStores.class))
                    .collect(Collectors.toList());
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryPartnerStores key:{} json:{}", key, map);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return Collections.emptyList();
    }

    *//**
     * 查询连锁门店数据
     * @param dbId
     * @return
     *//*
    public List<PartnerStores> queryPartnerStores(Integer dbId, String groupId) {
        if (Objects.isNull(dbId)) {
            return Collections.emptyList();
        }
        String key = COLLECT_CACHE_PARTNER_STORES + "dbId" + dbId + "groupId" + groupId;
        Map<String, String> map = null;
        try {
            map = hgetAll(key);
            if (Objects.isNull(map)) {
                return Collections.emptyList();
            }
            return map.values().stream()
                    .map(s -> JSON.parseObject(s, PartnerStores.class))
                    .collect(Collectors.toList());
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryPartnerStores key:{} json:{}", key, map);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return Collections.emptyList();
    }

    *//**
     * 查询 连锁db
     * partners
     * @param merchantId
     * @return
     *//*
    public List<Partners> queryPartners(Integer merchantId) {
        if (Objects.isNull(merchantId)) {
            return Collections.emptyList();
        }
        Map<String, String> map = null;
        try {
            map = hgetAll(COLLECT_CACHE_PARTNERS + "organizationId" + merchantId);
            if (Objects.isNull(map)) {
                return Collections.emptyList();
            }
            return map.entrySet().parallelStream()
                    .map(e ->  JSON.parseObject(e.getValue(), Partners.class))
                    .collect(Collectors.toList());
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryPartners params:{} json:{}, e", merchantId, map, e);
        }
        return Collections.emptyList();
    }


    *//**
     * 查询 标准商品说明书
     * gc_goods_manual
     *
     * @param approvalNumber
     * @return
     *//*
    public GoodsManual queryGcGoodsManual(String approvalNumber) {
        if (StringUtils.isBlank(approvalNumber)) {
            return null;
        }
        String json = null;
        try {
            json = get(RedisConstant.COLLECT_CACHE_GC_GOODS_MANUAL + "approvalNumber" + approvalNumber);
            return JSON.parseObject(json, GoodsManual.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryGcGoodsManual params:{} json:{}", approvalNumber, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }


    *//**
     * 查询
     * gc_base_nootc
     *
     * @param approvalNumber
     * @return
     *//*
    public BaseNootc queryGcBaseNootc(String approvalNumber) {
        if (StringUtils.isBlank(approvalNumber)) {
            return null;
        }
        String json = null;
        try {
            json = get(RedisConstant.COLLECT_CACHE_GC_BASE_NOOTC + "approvalNumber" + approvalNumber);
            return JSON.parseObject(json, BaseNootc.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryGcGoodsDosage params:{} json:{}", approvalNumber, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }

    *//**
     * 连锁白名单查询
     *
     * stock_merchant
     *
     *//*
    public StockMerchant queryStockMerchant(Integer rootId, Integer organizationId ) {
        if (Objects.isNull(rootId) || Objects.isNull(organizationId)) {
            return null;
        }

        String json = null;
        try {
            json = get(RedisConstant.COLLECT_CACHE_STOCK_MERCHANT + "rootId" + rootId + "organizationId" + organizationId);
            return JSON.parseObject(json, StockMerchant.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryStockMerchant rootId:{} organizationId:{} json:{}", rootId, organizationId, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;

    }




    *//**
     * 查询商品用药说明
     * gc_goods_dosage
     * @return
     *//*
    public GoodsDosage queryGcGoodsDosage(String tradeCode) {
        if (StringUtils.isBlank(tradeCode)) {
            return null;
        }
        String json = null;
        try {
            json = get(RedisConstant.COLLECT_CACHE_GC_GOODS_DOSAGE + "tradeCode" + tradeCode);
            return JSON.parseObject(json, GoodsDosage.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryGcGoodsDosage params:{} json:{}", tradeCode, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }

    *//**
     * 查询 赠品表
     * gc_partner_goods_gift
     *
     * @param dbId
     * @param internalId
     * @return
     *//*
    public PartnerGoodsGift queryGcPartnerGoodsGift(Integer dbId, String internalId) {
        if (Objects.isNull(dbId) || StringUtils.isBlank(internalId)) {
            return null;
        }
        String json = null;
        try {
            json = hget(RedisConstant.COLLECT_CACHE_GC_PARTNER_GOODS_GIFT + "dbId" + dbId, "internalId" + internalId);
            return JSON.parseObject(json, PartnerGoodsGift.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryGcPartnerGoodsGift dbId:{} internalId:{} json:{}", dbId, internalId, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }

    *//**
     * 查询 标准商品表(同步rds)
     * gc_standard_goods_syncrds
     *
     * @param tradeCode
     * @return
     *//*
    public StandardGoodsSyncrds queryGcStandardGoodsSyncrds(String tradeCode) {
        if (StringUtils.isBlank(tradeCode)) {
            return null;
        }
        String json = null;
        try {
            json = get(RedisConstant.COLLECT_CACHE_GC_STANDARD_GOODS_SYNCRDS + "tradeCode" + tradeCode);
            return JSON.parseObject(json, StandardGoodsSyncrds.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryGcStandardGoodsSyncrds params:{} json:{}", tradeCode, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }

    *//**
     * 查询 spu属性关联表(同步rds)
     * @param tradeCode
     * @return
     *//*
    public List<GoodsSpuAttrSyncrds> queryGcGoodsSpuAttrSyncrds(String tradeCode) {
        if (StringUtils.isBlank(tradeCode)) {
            return Collections.emptyList();
        }

        Map<String, String> map = null;
        try {
            map = hgetAll(RedisConstant.COLLECT_CACHE_GC_GOODS_SPU_ATTR_SYNCRDS + "barCode" + tradeCode);
            List<GoodsSpuAttrSyncrds> list = new ArrayList<>();
            for (Map.Entry<String, String> m : map.entrySet()) {
                list.add(JSON.parseObject(m.getValue(), GoodsSpuAttrSyncrds.class));
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryGcGoodsSpuAttrSyncrds params:{} json:{}", tradeCode, map);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }

    *//**
     * 查询 spu属性关联表(同步rds)
     * gc_goods_attr_info_syncrds
     *
     * @param id
     * @return
     *//*
    public GoodsAttrInfoSyncrds queryGcGoodsAttrInfoSyncrds(Long id) {
        if (Objects.isNull(id)) {
            return null;
        }
        String json = null;
        try {
            json = get(RedisConstant.COLLECT_CACHE_GC_GOODS_ATTR_INFO_SYNCRDS + "id" + id);
            return JSON.parseObject(json, GoodsAttrInfoSyncrds.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryGcGoodsAttrInfoSyncrds params:{} json:{}", id, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }

    *//**
     * gc_goods_spu
     * @param id
     * @return
     *//*
    public GoodsSpu queryGoodsSpu(Long id) {
        if (Objects.isNull(id)) {
            return null;
        }
        String json = null;
        try {
            json = get(RedisConstant.COLLECT_CACHE_GC_GOODS_SPU + "id" + id);
            return JSON.parseObject(json, GoodsSpu.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryGoodsSpu params:{} json:{}", id, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }


    *//**
     * 查询
     * gc_base_spu_img
     *
     * @param approvalNumber
     * @return
     *//*
    public BaseSpuImg queryGcBaseSpuImg(String approvalNumber) {
        if (StringUtils.isBlank(approvalNumber)) {
            return null;
        }
        String json = null;
        try {
            json = get(RedisConstant.COLLECT_CACHE_GC_BASE_SPU_IMG + "approvalNumber" + approvalNumber);
            return JSON.parseObject(json, BaseSpuImg.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryGcBaseSpuImg params:{} json:{}", approvalNumber, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }


    *//**
     * 查询 连锁O2O商品图片表
     * partner_goods_img
     *
     * @param dbId
     * @param internalId
     * @return
     *//*
    public PartnerGoodsImg queryPartnerGoodsImg(Integer dbId, String internalId) {
        if (Objects.isNull(dbId) || StringUtils.isBlank(internalId)) {
            return null;
        }

        String json = null;
        try {
            json = hget(RedisConstant.COLLECT_CACHE_PARTNER_GOODS_IMG + "dbId" + dbId,
                    "internalId" + internalId);
            return JSON.parseObject(json, PartnerGoodsImg.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryPartnerGoodsImg params dbId:{} internalId:{} json:{}", dbId, internalId, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }

    *//**
     * 查询 Goods Info 汇总表
     * partner_goods_info
     *
     * @param dbId
     * @param internalId
     * @return
     *//*
    public PartnerGoodsInfo queryPartnerGoodsInfo(Integer dbId, String internalId) {
        if (Objects.isNull(dbId) && StringUtils.isBlank(internalId)) {
            return null;
        }

        String json = null;
        try {
            json = hget(RedisConstant.COLLECT_CACHE_PARTNER_GOODS_INFO + "dbId" + dbId,
                    "internalId" + internalId);
            return JSON.parseObject(json, PartnerGoodsInfo.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryPartnerGoodsInfo params dbId:{} internalId:{} json:{}", dbId, internalId, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }

    *//**
     * 查询 Goods Info 汇总表
     * base_goods
     *
     * @param approvalNumber
     * @return
     *//*
    public BaseGoods queryBaseGoods(String approvalNumber) {
        if (StringUtils.isBlank(approvalNumber)) {
            return null;
        }
        String json = null;
        try {
            json = get(COLLECT_CACHE_BASE_GOODS + "approvalNumber" + approvalNumber);
            return JSON.parseObject(json, BaseGoods.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryBaseGoods params:{} json:{}", approvalNumber, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }

    *//**
     * 商户商品分类关联表
     * @param tradeCode
     * @return
     *//*
    public MerchantGoodsCategoryMapping queryMerchantGoodsCategoryMapping(String tradeCode) {
        if (StringUtils.isBlank(tradeCode)) {
            return null;
        }

        String json = null;
        try {
            json = get(RedisConstant.COLLECT_CACHE_MERCHANT_GOODS_CATEGORY_MAPPING + "tradeCode" + tradeCode);
            return JSON.parseObject(json,
                    MerchantGoodsCategoryMapping.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryMerchantGoodsCategoryMapping params:{} json:{}", tradeCode, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }

    *//**
     * 查询连锁
     * @param dbId
     * @return
     *//*
    public List<PgcStoreInfoShort> queryPgcStoreInfoShort(Integer dbId) {
        String key = String.format(SQOOP_REDIS_KEY, dbId);
        Map<String, String> map = hgetAll(key);
        List<PgcStoreInfoShort> list = new ArrayList<>();
        for (Map.Entry<String, String> e : map.entrySet()) {
            list.add(JSON.parseObject(e.getValue(), PgcStoreInfoShort.class));
        }
        return list;
    }

    *//**
     * 获取所有连锁信息
     * @return
     *//*
    public List<PgcStoreInfoShortInit> queryAllPgcStoreInfoShort() {
        while (true) {
            Jedis jedis = null;
            try {
                String key = String.format(SQOOP_REDIS_KEY, "*");
                jedis = RedisPoolUtil.getInstance(parameterTool);
                ScanParams sp = new ScanParams();
                sp.match(key);
                sp.count(100000);
                String cursor = "0";
                List<PgcStoreInfoShortInit> list = new ArrayList<>();
                while (true) {
                    ScanResult<String> scan = jedis.scan(cursor, sp);
                    List<String> result = scan.getResult();
                    cursor = scan.getStringCursor();
                    if (CollectionUtils.isNotEmpty(result)) {
                        result.forEach(e -> {
                            Map<String, String> map = hgetAll(e);
                            List<PgcStoreInfoShort> collect = map.entrySet().stream().map(m ->
                                    JSON.parseObject(m.getValue(), PgcStoreInfoShort.class)
                            ).collect(Collectors.toList());
                            if (CollectionUtils.isNotEmpty(collect)) {
                                if (null != collect.get(0)) {
                                    list.add(new PgcStoreInfoShortInit(collect.get(0).getDbId(), collect));
                                }
                            }
                        });
                    }
                    if ("0".equals(cursor)) {
                        return list;
                    }
                }

            }
            catch (Exception e) {
                LOGGER.error("RedisService queryAllPgcStoreInfoShort Exception:{}", e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
        }
    }*/

    /**
     * 查询连锁
     * @param merchantId
     * @param storeId
     * @return
     */
 /*   public PgcStoreInfo queryPgcStoreInfo(Integer merchantId, Integer storeId) {
        if (Objects.isNull(merchantId) || Objects.isNull(storeId)) {
            return null;
        }
        String key = COLLECT_CACHE_PGC_STORE_INFO + "organizationId" + merchantId
                + REDIS_TABLE_SEPARATOR + "storeId" + storeId;

        String json = null;
        try {
            json = get(key);
            return JSON.parseObject(json, PgcStoreInfo.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryPgcStoreInfo params:{} json:{}", key, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }*/

    /**
     * 查询 Goods Info 汇总表
     * PgcStoreInfoIncrement
     *
     * @param merchantId
     * @param storeId
     * @return
     */
/*    public PgcStoreInfoIncrement queryPgcStoreInfoIncrement(Integer merchantId, Integer storeId) {
        if (Objects.isNull(merchantId) || Objects.isNull(storeId)) {
            return null;
        }
        String json = null;
        try {
            json = hget(RedisConstant.COLLECT_CACHE_PGC_STORE_INFO_INCREMENT + "organizationId" + merchantId,
                    "storeId" + storeId);
            return JSON.parseObject(json, PgcStoreInfoIncrement.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryPgcStoreInfoIncrement params merchantId:{} storeId:{} json:{}", merchantId, storeId, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }*/

    /**
     * 查询 organize_base
     * @param merchantId
     * @param storeId
     * @return
     */
/*    public OrganizeBase queryOrganizeBase(Integer merchantId, Integer storeId) {
        if (Objects.isNull(merchantId) || Objects.isNull(storeId)) {
            return null;
        }

        String json = null;
        try {
            json = hget(RedisConstant.COLLECT_CACHE_ORGANIZE_BASE + "rootId" + merchantId,
                    "organizationId" + storeId);
            return JSON.parseObject(json, OrganizeBase.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryOrganizeBase params merchantId:{} storeId:{} json:{}", merchantId, storeId, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }*/

    /**
     * 查询 organize_base
     * @param merchantId
     * @return
     */
/*    public List<OrganizeBase> queryOrganizeBase(Integer merchantId) {
        if (Objects.isNull(merchantId)) {
            return null;
        }

        Map<String, String> map = null;
        try {
            map = hgetAll(COLLECT_CACHE_ORGANIZE_BASE + "rootId" + merchantId);
            if (Objects.isNull(map)) {
                return Collections.emptyList();
            }
            return map.entrySet().parallelStream()
                    .map(e ->  JSON.parseObject(e.getValue(), OrganizeBase.class))
                    .collect(Collectors.toList());
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryOrganizeBase params merchantId:{} json:{}", merchantId, map);
        }
        return Collections.emptyList();
    }*/


    /**
     * 查询 organize_base
     * @param storeId
     * @return
     */
/*    public OrganizeBase queryOrganizeBaseByStoreId(Long storeId) {
        if (Objects.isNull(storeId)) {
            return null;
        }
        String json = null;
        try {
            json = get(COLLECT_CACHE_ORGANIZE_BASE_STORE + storeId);
            if (StringUtils.isBlank(json)) {
                return null;
            }
            return JSON.parseObject(json, OrganizeBase.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryOrganizeBase params merchantId:{} json:{}", storeId, json);
        }
        return null;
    }*/


    /**
     * 查询 organize_base
     * @param merchantId
     * @return
     */
  /*  public OrganizeBase queryOrganizeBasePartner(Integer merchantId) {
        if (Objects.isNull(merchantId)) {
            return null;
        }

        Map<String, String> map = null;
        try {
            map = hgetAll(COLLECT_CACHE_ORGANIZE_BASE + "rootId" + merchantId);
            if (Objects.isNull(map)) {
                return null;
            }
            // 1 || 4 一个是云联门店，一个是连锁对接
            OrganizeBase organizeBase = map.entrySet().parallelStream()
                    .map(e -> JSON.parseObject(e.getValue(), OrganizeBase.class))
                    .filter(item -> Objects.nonNull(item) && Objects.nonNull(item.getOrganizationType())
                            && (item.getOrganizationType().equals(1) || item.getOrganizationType().equals(4)))
                    .findFirst()
                    .orElse(null);
            return Objects.nonNull(organizeBase) ? organizeBase : null;
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryOrganizeBaseName params merchantId:{} json:{}", merchantId, map);
        }
        return null;
    }*/


    /**
     * 查询 gc_sku_extend
     * @param merchantId
     * @param goodsInternalId
     * @param title
     * @return
     */
/*    public SkuExtend querySkuExtend(Integer merchantId, String goodsInternalId, String title) {
        if (Objects.isNull(merchantId) || StringUtils.isBlank(goodsInternalId) || StringUtils.isBlank(title)) {
            return null;
        }
        String json = null;
        try {
            json = hget(RedisConstant.COLLECT_CACHE_GC_SKU_EXTEND + "skuNo"
                            + merchantId + SymbolConstants.HOR_LINE + goodsInternalId,
                    "title" + title);
            return JSON.parseObject(json, SkuExtend.class);
        } catch (Exception e) {
            LOGGER.error("RedisService querySkuExtend params merchantId:{} goodsInternalId:{} title:{} json:{}",
                    merchantId, goodsInternalId, title, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }*/

    /**
     * 查询连锁
     * @param dbId
     * @return
     */
/*    public List<PgcMerchantInfo> queryPgcMerchantInfo(Integer dbId) {
        if (Objects.isNull(dbId)) {
            return null;
        }
        String key = COLLECT_CACHE_PGC_MERCHANT_INFO + "dbId" + dbId;
        Map<String, String> map = hgetAll(key);
        List<PgcMerchantInfo> list = new ArrayList<>();
        if (Objects.nonNull(map)) {
            for (Map.Entry<String, String> e : map.entrySet()) {
                list.add(JSON.parseObject(e.getValue(), PgcMerchantInfo.class));
            }
        }
        return list;
    }*/

    /**
     * 查询连锁
     * @param dbId
     * @return
     */
/*    public List<DbMerchant> queryDbMerchant(Integer dbId) {
        if (Objects.isNull(dbId)) {
            return null;
        }
        String key = REDIS_KEY_DB_MERCHANT + dbId;
        Map<String, String> map = hgetAll(key);
        if (Objects.isNull(map)) {
            return Collections.emptyList();
        }
        return map.entrySet().stream()
                .map(m -> new DbMerchant(dbId, Integer.valueOf(m.getKey()), m.getValue()))
                .collect(Collectors.toList());
    }*/

    /**
     * 查询属性商品关联表
     * @param tradeCode
     * @return
     */
/*    public List<GoodsCateSpu> queryGoodsCateSpu(String tradeCode) {
        if (StringUtils.isBlank(tradeCode)) {
            return null;
        }
        String key = COLLECT_CACHE_GC_GOODS_CATE_SPU + "barCode" + tradeCode;
        Map<String, String> map = this.hgetAll(key);
        List<GoodsCateSpu> list = new ArrayList<>();
        if (Objects.nonNull(map)) {
            for (Map.Entry<String, String> e : map.entrySet()) {
                list.add(JSON.parseObject(e.getValue(), GoodsCateSpu.class));
            }
        }
        return list;
    }*/


    /**
     * 查询分类
     * @param id
     * @return
     */
  /*  public CategoryInfo queryCategoryInfo(Long id) {
        if (Objects.isNull(id)) {
            return null;
        }
        String json = null;
        try {
            json = this.get(COLLECT_CACHE_GC_CATEGORY_INFO + "id" + id);

            return JSON.parseObject(json, CategoryInfo.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService queryCategoryInfo params:{} json:{}", id, json);
            LOGGER.error("RedisService Exception e:{}", e);
        }
        return null;
    }*/

    /**
     * 查询商品超重配置
     * @param tradeCode
     * @return
     */
 /*   public GoodsOverweight queryGoodsOverweight(String tradeCode) {
        if (StringUtils.isBlank(tradeCode)) {
            return null;
        }
        String json = null;
        try {
            json = get(RedisConstant.COLLECT_CACHE_GC_GOODS_OVERWEIGHT + "tradeCode" + tradeCode);
            return JSON.parseObject(json, GoodsOverweight.class);
        }
        catch (Exception e) {
            LOGGER.error("RedisService GoodsOverweight params:{} json:{}, Exception:{}", tradeCode, json, e);
        }
        return null;
    }*/

    /**
     * 全连锁redis写入
     * @param key
     * @param val
     * @return
     */
    public Long addSetGoodsAll(String key, String val) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getGoodsAllInstance(parameterTool);
                return jedis.sadd(key, val);
            }
            catch (Exception e) {
                logger.error("RedisService addSetGoodsAll key:{} Exception:{}", key, e);
            }
            finally {
                RedisPoolUtil.closeGoodsAllConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * 全连锁redis写入
     * @param key
     * @param val
     * @return
     */
    public Long deleteSetGoodsAll(String key, String val) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getGoodsAllInstance(parameterTool);
                Long srem = jedis.srem(key, val);
//                LOGGER.info("RedisService deleteSetGoodsAll key:{}, value:{} delRow:{}", key, val, srem);
                return srem;
            }
            catch (Exception e) {
                logger.error("RedisService deleteSetGoodsAll Exception:{}", e);
            }
            finally {
                RedisPoolUtil.closeGoodsAllConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * 全连锁redis根据key删除
     * @param key
     * @return
     */
    public Long deleteKeyGoodsAll(String key) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getGoodsAllInstance(parameterTool);
                Long del = jedis.del(key);
                logger.info("RedisService deleteKeyGoodsAll key:{}, delRow:{}", key, del);
                return del;
            }
            catch (Exception e) {
                logger.error("RedisService deleteKeyGoodsAll Exception:{}", e);
            }
            finally {
                RedisPoolUtil.closeGoodsAllConn(jedis);
            }
            i ++;
        }
        return null;
    }

    /**
     * 校验dbid是否是dtp连锁
     * @return
     */
 /*   public Boolean checkIsDtpStore(Integer dbId) {
        if (Objects.isNull(dbId)) {
            return false;
        }
        Boolean exists = null;
        try {
            exists = exists(REDIS_KEY_DTP_STORE + dbId);
            return exists;
        }
        catch (Exception e) {
            logger.error("RedisService queryGcGoodsDosage params:{} json:{}, e:{}", dbId, exists, e);
        }
        return null;
    }*/

    /**
     * String 如果不存在，则插入
     * @param key
     * @param value
     * @return
     */
    public Long setNX(String key, String value) {
        int i = 0;
        while (i < 100) {
            Jedis jedis = null;
            try {
                jedis = RedisPoolUtil.getInstance(parameterTool);
                return jedis.setnx(key, value);
            }
            catch (Exception e) {
                logger.error("RedisService set key:{} Exception:{}", key, e);
            }
            finally {
                RedisPoolUtil.closeConn(jedis);
            }
            i ++;
        }
        return null;
    }

}
