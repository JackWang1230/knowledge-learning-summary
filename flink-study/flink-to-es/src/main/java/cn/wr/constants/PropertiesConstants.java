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

    /** 读取监听库表名称*/
    public static final String SCHEMA_CN_UNIONDRUG_MIDDLEED_GOODSCENTER = "cn_uniondrug_middleend_goodscenter";
    public static final String TABLE_GC_CONFIG_SKU = "gc_config_sku";

    /** flink checkpoint 配置 */
    public static final String STREAM_CHECKPOINT_ENABLE = "stream.checkpoint.enable";
    public static final String STREAM_CHECKPOINT_INTERVAL = "stream.checkpoint.interval";
    public static final String STREAM_CHECKPOINT_PATH = "stream.checkpoint.path";


    /** mysql 基础配置*/
    public static final String MYSQL_DATABASE_URL = "mysql.database.url";
    public static final String MYSQL_DATABASE_USER = "mysql.database.user";
    public static final String MYSQL_DATABASE_PASSWORD = "mysql.database.password";

    /** polar 基础配置*/
    public static final String POLAR_DATABASE_URL = "polar.database.url";
    public static final String POLAR_DATABASE_USER = "polar.database.user";
    public static final String POLAR_DATABASE_PASSWORD = "polar.database.password";

    /** flink基础配置 */
    public static final String STREAM_SOURCE_PARALLELISM="stream.source.parallelism";
    public static final String STREAM_SINK_PARALLELISM="stream.sink.parallelism";
    public static final String FLINK_JOB_NAME = "flink.job.name";


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

    public static final String ELASTICSEARCH_INDEX_WORD = "elasticsearch.index.word";
    public static final String ELASTICSEARCH_DOCUMENT_TYPE_WORD = "elasticsearch.docs.type.word";

}
