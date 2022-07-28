package cn.wr.collect.sync.constants;


/**
 * 配置项参数
 */
public class PropertiesConstants {
    // 任务版本号
    public static final String FLINK_COLLECT_VERSION = "flink.collect.version";

    // 任务启动入参
    public static final String START_ARGS = "conf";

    // O2O连锁商品 es index/type
    public static final String ES_INDEX_O2O = "goods_index_2.0_real_time_v10";
    public static final String ES_DOCUMENT_TYPE_O2O = "goods";
    /*public static final String ES_INDEX_B2C = "goods_index_2.0_real_time_v5";
    public static final String ES_DOCUMENT_TYPE_B2C = "goods";*/

    // 福利严选 es index/type
    public static final String ES_INDEX_WELFARE = "welfare_goods_v1";
    public static final String ES_DOCUMENT_TYPE_WELFARE = "goods";

    // 标准商品 es index/type
    public static final String ES_INDEX_STANDARD = "standard_goods_v2";
    public static final String ES_DOCUMENT_TYPE_STANDARD = "goods";

    // 搜索热词 es index/type
    public static final String ES_INDEX_WORD = "goods_word_index_v4";
    public static final String ES_DOCUMENT_TYPE_WORD = "word";

    // polardb binlog kafka topic
    public static final String KAFKA_TOPIC = "kafka.topic";
    // polardb binlog kafka brokers
    public static final String KAFKA_BROKERS = "kafka.brokers";

    // 默认配置项
    public static final String DEFAULT_KAFKA_BROKERS = "localhost:9092";

    // 基础商品数据实时同步redis/hbase groupid
    public static final String KAFKA_GROUP_ID_BASIC = "kafka.group.id.basic";

    // 商品数据实时同步es groupid
    public static final String KAFKA_GROUP_ID_GOODS = "kafka.group.id.goods";

    // polardb binlog 发送商品中心 groupid
    public static final String KAFKA_GROUP_ID_GOODS_CENTER = "kafka.group.id.goodscenter";

    // 默认配置项
    public static final String DEFAULT_KAFKA_GROUP_ID = "goods-collect-sync-group";

    // 搜索关键词kafka groupid
    public static final String KAFKA_GROUP_ID_SEARCH_KEYWORDS = "kafka.group.id.searchkeywords";

    // 基础数据实时写es groupid
    public static final String KAFKA_GROUP_ID_BASIC_2_ES = "kafka.group.id.basic2es";

    // 标准商品实时更新es 字段
    public static final String KAFKA_GROUP_ID_STANDARDGOODS = "kafka.group.id.standardgoods";

    // 商品数据实时推送商品中心 kafka 配置
    public static final String KAFKA_GOODSCENTER_BROKERS = "kafka.goodscenter.brokers";
    public static final String KAFKA_GOODSCENTER_TOPIC = "kafka.goodscenter.topic";
    public static final String KAFKA_GOODSCENTERDTP_TOPIC = "kafka.goodscenterdtp.topic";

    //  全国搜索redis group
    public static final String KAFKA_GROUP_ID_BASIC_GOODS_ALL = "kafka.group.id.goodsall";
    // 全国搜索redis topic
    public static final String KAFKA_TOPIC_BASIC_GOODS_ALL = "kafka.topic.goodsall";
    public static final String STREAM_BASIC_GOODS_ALL_INIT_FILTER_PARALLELISM = "stream.basic.goods.all.filter.parallelism";
    public static final String STREAM_BASIC_GOODS_ALL_INIT_FLATMAP_PARALLELISM = "stream.basic.goods.all.flatmap.parallelism";
    public static final String STREAM_BASIC_GOODS_ALL_INIT_SINK_PARALLELISM = "stream.basic.goods.all.sink.parallelism";

    // 福利严选 kafka 配置
    public static final String KAFKA_WELFARE_BROKERS = "kafka.welfare.brokers";
    public static final String KAFKA_WELFARE_TOPIC = "kafka.welfare.topic";
    public static final String KAFKA_GROUP_ID_WELFARE = "kafka.group.id.welfare";

    // 标准商品 kafka 配置
    public static final String KAFKA_STANDARD_BROKERS = "kafka.standard.brokers";
    public static final String KAFKA_STANDARD_TOPIC = "kafka.standard.topic";
    public static final String KAFKA_GROUP_ID_STANDARD = "kafka.group.id.standard";

    // 连锁商品数据发送kafka
    public static final String KAFKA_BROKERS_PARTNER_GOODSALL = "kafka.brokers.partner.goodsall";
    public static final String KAFKA_GROUP_ID_PARTNER_GOODSALL = "kafka.group.id.partner.goodsall";
    public static final String KAFKA_TOPIC_PARTNER_GOODSALL = "kafka.topic.partner.goodsall";
    public static final String KAFKA_TOPIC_PARTNER_GOODS_CHANGE = "kafka.topic.partner.goods.change";

    // 基础商品数据发送kafka
    public static final String KAFKA_BROKERS_GOODSALL = "kafka.brokers.goodsall";
    public static final String KAFKA_GROUP_ID_GOODSALL = "kafka.group.id.goodsall";
    public static final String KAFKA_TOPIC_GOODSALL = "kafka.topic.goodsall";

    // 对码 kafka 配置
    public static final String KAFKA_MATCHCODE_BROKERS = "kafka.matchcode.brokers";
    public static final String KAFKA_MATCHCODE_TOPIC = "kafka.matchcode.topic";
    public static final String KAFKA_GROUP_ID_MATCHCODE = "kafka.group.id.matchcode";

    // 连锁商品新增/变更报警
    public static final String KAFKA_GC_ALARM_BROKERS = "kafka.gc.alarm.brokers";
    public static final String KAFKA_GC_ALARM_TOPIC = "kafka.gc.alarm.topic";
    public static final String KAFKA_GROUP_ID_GC_ALARM = "kafka.group.id.gc.alarm";

    // 库存中心binlog kafka 配置
    public static final String KAFKA_STOCK_BROKERS = "kafka.stock.brokers";
    public static final String KAFKA_STOCK_TOPIC = "kafka.stock.topic";
    public static final String KAFKA_GROUP_ID_STOCK = "kafka.group.id.stock";


    // 价格中心binlog kafka 配置
    public static final String KAFKA_PRICE_BROKERS = "kafka.price.brokers";
    public static final String KAFKA_PRICE_TOPIC = "kafka.price.topic";
    public static final String KAFKA_GROUP_ID_PRICE = "kafka.group.id.price";


    // 连锁商品新增/变更报警
    public static final String KAFKA_GOODS_STATE_CHANGE_BROKERS = "kafka.goods.state.change.brokers";
    public static final String KAFKA_GOODS_STATE_CHANGE_TOPIC = "kafka.goods.state.change.topic";
    public static final String KAFKA_GROUP_ID_GOODS_STATE_CHANGE = "kafka.group.id.goods.state.change";

    // 推送库存中心库存 kafka 配置
    public static final String KAFKA_PUSH_STOCK_STATE_BROKERS = "kafka.push.stock.state.brokers";
    public static final String KAFKA_PUSH_STOCK_STATE_TOPIC = "kafka.push.stock.state.topic";
    public static final String KAFKA_PUSH_STOCK_STATE_DTP_TOPIC = "kafka.push.stock.state.dtp.topic";

    // 重置kafka消费位点-从最新开始消费
    public static final String KAFKA_OFFSET_RESET_LATEST = "kafka.offset.reset.latest";

    // kafka消费时间重置
    public static final String CONSUMER_FROM_TIME = "consumer.from.time";
    // es sink 默认参数
    public static final String STREAM_SINK_PARALLELISM = "stream.sink.parallelism";

    /** ---------------------------------- 任务并行度 start ----------------------------------*/
    // 初始化数据到redis/hbase
    public static final String STREAM_INIT2CACHE_SOURCE_PARALLELISM = "stream.init2cache.source.parallelism";
    public static final String STREAM_INIT2CACHE_SINK_PARALLELISM = "stream.init2cache.sink.parallelism";

    // 自定义同步任务到es
    public static final String STREAM_SELF_SOURCE_PARALLELISM = "stream.self.source.parallelism";
    public static final String STREAM_SELF_FLATMAP01_PARALLELISM = "stream.self.flatmap01.parallelism";
    public static final String STREAM_SELF_FLATMAP02_PARALLELISM = "stream.self.flatmap02.parallelism";
    public static final String STREAM_SELF_SINK01_PARALLELISM = "stream.self.sink01.parallelism";
    public static final String STREAM_SELF_SINK02_PARALLELISM = "stream.self.sink02.parallelism";
    public static final String STREAM_SELF_SINK03_PARALLELISM = "stream.self.sink03.parallelism";

    // 基础商品同步redis/hbase任务
    public static final String STREAM_BASIC2CACHE_SOURCE_PARALLELISM = "stream.basic2cache.source.parallelism";
    public static final String STREAM_BASIC2CACHE_FLATMAP_PARALLELISM = "stream.basic2cache.flatmap.parallelism";
    public static final String STREAM_BASIC2CACHE_SINK01_PARALLELISM = "stream.basic2cache.sink01.parallelism";
    public static final String STREAM_BASIC2CACHE_SINK02_PARALLELISM = "stream.basic2cache.sink02.parallelism";

    // 基础商品数据实时同步es任务
    public static final String STREAM_BASIC2ES_SOURCE_PARALLELISM = "stream.basic2es.source.parallelism";
    public static final String STREAM_BASIC2ES_FLATMAP01_PARALLELISM = "stream.basic2es.flatmap01.parallelism";
    public static final String STREAM_BASIC2ES_FLATMAP02_PARALLELISM = "stream.basic2es.flatmap02.parallelism";
    public static final String STREAM_BASIC2ES_FLATMAP03_PARALLELISM = "stream.basic2es.flatmap03.parallelism";
    public static final String STREAM_BASIC2ES_SINK01_PARALLELISM = "stream.basic2es.sink01.parallelism";
    public static final String STREAM_BASIC2ES_SINK02_PARALLELISM = "stream.basic2es.sink02.parallelism";
    public static final String STREAM_BASIC2ES_SINK03_PARALLELISM = "stream.basic2es.sink03.parallelism";

    // 基础商品数据定时同步es任务
    public static final String STREAM_BASIC2ES_PT_SOURCE_PARALLELISM = "stream.basic2es.pt.source.parallelism";
    public static final String STREAM_BASIC2ES_PT_FLATMAP01_PARALLELISM = "stream.basic2es.pt.flatmap01.parallelism";
    public static final String STREAM_BASIC2ES_PT_FLATMAP02_PARALLELISM = "stream.basic2es.pt.flatmap02.parallelism";
    public static final String STREAM_BASIC2ES_PT_SINK01_PARALLELISM = "stream.basic2es.pt.sink01.parallelism";
    public static final String STREAM_BASIC2ES_PT_SINK02_PARALLELISM = "stream.basic2es.pt.sink02.parallelism";
    public static final String STREAM_BASIC2ES_PT_SINK03_PARALLELISM = "stream.basic2es.pt.sink03.parallelism";

    // 基础商品数据定时同步es任务
    public static final String STREAM_TRUNCATE_PT_SOURCE_PARALLELISM = "stream.truncate.pt.source.parallelism";
    public static final String STREAM_TRUNCATE_PT_FLATMAP01_PARALLELISM = "stream.truncate.pt.flatmap01.parallelism";
    public static final String STREAM_TRUNCATE_PT_FLATMAP02_PARALLELISM = "stream.truncate.pt.flatmap02.parallelism";
    public static final String STREAM_TRUNCATE_PT_SINK01_PARALLELISM = "stream.truncate.pt.sink01.parallelism";
    public static final String STREAM_TRUNCATE_PT_SINK02_PARALLELISM = "stream.truncate.pt.sink02.parallelism";
    public static final String STREAM_TRUNCATE_PT_SINK03_PARALLELISM = "stream.truncate.pt.sink03.parallelism";

    // 门店定时同步es任务
    public static final String STREAM_STORE2ES_PT_SOURCE_PARALLELISM = "stream.store.source.parallelism";
    public static final String STREAM_STORE2ES_PT_FLATMAP01_PARALLELISM = "stream.store.flatmap01.parallelism";
    public static final String STREAM_STORE2ES_PT_FLATMAP02_PARALLELISM = "stream.store.flatmap02.parallelism";
    public static final String STREAM_STORE2ES_PT_SINK01_PARALLELISM = "stream.store.sink01.parallelism";
    public static final String STREAM_STORE2ES_PT_SINK02_PARALLELISM = "stream.store.sink02.parallelism";
    public static final String STREAM_STORE2ES_PT_SINK03_PARALLELISM = "stream.store.sink03.parallelism";

    // 门店初始化es任务
    public static final String STREAM_STORE2ES_INIT_SOURCE_PARALLELISM = "stream.store2es.init.source.parallelism";
    public static final String STREAM_STORE2ES_INIT_FLATMAP01_PARALLELISM = "stream.store2es.init.flatmap01.parallelism";
    public static final String STREAM_STORE2ES_INIT_FLATMAP02_PARALLELISM = "stream.store2es.init.flatmap02.parallelism";
    public static final String STREAM_STORE2ES_INIT_SINK01_PARALLELISM = "stream.store2es.init.sink01.parallelism";
    public static final String STREAM_STORE2ES_INIT_SINK02_PARALLELISM = "stream.store2es.init.sink02.parallelism";

    // 商品数据实时写入es任务
    public static final String STREAM_GOODS_SOURCE_PARALLELISM = "stream.goods.source.parallelism";
    public static final String STREAM_GOODS_FLATMAP01_PARALLELISM = "stream.goods.flatmap01.parallelism";
    public static final String STREAM_GOODS_FLATMAP02_PARALLELISM = "stream.goods.flatmap02.parallelism";
    public static final String STREAM_GOODS_FLATMAP03_PARALLELISM = "stream.goods.flatmap03.parallelism";
    public static final String STREAM_GOODS_SINK01_PARALLELISM = "stream.goods.sink01.parallelism";
    public static final String STREAM_GOODS_SINK02_PARALLELISM = "stream.goods.sink02.parallelism";
    public static final String STREAM_GOODS_SINK03_PARALLELISM = "stream.goods.sink03.parallelism";

    // es根据商品补数任务
    public static final String STREAM_ES_COMP_SOURCE_PARALLELISM = "stream.es.comp.source.parallelism";
    public static final String STREAM_ES_COMP_FLATMAP01_PARALLELISM = "stream.es.comp.flatmap01.parallelism";
    public static final String STREAM_ES_COMP_FLATMAP02_PARALLELISM = "stream.es.comp.flatmap02.parallelism";
    public static final String STREAM_ES_COMP_SINK01_PARALLELISM = "stream.es.comp.sink01.parallelism";
    public static final String STREAM_ES_COMP_SINK02_PARALLELISM = "stream.es.comp.sink02.parallelism";

    // 商品数据推送商品中心初始化任务
    public static final String STREAM_GOODS2KAFKA_INIT_SOURCE_PARALLELISM = "stream.goods2kafka.init.source.parallelism";
    public static final String STREAM_GOODS2KAFKA_INIT_FLATMAP_PARALLELISM = "stream.goods2kafka.init.flatmap.parallelism";
    public static final String STREAM_GOODS2KAFKA_INIT_SINK_PARALLELISM = "stream.goods2kafka.init.sink.parallelism";

    // 商品数据实时推送商品中心任务
    public static final String STREAM_GOODS2KAFKA_SOURCE_PARALLELISM = "stream.goods2kafka.source.parallelism";
    public static final String STREAM_GOODS2KAFKA_FLATMAP_PARALLELISM = "stream.goods2kafka.flatmap.parallelism";
    public static final String STREAM_GOODS2KAFKA_SINK_PARALLELISM = "stream.goods2kafka.sink.parallelism";

    // 商品数据实时报警任务
    public static final String STREAM_GOODS_ALARM_SOURCE_PARALLELISM = "stream.goods.alarm.source.parallelism";
    public static final String STREAM_GOODS_ALARM_FLATMAP_PARALLELISM = "stream.goods.alarm.flatmap.parallelism";
    public static final String STREAM_GOODS_ALARM_SINK_PARALLELISM = "stream.goods.alarm.sink.parallelism";

    // 标准商品实时写es任务
    public static final String STREAM_STANDARD_SOURCE_PARALLELISM = "stream.standard.source.parallelism";
    public static final String STREAM_STANDARD_FLATMAP_PARALLELISM = "stream.standard.flatmap.parallelism";
    public static final String STREAM_STANDARD_SINK_PARALLELISM = "stream.standard.sink.parallelism";

    // 标准商品实时更新es部分字段任务
    public static final String STREAM_STANDARD_BINLOG_SOURCE_PARALLELISM = "stream.standard.binlog.source.parallelism";
    public static final String STREAM_STANDARD_BINLOG_FLATMAP01_PARALLELISM = "stream.standard.binlog.flatmap01.parallelism";
    public static final String STREAM_STANDARD_BINLOG_FLATMAP02_PARALLELISM = "stream.standard.binlog.flatmap02.parallelism";
    public static final String STREAM_STANDARD_BINLOG_SINK_PARALLELISM = "stream.standard.binlog.sink.parallelism";

    // 福利严选商品实时写es任务
    public static final String STREAM_WELFARE_SOURCE_PARALLELISM = "stream.welfare.source.parallelism";
    public static final String STREAM_WELFARE_FLATMAP_PARALLELISM = "stream.welfare.flatmap.parallelism";
    public static final String STREAM_WELFARE_SINK_PARALLELISM = "stream.welfare.sink.parallelism";

    // 连锁商品同步redis
    public static final String STREAM_GOODS2REDIS_INIT_SOURCE_PARALLELISM = "stream.goods2redis.init.source.parallelism";
    public static final String STREAM_GOODS2REDIS_INIT_FLATMAP01_PARALLELISM = "stream.goods2redis.init.flatmap01.parallelism";
    public static final String STREAM_GOODS2REDIS_INIT_FLATMAP02_PARALLELISM = "stream.goods2redis.init.flatmap02.parallelism";
    public static final String STREAM_GOODS2REDIS_INIT_FILTER_PARALLELISM = "stream.goods2redis.init.filter.parallelism";
    public static final String STREAM_GOODS2REDIS_INIT_SINK_PARALLELISM = "stream.goods2redis.init.sink.parallelism";

    // 连锁商品同步redis
    public static final String STREAM_GOODS2REDIS_SOURCE_PARALLELISM = "stream.goods2redis.source.parallelism";
    public static final String STREAM_GOODS2REDIS_FILTER_PARALLELISM = "stream.goods2redis.filter.parallelism";
    public static final String STREAM_GOODS2REDIS_SINK_PARALLELISM = "stream.goods2redis.sink.parallelism";

    // 对码同步连锁库
    public static final String STREAM_MATCHCODE_SOURCE_PARALLELISM = "stream.matchcode.source.parallelism";
    public static final String STREAM_MATCHCODE_SINK_PARALLELISM = "stream.matchcode.sink.parallelism";

    // 库存实时同步es
    public static final String STREAM_STOCK_SOURCE_PARALLELISM = "stream.stock.source.parallelism";
    public static final String STREAM_STOCK_FLATMAP01_PARALLELISM = "stream.stock.flatmap01.parallelism";
    public static final String STREAM_STOCK_SINK01_PARALLELISM = "stream.stock.sink01.parallelism";

    // 库存实时同步es
    public static final String STREAM_PUSH_GOODS_STATE_SOURCE_PARALLELISM = "stream.push.goods.state.source.parallelism";
    public static final String STREAM_PUSH_GOODS_STATE_FLATMAP01_PARALLELISM = "stream.push.goods.state.flatmap01.parallelism";
    public static final String STREAM_PUSH_GOODS_STATE_FLATMAP02_PARALLELISM = "stream.push.goods.state.flatmap02.parallelism";
    public static final String STREAM_PUSH_GOODS_STATE_SINK01_PARALLELISM = "stream.push.goods.state.sink01.parallelism";


    // 价格实时同步es
    public static final String STREAM_PRICE_SOURCE_PARALLELISM = "stream.price.source.parallelism";
    public static final String STREAM_PRICE_FLATMAP01_PARALLELISM = "stream.price.flatmap01.parallelism";
    public static final String STREAM_PRICE_SINK01_PARALLELISM = "stream.price.sink01.parallelism";


    /** ---------------------------------- 任务并行度 end ----------------------------------*/



    // checkpoint
    public static final String STREAM_CHECKPOINT_ENABLE = "stream.checkpoint.enable";
    public static final String STREAM_CHECKPOINT_INTERVAL = "stream.checkpoint.interval";
    // TODO: 2020/4/16  区分测试环境 开发环境
    public static final String PROPERTIES_FILE_NAME = "/application.properties";

    // polardb 同步 hbase
    public static final String COMPLEMENT_POLAR2CACHE_TABLE = "complement.polar2cache.table";
    // 清空redis数据
    public static final String COMPLEMENT_CLEAR_REDIS = "complement.clear.redis";
    // 清空hbase数据
    public static final String COMPLEMENT_CLEAR_HBASE = "complement.clear.hbase";

    //es config
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

    // mysql: partner
    public static final String MYSQL_DATABASE_PARTNER_URL = "mysql.database.partner.url";
    public static final String MYSQL_DATABASE_PARTNER_USERNAME = "mysql.database.partner.username";
    public static final String MYSQL_DATABASE_PARTNER_PASSWORD = "mysql.database.partner.password";
    // mysql: goodscenter
    public static final String MYSQL_DATABASE_GOODSCENTER_URL = "mysql.database.goodscenter.url";
    public static final String MYSQL_DATABASE_GOODSCENTER_USERNAME = "mysql.database.goodscenter.username";
    public static final String MYSQL_DATABASE_GOODSCENTER_PASSWORD = "mysql.database.goodscenter.password";
    // 中间库：sqoop
    public static final String MYSQL_DATABASE_SQOOP_URL = "mysql.database.sqoop.url";
    public static final String MYSQL_DATABASE_SQOOP_USERNAME = "mysql.database.sqoop.username";
    public static final String MYSQL_DATABASE_SQOOP_PASSWORD = "mysql.database.sqoop.password";
    // 数据中心
    public static final String MYSQL_DATABASE_DATACENTER_URL = "mysql.database.datacenter.url";
    public static final String MYSQL_DATABASE_DATACENTER_USERNAME = "mysql.database.datacenter.username";
    public static final String MYSQL_DATABASE_DATACENTER_PASSWORD = "mysql.database.datacenter.password";

    // 连锁库
    public static final String MYSQL_DATABASE_CHAIN_URL = "mysql.database.chain.url";
    public static final String MYSQL_DATABASE_CHAIN_USERNAME = "mysql.database.chain.username";
    public static final String MYSQL_DATABASE_CHAIN_PASSWORD = "mysql.database.chain.password";

    // 连锁库-单体库
    public static final String MYSQL_DATABASE_CHAIN_SINGLE_URL = "mysql.database.chain.single.url";
    public static final String MYSQL_DATABASE_CHAIN_SINGLE_USERNAME = "mysql.database.chain.single.username";
    public static final String MYSQL_DATABASE_CHAIN_SINGLE_PASSWORD = "mysql.database.chain.single.password";

    // 库存中心
    public static final String MYSQL_DATABASE_STOCK_URL = "mysql.database.stock.url";
    public static final String MYSQL_DATABASE_STOCK_USERNAME = "mysql.database.stock.username";
    public static final String MYSQL_DATABASE_STOCK_PASSWORD = "mysql.database.stock.password";

    // 价格中心
    public static final String MYSQL_DATABASE_PRICE_URL = "mysql.database.price.url";
    public static final String MYSQL_DATABASE_PRICE_USERNAME = "mysql.database.price.username";
    public static final String MYSQL_DATABASE_PRICE_PASSWORD = "mysql.database.price.password";


    // 定时任务开始时间 flink 任务重启时需要重新配置，为空则默认当前时间 格式:{yyyy-MM-dd hh:mm:ss}
    public static final String SCHEDULED_JOB_START_TIME = "scheduled.job.start.time";
    // 基础数据定时任务时间间隔 单位：(s)
    public static final String SCHEDULED_JOB_BASIC = "scheduled.job.basic";
    // 门店定时任务时间间隔 单位：(s)
    public static final String SCHEDULED_JOB_STORES = "scheduled.job.stores";
    // 连锁上下架定时任务时间间隔 单位：(s)
    public static final String SCHEDULED_JOB_PLATFORM = "scheduled.job.platform";

    public static final String INIT_ES_START_ID = "init.es.start.id";
    public static final String INIT_ES_END_ID = "init.es.end.id";
    public static final String INIT_ES_DB_ID = "init.es.db.id";

    //商品中心推送kafka消息任务补数据
    public static final String INIT_GOODS_CENTER_DB_ID = "init.goodscenter.db.id";
    public static final String INIT_GOODS_CENTER_MERCHANT_ID = "init.goodscenter.merchant.id";
    public static final String INIT_GOODS_CENTER_START_TIME = "init.goodscenter.start.time";
    public static final String INIT_GOODS_CENTER_END_TIME = "init.goodscenter.end.time";


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

    // 是否开启写入
    public static final String SINK_ELASTIC = "sink.elastic";
    public static final String SINK_TIDB = "sink.tidb";
    public static final String SINK_MBS = "sink.mbs";
    public static final String SINK_CACHECHANGE = "sink.cachechange";
    public static final String SINK_KAFKA = "sink.kafka";
    public static final String SINK_HBASE = "sink.hbase";
    public static final String SINK_CACHE_PRODUCT_PROGRESS = "sink.cache.product.progress";
    public static final String SINK_CACHE_STATE_PROGRESS = "sink.cache.state.progress";


    // 商品中心首次初始化
    public static final String GOODSCENTER_FIRST_INIT = "goodscenter.first.init";
    public static final String SCHEDULED_GOODSCENTER_TIME = "scheduled.goodscenter.time";

    // 补数逻辑  开始时间/结束时间
    public static final String COMPLEMENT_START_TIME = "complement.start.time";
    public static final String COMPLEMENT_END_TIME = "complement.end.time";
    public static final String COMPLEMENT_DB_ID = "complement.db.id";
    public static final String COMPLEMENT_ID = "complement.id";

    // 比数逻辑
    public static final String COMPARE_DB_ID = "compare.db.id";
    public static final String COMPARE_MERCHANT_ID = "compare.merchant.id";
    public static final String COMPARE_STORE_ID = "compare.store.id";

    // mbs url 地址
    public static final String MBS2_SERVICE_ADDR = "mbs2.service-addr";
    public static final String MBS_GOODS_TOPIC = "mbs.goods.topic";
    public static final String MBS_GOODS_MGC_TAG = "mbs.goods.mgc.tag";
    public static final String MBS_FIRST_PUSH_GOODS = "mbs.first.push.goods";
    public static final String MBS_FIRST_PUSH_GOODS_TOPIC = "mbs.first.push.goods.topic";
    public static final String MBS_FIRST_PUSH_GOODS_TAG = "mbs.first.push.goods.tag";

    // 发送钉钉消息参数
    public static final String WELFARE_GOODS_BOT_URL = "welfare.goods.bot.url";
    public static final String WELFARE_GOODS_BOT_TITLE = "welfare.goods.bot.title";

    // 发送钉钉消息参数
    public static final String GOODS_CENTER_BOT_URL = "goods.center.bot.url";
    public static final String GOODS_CENTER_BOT_TITLE = "goods.center.bot.title";
    public static final String GOODS_CENTER_BOT_SECRET = "goods.center.bot.secret";

    public static final String FILTER_DBID = "filter.dbid";
    public static final String DTP_STORE_ID = "dtp.store.id";

    // 价格中心补数逻辑
    public static final String INIT_PRICE_MERCHANT_ID = "init.price.merchant.id";
    public static final String INIT_PRICE_STORE_ID = "init.price.store.id";

    // 对比连锁库id
    public static final String COMPARE_PRICE_MERCHANT_ID = "compare.price.merchant.id";


    // 对比价格数据库连接信息
    public static final String MYSQL_DATABASE_PRICE_COMPARE_URL = "mysql.database.price.compare.url";
    public static final String MYSQL_DATABASE_PRICE_COMPARE_USERNAME = "mysql.database.price.compare.username";
    public static final String MYSQL_DATABASE_PRICE_COMPARE_PASSWORD = "mysql.database.price.compare.password";
}
