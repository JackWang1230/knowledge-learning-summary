package cn.wr.collect.sync.constants;

public class RedisConstant {
    /**
     * hbase familyName
     */
    public static final String HBASE_FAMILY_NAME = "base_data_json_family";
    /**
     * hbase qualifier
     */
    public static final String HBASE_QUALIFIER = "base_data_json_value";
    /**
     * redis表存储名前缀
     */
    public static final String REDIS_TABLE_PREFIX = "collect_cache:";
    /**
     * redis表存储名分隔符
     */
    public static final String REDIS_TABLE_SEPARATOR = ":";

    /**
     * redis 刷新字段前缀
     */
    public static final String REDIS_REFRESH_DATA_PREFIX = "refresh_data:";

    /**
     * redis 刷新字段前缀
     */
    public static final String ORGANIZE_BASE_STORE_STATUS = "organize_base_store_status:";

    /**
     * goods_all
     */
    public static final String REDIS_GOODS_ALL = "goods_all:";

    /**
     * gc_goods_manual表记录存储前缀
     */
    public static final String COLLECT_CACHE_GC_GOODS_MANUAL = REDIS_TABLE_PREFIX + "gc_goods_manual" + REDIS_TABLE_SEPARATOR;
    /**
     * gc_goods_sales_statistics_merchant表记录存储前缀
     */
    public static final String COLLECT_CACHE_GC_GOODS_SALES_STATISTICS_MERCHANT = REDIS_TABLE_PREFIX + "gc_goods_sales_statistics_merchant" + REDIS_TABLE_SEPARATOR;
    /**
     * gc_partner_goods_gift表记录存储前缀
     */
    public static final String COLLECT_CACHE_GC_PARTNER_GOODS_GIFT = REDIS_TABLE_PREFIX + "gc_partner_goods_gift" + REDIS_TABLE_SEPARATOR;
    /**
     * gc_base_spu_img表记录存储前缀
     */
    public static final String COLLECT_CACHE_GC_BASE_SPU_IMG = REDIS_TABLE_PREFIX + "gc_base_spu_img" + REDIS_TABLE_SEPARATOR;
    /**
     * partner_goods_img表记录存储前缀
     */
    public static final String COLLECT_CACHE_PARTNER_GOODS_IMG = REDIS_TABLE_PREFIX + "partner_goods_img" + REDIS_TABLE_SEPARATOR;
    /**
     * parnter_goods_search_priority表记录存储前缀
     */
    public static final String COLLECT_CACHE_PARNTER_GOODS_SEARCH_PRIORITY = REDIS_TABLE_PREFIX + "parnter_goods_search_priority" + REDIS_TABLE_SEPARATOR;
    /**
     * gc_standard_goods_syncrds表记录存储前缀
     */
    public static final String COLLECT_CACHE_GC_STANDARD_GOODS_SYNCRDS = REDIS_TABLE_PREFIX + "gc_standard_goods_syncrds" + REDIS_TABLE_SEPARATOR;
    /**
     * gc_goods_spu_attr_syncrds表记录存储前缀
     */
    public static final String COLLECT_CACHE_GC_GOODS_SPU_ATTR_SYNCRDS = REDIS_TABLE_PREFIX + "gc_goods_spu_attr_syncrds" + REDIS_TABLE_SEPARATOR;
    /**
     * gc_goods_attr_info_syncrds表记录存储前缀
     */
    public static final String COLLECT_CACHE_GC_GOODS_ATTR_INFO_SYNCRDS = REDIS_TABLE_PREFIX + "gc_goods_attr_info_syncrds" + REDIS_TABLE_SEPARATOR;
    /**
     * gc_goods_spu表记录存储前缀
     */
    public static final String COLLECT_CACHE_GC_GOODS_SPU = REDIS_TABLE_PREFIX + "gc_goods_spu" + REDIS_TABLE_SEPARATOR;
    /**
     * partner_goods_info表记录存储前缀
     */
    public static final String COLLECT_CACHE_PARTNER_GOODS_INFO = REDIS_TABLE_PREFIX + "partner_goods_info" + REDIS_TABLE_SEPARATOR;
    /**
     * gc_partner_stores_all表记录存储前缀
     */
    public static final String COLLECT_CACHE_GC_PARTNER_STORES_ALL = REDIS_TABLE_PREFIX + "gc_partner_stores_all" + REDIS_TABLE_SEPARATOR;//
    /**
     * partner_stores表记录存储前缀
     */
    public static final String COLLECT_CACHE_PARTNER_STORES = REDIS_TABLE_PREFIX + "partner_stores" + REDIS_TABLE_SEPARATOR;//
    /**
     * gc_base_sku_goods表记录存储前缀
     */
    public static final String COLLECT_CACHE_GC_BASE_SKU_GOODS = REDIS_TABLE_PREFIX + "gc_base_sku_goods_" + REDIS_TABLE_SEPARATOR;//
    /**
     * merchant_goods_category_mapping表记录存储前缀
     */
    public static final String COLLECT_CACHE_MERCHANT_GOODS_CATEGORY_MAPPING = REDIS_TABLE_PREFIX + "merchant_goods_category_mapping" + REDIS_TABLE_SEPARATOR;
    /**
     * base_goods表记录存储前缀
     */
    public static final String COLLECT_CACHE_BASE_GOODS = REDIS_TABLE_PREFIX + "base_goods" + REDIS_TABLE_SEPARATOR;
    /**
     * gc_base_nootc表记录存储前缀
     */
    public static final String COLLECT_CACHE_GC_BASE_NOOTC = REDIS_TABLE_PREFIX + "gc_base_nootc" + REDIS_TABLE_SEPARATOR;

    /**
     * gc_goods_dosage表记录存储前缀
     */
    public static final String COLLECT_CACHE_GC_GOODS_DOSAGE = REDIS_TABLE_PREFIX + "gc_goods_dosage" + REDIS_TABLE_SEPARATOR;

    /**
     * pgc_store_info表记录存储前缀
     */
    public static final String COLLECT_CACHE_PGC_STORE_INFO = REDIS_TABLE_PREFIX + "pgc_store_info" + REDIS_TABLE_SEPARATOR;

    /**
     * pgc_store_info表记录存储前缀
     */
    public static final String COLLECT_CACHE_PGC_MERCHANT_INFO = REDIS_TABLE_PREFIX + "pgc_merchant_info" + REDIS_TABLE_SEPARATOR;

    /**
     * pgc_store_info_increment表记录存储前缀
     */
    public static final String COLLECT_CACHE_PGC_STORE_INFO_INCREMENT = REDIS_TABLE_PREFIX + "pgc_store_info_increment" + REDIS_TABLE_SEPARATOR;

    /**
     * organize_base表记录存储前缀
     */
    public static final String COLLECT_CACHE_ORGANIZE_BASE = REDIS_TABLE_PREFIX + "organize_base" + REDIS_TABLE_SEPARATOR;
    /**
     * organize_base表记录存储前缀
     */
    public static final String COLLECT_CACHE_ORGANIZE_BASE_STORE = REDIS_TABLE_PREFIX + "organize_base_store" + REDIS_TABLE_SEPARATOR;

    /**
     * stock_merchant表记录存储前缀
     */
    public static final String COLLECT_CACHE_STOCK_MERCHANT = REDIS_TABLE_PREFIX + "stock_merchant" + REDIS_TABLE_SEPARATOR;

    /**
     * gc_goods_overweight表记录存储前缀
     */
    public static final String COLLECT_CACHE_GC_GOODS_OVERWEIGHT = REDIS_TABLE_PREFIX + "gc_goods_overweight" + REDIS_TABLE_SEPARATOR;

    /**
     * gc_sku_extend表记录存储前缀
     */
    public static final String COLLECT_CACHE_GC_SKU_EXTEND = REDIS_TABLE_PREFIX + "gc_sku_extend" + REDIS_TABLE_SEPARATOR;

    /**
     * gc_goods_cate_spu表记录存储前缀
     */
    public static final String COLLECT_CACHE_GC_GOODS_CATE_SPU = REDIS_TABLE_PREFIX + "gc_goods_cate_spu" + REDIS_TABLE_SEPARATOR;

    /**
     * gc_goods_cate_spu表记录存储前缀
     */
    public static final String COLLECT_CACHE_GC_CATEGORY_INFO = REDIS_TABLE_PREFIX + "gc_category_info" + REDIS_TABLE_SEPARATOR;

    /**
     * partners 表记录存储前缀
     */
    public static final String COLLECT_CACHE_PARTNERS = REDIS_TABLE_PREFIX + "partners" + REDIS_TABLE_SEPARATOR;

    /**
     * 中间库缓存redis key  sqoop:table:db_id:
     */
    public static final String SQOOP_REDIS_PREFIX = "sqoop";
    public static final String PGC_STORE_INFO_SHORT_KEY = "pgc_store_info_short";
    public static final String PGC_STORE_INFO_SHORT_REFRESH_KEY = "pgc_store_info_short_sync";
    public static final String SQOOP_REDIS_KEY = SQOOP_REDIS_PREFIX + REDIS_TABLE_SEPARATOR + PGC_STORE_INFO_SHORT_KEY + REDIS_TABLE_SEPARATOR + "%s";

    // 全国搜索redis 前缀
    public static final String REDIS_GOODSALL_PREFIX = "goods_all:";

    // 新增门店刷商品
    public static final String REDIS_KEY_SYNC_PROGRESS_PRODUCT = "SyncProgress:product:%d";
    // 门店开通O2O刷状态
    public static final String REDIS_KEY_SYNC_PROGRESS_STATE = "SyncProgress:state:%d";

    // 连锁清库操作，需要更新以下redis数据
    // 商户中心通知商品中心key
    public static final String REDIS_KEY_REFRESH = "NeedRefresh:NoDel";
    // PolarDB同步数据到HBase
    public static final String REDIS_KEY_REFRESH_HBASE = "NeedRefreshHBase:NoDel";
    // 刷新es数据
    public static final String REDIS_KEY_REFRESH_ES = "NeedRefreshES:NoDel";

    // dtp 门店缓存redis
    public static final String REDIS_KEY_DTP_STORE = "DtpStore:";

    // dtp 门店缓存redis
    public static final String REDIS_KEY_DB_MERCHANT = "collect_cache:dbmerchant:";

    // 推送商品中心缓存
    public static final String REDIS_GOODS_CENTER = "collect_cache:goodscenter";
}
