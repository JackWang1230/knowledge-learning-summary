package cn.wr.constants;

/**
 * @author RWang
 * @Date 2022/5/11
 */

public class PropertiesConstants {

    public static final String PROPERTIES_FILE_NAME = "/application.properties";

    /**  kafka 基础配置*/
    public static final String KAFKA_CONFIG_TABLE_SERVERS = "kafka.config.table.servers";
    public static final String KAFKA_CONFIG_TABLE_TOPIC = "kafka.config.table.topic";
    public static final String KAFKA_CONFIG_TABLE_GROUP = "kafka.config.table.group";
    public static final String KAFKA_CONFIG_TABLE_OFFSET = "kafka.config.table.offset";

    public static final String KAFKA_CONFIG_STOCK_SERVERS= "kafka.config.stock.servers";
    public static final String KAFKA_CONFIG_STOCK_TOPICS= "kafka.config.stock.topics";
    public static final String KAFKA_CONFIG_STOCK_GROUP= "kafka.config.stock.group";

    /** 读取监听库表名称*/
    public static final String SCHEMA_CN_UNIONDRUG_MIDDLEED_GOODSCENTER = "cn_uniondrug_middleend_goodscenter";
    public static final String TABLE_GC_CONFIG_SKU = "gc_config_sku";
    public static final String TABLE_GC_SOURCE_SKU = "gc_source_sku";
    public static final String TABLE_GC_DISABLE_STORE = "gc_disable_store";
    public static final String TABLE_GC_GOODS_MANAGEMENT = "gc_goods_management";
    public static final String TABLE_GC_GOODS_MANAGEMENT_STORE = "gc_goods_management_store";

    /** flink checkpoint 配置 */
    public static final String STREAM_CHECKPOINT_ENABLE = "stream.checkpoint.enable";
    public static final String STREAM_CHECKPOINT_INTERVAL = "stream.checkpoint.interval";
    public static final String STREAM_CHECKPOINT_PATH = "stream.checkpoint.path";

    /** flink 窗口及定时器 */
    public static final String STREAM_DELAY_INTERNAL = "stream.delay.internal";
    public static final String STREAM_WINDOW_INTERNAL = "stream.window.internal";

    /**
     * 数据定时任务时间间隔 单位：(s)
     */
    public static final String SCHEDULED_JOB_BASIC = "scheduled.job.internal";


    /** mysql 基础配置*/
    public static final String MYSQL_DATABASE_URL = "mysql.database.url";
    public static final String MYSQL_DATABASE_USER = "mysql.database.user";
    public static final String MYSQL_DATABASE_PASSWORD = "mysql.database.password";

    /** polar 基础配置*/
    public static final String POLAR_DATABASE_URL = "polar.database.url";
    public static final String POLAR_DATABASE_USER = "polar.database.user";
    public static final String POLAR_DATABASE_PASSWORD = "polar.database.password";


    /** datacenter polar 基础配置*/
    public static final String POLAR_DATACENTER_DATABASE_URL = "polar.datacenter.database.url";
    public static final String POLAR_DATACENTER_DATABASE_USER = "polar.datacenter.database.user";
    public static final String POLAR_DATACENTER_DATABASE_PASSWORD = "polar.datacenter.database.password";

    /**
     * mongodb 基础配置
     */
    public static final String MONGO_DATABASE_HOST = "mongo.database.host";
    public static final String MONGO_DATABASE_PORT = "mongo.database.port";
    public static final String MONGO_DATABASE_DBNAME = "mongo.database.dbname";
    public static final String MONGO_DATABASE_USER = "mongo.database.user";
    public static final String MONGO_DATABASE_PASSWORD = "mongo.database.password";


    /** flink基础配置 */
    public static final String STREAM_GLOBAl_PARALLELISM="stream.global.parallelism";
    public static final String STREAM_SOURCE_PARALLELISM="stream.source.parallelism";
    public static final String STREAM_PROCESS_AGG_PARALLELISM="stream.process.agg.parallelism";
    public static final String STREAM_SINK_PARALLELISM="stream.sink.parallelism";
    public static final String FLINK_JOB_NAME = "flink.job.name";


    /** sql 常用配置 */
    public final static String UPDATE ="UPDATE";
    public final static String INSERT ="INSERT";
    public final static String DELETE ="DELETE";

    /** 横线 */
    public final static String HOR_LINE = "-";
    /** 英文逗号*/
    public static final String COMMA_EN = ",";

    // 发送钉钉消息参数
    public static final String STOCK_ABNORMAL_BOT_URL = "stock.abnormal.bot.url";
    public static final String STOCK_ABNORMAL_BOT_TITLE = "stock.abnormal.bot.title";
    public static final String STOCK_ABNORMAL_BOT_SECRET = "stock.abnormal.bot.secret";

    public final static String  GC_CONFIG_SKU="gc_config_sku";
    public final static String  GC_DISABLE_STORE="gc_disable_store";

    /** es 基础配置*/
    public static final String ELASTICSEARCH_BULK_FLUSH_MAX_ACTIONS = "elasticsearch.bulk.flush.max.actions";
    public static final String ELASTICSEARCH_BULK_FLUSH_INTERVAL_MS = "elasticsearch.bulk.flush.interval.ms";
    public static final String ELASTICSEARCH_HOSTS = "elasticsearch.hosts";
    // 用来表示是否开启重试机制
    public static final String ELASTICSEARCH_BULK_FLUSH_BACKOFF_ENABLE = "elasticsearch.bulk.flush.backoff.enable";
    // 重试策略，有两种：EXPONENTIAL 指数型（表示多次重试之间的时间间隔按照指数方式进行增长）、CONSTANT 常数型（表示多次重试之间的时间间隔为固定常数）
    public static final String ELASTICSEARCH_BULK_FLUSH_BACKOFF_TYPE = "elasticsearch.bulk.flush.backoff.type";
    // 进行重试的时间间隔
    public static final String ELASTICSEARCH_BULK_FLUSH_BACKOFF_DELAY = "elasticsearch.bulk.flush.backoff.delay";
    // 失败重试的次数
    public static final String ELASTICSEARCH_BULK_FLUSH_BACKOFF_RETRIES = "elasticsearch.bulk.flush.backoff.retries";

    public static final String ELASTICSEARCH_SECURITY_ENABLE = "elasticsearch.security.enable";
    public static final String ELASTICSEARCH_SECURITY_USERNAME = "elasticsearch.security.username";
    public static final String ELASTICSEARCH_SECURITY_PASSWORD = "elasticsearch.security.password";
    public static final String ELASTICSEARCH_PAGE_SIZE = "elasticsearch.page.size";

    public static final String ELASTICSEARCH_INDEX_WORD = "elasticsearch.index.word";
    public static final String ELASTICSEARCH_DOCUMENT_TYPE_WORD = "elasticsearch.docs.type.word";

    public static final String ELASTICSEARCH_SEARCH_INDEX_WORD = "elasticsearch.search.index.word";
    public static final String ELASTICSEARCH_SEARCH_DOCUMENT_TYPE_WORD = "elasticsearch.search.docs.type.word";

    public static final String DATA_PAGE_SIZE = "data.page.size";


    // hbase
    public static final String ZOOKEEPER_ZNODE_PARENT = "zookeeper.znode.parent";
    public static final String HBASE_ZOOKEEPER_QUORUM = "hbase.zookeeper.quorum";
    public static final String HBASE_CLIENT_RETRIES_NUMBER = "hbase.client.retries.number";
    public static final String HBASE_MASTER_INFO_PORT = "hbase.master.info.port";
    public static final String HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT = "hbase.zookeeper.property.clientPort";
    public static final String HBASE_RPC_TIMEOUT = "hbase.rpc.timeout";
    public static final String HBASE_CLIENT_SCANNER_CACHING = "hbase.client.scanner.caching";
    public static final String HBASE_CLIENT_OPERATION_TIMEOUT = "hbase.client.operation.timeout";
    public static final String HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD = "hbase.client.scanner.timeout.period";
    public static final String HBASE_CLIENT_THREAD_NUM = "hbase.client.thread.num";


    /* HBase 存储表 */
    public static final String HBASE_PREFIX = "hbase_";
    // stock_goods
    public static final String HBASE_STOCK_GOODS = "hbase_stock_goods";


    // redis config
    public static final String REDIS_IP = "redis.ip";
    public static final String REDIS_PORT = "redis.port";
    public static final String REDIS_PASSWORD = "redis.passWord";
    public static final String REDIS_TIMEOUT = "redis.timeout";
    public static final String REDIS_DATABASE = "redis.database";
    public static final String REDIS_GOODS_ALL_DATABASE = "redis.goodsall.database";
    public static final String REDIS_GOODS_CENTER_DATABASE = "redis.goodscenter.database";
    public static final String REDIS_POOL_MAXTOTAL = "redis.pool.maxTotal";
    public static final String REDIS_POOL_MAXIDLE = "redis.pool.maxIdle";
    public static final String REDIS_POOL_MAXWAITMILLIS = "redis.pool.maxWaitMillis";

}
