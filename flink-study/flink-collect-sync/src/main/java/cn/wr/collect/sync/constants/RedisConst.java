package cn.wr.collect.sync.constants;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Objects;


public class RedisConst {
    // redis 前缀
    public static final String REDIS_TABLE_PREFIX = "collect:";

    // 定时刷新任务key
    public static final String REFRESH_TIME_SYNC = "collect:timesync";

    // 缓存sku_code，去重
    public static final String REDIS_SKU_CODE = "collect:skucode:%s";

    // 缓存门店商品
    public static final String REDIS_GOODS_ALL = "goods_all:";

    public static final int REDIS_SKU_CODE_TIME_OUT = 5 * 60;

    /**
     * redis 数据类型
     */
    public enum RedisType {
        STRING,
        HASH
    }

    public static RedisType getRedisType(String table) {
        if (StringUtils.isBlank(table)) {
            return null;
        }
        if (Arrays.stream(StringKey.values()).anyMatch(name -> StringUtils.equals(name.name(), table))) {
            return RedisType.STRING;
        }
        if (Arrays.stream(HashKey.values()).anyMatch(name -> StringUtils.equals(name.name(), table))) {
            return RedisType.HASH;
        }
        return null;
    }

    public interface RedisKey {
        String getKey();
        String getField();
    }

    public static RedisKey getRedis(String table) {
        if (StringUtils.isBlank(table)) {
            return null;
        }
        StringKey set = Arrays.stream(StringKey.values()).filter(s -> StringUtils.equals(s.name(), table))
                .findFirst().orElse(null);
        if (Objects.nonNull(set)) {
            return set;
        }
        HashKey hash = Arrays.stream(HashKey.values()).filter(h -> StringUtils.equals(h.name(), table))
                .findFirst().orElse(null);
        if (Objects.nonNull(hash)) {
            return hash;
        }
        return null;
    }

    // string
    public enum StringKey implements RedisKey {
        base_goods(REDIS_TABLE_PREFIX + "base_goods:%s"),
        gc_base_spu_img(REDIS_TABLE_PREFIX + "gc_base_spu_img:%s"),
        gc_goods_manual(REDIS_TABLE_PREFIX + "gc_goods_manual:%s"),
        gc_standard_goods_syncrds(REDIS_TABLE_PREFIX + "gc_standard_goods_syncrds:%s"),
        gc_goods_attr_info_syncrds(REDIS_TABLE_PREFIX + "gc_goods_attr_info_syncrds:%s"),
        gc_goods_dosage(REDIS_TABLE_PREFIX + "gc_goods_dosage:%s"),
        merchant_goods_category_mapping(REDIS_TABLE_PREFIX + "merchant_goods_category_mapping:%s"),
        gc_base_nootc(REDIS_TABLE_PREFIX + "gc_base_nootc:%s"),
        pgc_store_info(REDIS_TABLE_PREFIX + "pgc_store_info:%s:%s"),
        gc_goods_overweight(REDIS_TABLE_PREFIX + "gc_goods_overweight:%s"),
        gc_goods_spu(REDIS_TABLE_PREFIX + "gc_goods_spu:%s"),
        gc_category_info(REDIS_TABLE_PREFIX + "gc_category_info:%s"),
        ;
        private String key;

        StringKey(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getField() {
            return null;
        }

        public static boolean isExist(String table) {
            return Arrays.stream(StringKey.values()).anyMatch(s -> StringUtils.equals(s.name(), table));
        }
    }

    // hash
    public enum HashKey implements RedisKey {
        gc_partner_goods_gift(REDIS_TABLE_PREFIX + "gc_partner_goods_gift:%s", "%s"),
        gc_partner_stores_all(REDIS_TABLE_PREFIX + "gc_partner_stores_all:%s", "%s"),
        partner_goods_img(REDIS_TABLE_PREFIX + "partner_goods_img:%s", "%s"),
        gc_goods_spu_attr_syncrds(REDIS_TABLE_PREFIX + "gc_goods_spu_attr_syncrds:%s", "%s"),
        organize_base(REDIS_TABLE_PREFIX + "organize_base:%s", "%s"),
        pgc_store_info_increment(REDIS_TABLE_PREFIX + "pgc_store_info_increment:%s", "%s"),
        pgc_merchant_info(REDIS_TABLE_PREFIX + "pgc_merchant_info:%s", "%s"),
        gc_sku_extend(REDIS_TABLE_PREFIX + "gc_sku_extend:%s", "%s"),
        partner_goods_info(REDIS_TABLE_PREFIX + "partner_goods_info:%s", "%s"),
        gc_goods_cate_spu(REDIS_TABLE_PREFIX + "gc_goods_cate_spu:%s", "%s"),
        partners(REDIS_TABLE_PREFIX + "partners:%s", "%s"),
        gc_config_sku(REDIS_TABLE_PREFIX + "gc_config_sku:%s", "%s"),
        price_list(REDIS_TABLE_PREFIX + "price_list:%s", "%s"),
        price_store(REDIS_TABLE_PREFIX + "price_store:%s", "%s"),
        dbmerchant(REDIS_TABLE_PREFIX + "dbmerchant:%s", "%s"),
        ;

        /**
         * key
         */
        private String key;
        /**
         * field
         */
        private String field;

        HashKey(String key, String field) {
            this.key = key;
            this.field = field;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getField() {
            return field;
        }

        public static boolean isExist(String table) {
            return Arrays.stream(HashKey.values()).anyMatch(s -> StringUtils.equals(s.name(), table));
        }
    }

    public static String buildKey(String table, Object...args) {
        boolean isNull = Arrays.stream(args).anyMatch(arg -> {
            if (arg instanceof String) {
                return StringUtils.isBlank((String) arg);
            } else {
                return Objects.isNull(arg);
            }
        });
        if (isNull) {
            return null;
        }

        RedisKey redis = RedisConst.getRedis(table);
        if (Objects.isNull(redis)) return null;
        String key = redis.getKey();
        if (StringUtils.isBlank(key)) return null;
        return String.format(key, args);
    }

    public static String buildFiled(String table, Object...args) {
        boolean isNull = Arrays.stream(args).anyMatch(arg -> {
            if (arg instanceof String) {
                return StringUtils.isBlank((String) arg);
            } else {
                return Objects.isNull(arg);
            }
        });
        if (isNull) {
            return null;
        }

        RedisKey redis = RedisConst.getRedis(table);
        if (Objects.isNull(redis)) return null;
        String field = redis.getField();
        if (StringUtils.isBlank(field)) return null;
        return String.format(field, args);
    }


    public static void main(String[] args) {
        /*Pattern p = Pattern.compile("\\{(.*?)}");
        Matcher matcher1 = p.matcher(Set.pgc_store_info.key);
        while (matcher1.find()) {
            System.out.println(matcher1.group());
        }*/

        /*String s = RedisConst.buildKey("pgc_store_info", "sdf", "ghj");
        System.out.println(s);*/

        String str = "test:%s%s";
        String[] arr = new String[]{"1", "2", "3", "4"};
        String format = String.format(str, arr);
        System.out.println(format);

    }
}
