package cn.wr.collect.sync.utils;


import cn.wr.collect.sync.constants.PropertiesConstants;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.MetricEvent;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.chain.MatchCodeDTO;
import cn.wr.collect.sync.model.goodscenter.GoodsCenterDTO;
import cn.wr.collect.sync.model.kafka.ElasticGoodsDTO;
import cn.wr.collect.sync.model.kafka.TestPartitions;
import cn.wr.collect.sync.model.stock.StockGoods;
import cn.wr.collect.sync.scheme.*;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;

public class KafkaConfigUtil {

    /**
     * 设置基础的 Kafka 配置
     *
     * @return
     */
    /*public static Properties buildKafkaProps() {
        return buildKafkaProps(ParameterTool.fromSystemProperties());
    }*/
    private static Properties buildKafkaProps(ParameterTool parameterTool, String brokersProp, String groupIdProp) {
        Properties props = parameterTool.getProperties();
        props.put("bootstrap.servers", parameterTool.get(brokersProp));
        props.put("group.id", parameterTool.get(groupIdProp));
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        return props;
    }

    /**
     * 重置kafka消费时间点
     * @param props
     * @param parameterTool
     * @param time
     * @return
     */
    private static Map<KafkaTopicPartition, Long> buildOffsetByTime(Properties props, ParameterTool parameterTool, Long time) {
        props.setProperty("group.id", "query_time_" + time);
        KafkaConsumer consumer = new KafkaConsumer(props);
        List<PartitionInfo> partitionsFor = consumer.partitionsFor(parameterTool.getRequired(KAFKA_TOPIC));
        Map<TopicPartition, Long> partitionInfoLongMap = new HashMap<>();
        for (PartitionInfo partitionInfo : partitionsFor) {
            partitionInfoLongMap.put(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()), time);
        }
        Map<TopicPartition, OffsetAndTimestamp> offsetResult = consumer.offsetsForTimes(partitionInfoLongMap);
        Map<KafkaTopicPartition, Long> partitionOffset = new HashMap<>();
        offsetResult.forEach((key, value) -> partitionOffset.put(new KafkaTopicPartition(key.topic(), key.partition()), value.offset()));

        consumer.close();
        return partitionOffset;
    }


    /**--------------------------------------- 基础数据 kafka 配置 Start ---------------------------------------------*/
    private static Properties buildKafkaPropsBasic(ParameterTool parameterTool) {
        Properties props = parameterTool.getProperties();
        props.put("bootstrap.servers", parameterTool.get(PropertiesConstants.KAFKA_BROKERS, DEFAULT_KAFKA_BROKERS));
        props.put("group.id", parameterTool.get(KAFKA_GROUP_ID_BASIC, DEFAULT_KAFKA_GROUP_ID));
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        return props;
    }

    public static DataStreamSource<PolarDbBinlogBatch> buildBasicSource(StreamExecutionEnvironment env) {
        ParameterTool parameter = (ParameterTool) env.getConfig().getGlobalJobParameters();
        String topic = parameter.getRequired(PropertiesConstants.KAFKA_TOPIC);
        Long time = parameter.getLong(PropertiesConstants.CONSUMER_FROM_TIME, 0L);
        return buildBasicSource(env, topic, time);
    }

    private static DataStreamSource<PolarDbBinlogBatch> buildBasicSource(StreamExecutionEnvironment env, String topic, Long time) {
        ParameterTool parameterTool = (ParameterTool) env.getConfig().getGlobalJobParameters();
        Properties props = buildKafkaPropsBasic(parameterTool);
        FlinkKafkaConsumer<PolarDbBinlogBatch> consumer = new FlinkKafkaConsumer<>(
                topic,
                new BasicBinlogDeserializationSchemaV2(),
                props);
        //重置offset到time时刻
        if (time != 0L) {
            Map<KafkaTopicPartition, Long> partitionOffset = buildOffsetByTime(props, parameterTool, time);
            consumer.setStartFromSpecificOffsets(partitionOffset);
        }
        // 重置消费位点
        if (parameterTool.getBoolean(KAFKA_OFFSET_RESET_LATEST)) {
            consumer.setStartFromLatest();
        }
        return env.addSource(consumer);
    }
    /**--------------------------------------- 基础数据 kafka 配置 End ---------------------------------------------*/


    /**--------------------------------------- 源数据 kafka 配置 Start ---------------------------------------------*/
    private static Properties buildKafkaPropsGoods(ParameterTool parameterTool) {
        Properties props = parameterTool.getProperties();
        props.put("bootstrap.servers", parameterTool.get(PropertiesConstants.KAFKA_BROKERS, DEFAULT_KAFKA_BROKERS));
        props.put("group.id", parameterTool.get(KAFKA_GROUP_ID_GOODS, DEFAULT_KAFKA_GROUP_ID));
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        return props;
    }

    public static DataStreamSource<MetricEvent> buildGoodsSource(StreamExecutionEnvironment env) {
        ParameterTool parameter = (ParameterTool) env.getConfig().getGlobalJobParameters();
        String topic = parameter.getRequired(PropertiesConstants.KAFKA_TOPIC);
        Long time = parameter.getLong(PropertiesConstants.CONSUMER_FROM_TIME, 0L);
        return buildGoodsSource(env, topic, time);
    }

    private static DataStreamSource<MetricEvent> buildGoodsSource(StreamExecutionEnvironment env, String topic, Long time) {
        ParameterTool parameterTool = (ParameterTool) env.getConfig().getGlobalJobParameters();
        Properties props = buildKafkaPropsGoods(parameterTool);
        FlinkKafkaConsumer consumer = new FlinkKafkaConsumer<>(
                topic,
                new GoodsBinlogDeserializationSchemaV2(),
                props);
        //重置offset到time时刻
        if (time != 0L) {
            Map<KafkaTopicPartition, Long> partitionOffset = buildOffsetByTime(props, parameterTool, time);
            consumer.setStartFromSpecificOffsets(partitionOffset);
        }
        // 重置消费位点
        if (parameterTool.getBoolean(KAFKA_OFFSET_RESET_LATEST)) {
            consumer.setStartFromLatest();
        }

        return env.addSource(consumer);
    }

    /**--------------------------------------- 源数据 kafka 配置 End ---------------------------------------------*/


    /**--------------------------------------- 源数据 V2 kafka 配置 Start ---------------------------------------------*/
    private static Properties buildKafkaPropsGoodsV2(ParameterTool parameterTool) {
        Properties props = parameterTool.getProperties();
        props.put("bootstrap.servers", parameterTool.get(PropertiesConstants.KAFKA_BROKERS, DEFAULT_KAFKA_BROKERS));
        props.put("group.id", parameterTool.get(KAFKA_GROUP_ID_GOODS, DEFAULT_KAFKA_GROUP_ID));
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        return props;
    }

    public static DataStreamSource<PolarDbBinlogBatch> buildGoodsSourceV2(StreamExecutionEnvironment env) {
        ParameterTool parameter = (ParameterTool) env.getConfig().getGlobalJobParameters();
        String topic = parameter.getRequired(PropertiesConstants.KAFKA_TOPIC);
        Long time = parameter.getLong(PropertiesConstants.CONSUMER_FROM_TIME, 0L);
        return buildGoodsSourceV2(env, topic, time);
    }

    private static DataStreamSource<PolarDbBinlogBatch> buildGoodsSourceV2(StreamExecutionEnvironment env, String topic, Long time) {
        ParameterTool parameterTool = (ParameterTool) env.getConfig().getGlobalJobParameters();
        Properties props = buildKafkaPropsGoodsV2(parameterTool);
        FlinkKafkaConsumer<PolarDbBinlogBatch> consumer = new FlinkKafkaConsumer<>(
                topic,
                new GoodsBinlogDeserializationSchemaV2(),
                props);
        //重置offset到time时刻
        if (time != 0L) {
            Map<KafkaTopicPartition, Long> partitionOffset = buildOffsetByTime(props, parameterTool, time);
            consumer.setStartFromSpecificOffsets(partitionOffset);
        }
        // 重置消费位点
        if (parameterTool.getBoolean(KAFKA_OFFSET_RESET_LATEST)) {
            consumer.setStartFromLatest();
        }

        return env.addSource(consumer);
    }

    /**--------------------------------------- 源数据 V2 kafka 配置 End ---------------------------------------------*/


    /**--------------------------------------- 商品中心 kafka 配置 Start ---------------------------------------------*/
    private static Properties buildKafkaPropsGoodsCenterConsumer(ParameterTool parameterTool) {
        Properties props = parameterTool.getProperties();
        props.put("bootstrap.servers", parameterTool.get(PropertiesConstants.KAFKA_BROKERS, DEFAULT_KAFKA_BROKERS));
        props.put("group.id", parameterTool.get(KAFKA_GROUP_ID_GOODS_CENTER, DEFAULT_KAFKA_GROUP_ID));
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        return props;
    }

    public static DataStreamSource<PolarDbBinlogBatch> buildGoodsCenterSourceConsumer(StreamExecutionEnvironment env) {
        ParameterTool parameter = (ParameterTool) env.getConfig().getGlobalJobParameters();
        String topic = parameter.getRequired(PropertiesConstants.KAFKA_TOPIC);
        Long time = parameter.getLong(PropertiesConstants.CONSUMER_FROM_TIME, 0L);
        return buildGoodsCenterSourceConsumer(env, topic, time);
    }

    private static DataStreamSource<PolarDbBinlogBatch> buildGoodsCenterSourceConsumer(StreamExecutionEnvironment env, String topic, Long time) {
        ParameterTool parameterTool = (ParameterTool) env.getConfig().getGlobalJobParameters();
        Properties props = buildKafkaPropsGoodsCenterConsumer(parameterTool);
        FlinkKafkaConsumer<PolarDbBinlogBatch> consumer = new FlinkKafkaConsumer<>(
                topic,
                new PartnerGoodsBinlogSchema(),
                props);
        //重置offset到time时刻
        if (time != 0L) {
            Map<KafkaTopicPartition, Long> partitionOffset = buildOffsetByTime(props, parameterTool, time);
            consumer.setStartFromSpecificOffsets(partitionOffset);
        }
        // 重置消费位点
        if (parameterTool.getBoolean(KAFKA_OFFSET_RESET_LATEST)) {
            consumer.setStartFromLatest();
        }
        return env.addSource(consumer);
    }

    public static FlinkKafkaProducer<GoodsCenterDTO> buildKafkaPropsGoodsCenterProducer(ParameterTool parameterTool) {
        Properties props = new Properties();
        props.put("bootstrap.servers", parameterTool.get(PropertiesConstants.KAFKA_GOODSCENTER_BROKERS, DEFAULT_KAFKA_BROKERS));
        return new FlinkKafkaProducer<>(parameterTool.get(PropertiesConstants.KAFKA_GOODSCENTER_TOPIC),
                new GoodsCenterSchema(), props, FlinkKafkaProducer.Semantic.AT_LEAST_ONCE);
    }

    public static FlinkKafkaProducer<GoodsCenterDTO> buildKafkaPropsGoodsCenterDtpProducer(ParameterTool parameterTool) {
        Properties props = new Properties();
        props.put("bootstrap.servers", parameterTool.get(PropertiesConstants.KAFKA_GOODSCENTER_BROKERS, DEFAULT_KAFKA_BROKERS));
        return new FlinkKafkaProducer<>(parameterTool.get(PropertiesConstants.KAFKA_GOODSCENTERDTP_TOPIC),
                new GoodsCenterDtpSchema(), props, FlinkKafkaProducer.Semantic.AT_LEAST_ONCE);
    }


    public static FlinkKafkaProducer<TestPartitions> buildKafkaPropsProducerTest() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.3.212:9092");
        return new FlinkKafkaProducer<>("topic-partitions",
                new TestPartitionsSchema(), props, FlinkKafkaProducer.Semantic.NONE);
    }

    /**--------------------------------------- 商品中心 kafka 配置 End ---------------------------------------------*/


    /**--------------------------------------- 搜索关键词 kafka 配置 Start ---------------------------------------------*/
    @Deprecated
    private static Properties buildKafkaPropsSearchConsumer(ParameterTool parameterTool) {
        Properties props = parameterTool.getProperties();
        props.put("bootstrap.servers", parameterTool.get(PropertiesConstants.KAFKA_BROKERS, DEFAULT_KAFKA_BROKERS));
//        props.put("zookeeper.connect", parameterTool.get(PropertiesConstants.KAFKA_ZOOKEEPER_CONNECT, DEFAULT_KAFKA_ZOOKEEPER_CONNECT));
        props.put("group.id", parameterTool.get(KAFKA_GROUP_ID_SEARCH_KEYWORDS, DEFAULT_KAFKA_GROUP_ID));
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        return props;
    }

    @Deprecated
    public static DataStreamSource<MetricEvent> buildSearchSourceConsumer(StreamExecutionEnvironment env) {
        ParameterTool parameter = (ParameterTool) env.getConfig().getGlobalJobParameters();
        String topic = parameter.getRequired(PropertiesConstants.KAFKA_TOPIC);
        Long time = parameter.getLong(PropertiesConstants.CONSUMER_FROM_TIME, 0L);
        return buildSearchSourceConsumer(env, topic, time);
    }

    @Deprecated
    private static DataStreamSource<MetricEvent> buildSearchSourceConsumer(StreamExecutionEnvironment env, String topic, Long time) {
        ParameterTool parameterTool = (ParameterTool) env.getConfig().getGlobalJobParameters();
        Properties props = buildKafkaPropsSearchConsumer(parameterTool);
        FlinkKafkaConsumer consumer = new FlinkKafkaConsumer<>(
                topic,
                new StandardGoodsBinlogSchema(),
                props);
        //重置offset到time时刻
        if (time != 0L) {
            Map<KafkaTopicPartition, Long> partitionOffset = buildOffsetByTime(props, parameterTool, time);
            consumer.setStartFromSpecificOffsets(partitionOffset);
        }
        // 重置消费位点
        if (parameterTool.getBoolean(KAFKA_OFFSET_RESET_LATEST)) {
            consumer.setStartFromLatest();
        }
        return env.addSource(consumer);
    }

    /**--------------------------------------- 搜索关键词 kafka 配置 End ---------------------------------------------*/


    /**--------------------------------------- 福利严选 kafka 配置 Start ---------------------------------------------*/
    private static Properties buildKafkaPropsWelfare(ParameterTool parameterTool) {
        Properties props = parameterTool.getProperties();
        props.put("bootstrap.servers", parameterTool.get(PropertiesConstants.KAFKA_WELFARE_BROKERS, DEFAULT_KAFKA_BROKERS));
        props.put("group.id", parameterTool.get(KAFKA_GROUP_ID_WELFARE, DEFAULT_KAFKA_GROUP_ID));
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        return props;
    }

    public static DataStreamSource<String> buildWelfareSource(StreamExecutionEnvironment env) {
        ParameterTool parameter = (ParameterTool) env.getConfig().getGlobalJobParameters();
        String topic = parameter.getRequired(PropertiesConstants.KAFKA_WELFARE_TOPIC);
        Long time = parameter.getLong(PropertiesConstants.CONSUMER_FROM_TIME, 0L);
        return buildWelfareSource(env, topic, time);
    }

    private static DataStreamSource<String> buildWelfareSource(StreamExecutionEnvironment env, String topic, Long time) {
        ParameterTool parameterTool = (ParameterTool) env.getConfig().getGlobalJobParameters();
        Properties props = buildKafkaPropsWelfare(parameterTool);
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
                topic,
                new SimpleStringSchema(),
                props);
        //重置offset到time时刻
        if (time != 0L) {
            Map<KafkaTopicPartition, Long> partitionOffset = buildOffsetByTime(props, parameterTool, time);
            consumer.setStartFromSpecificOffsets(partitionOffset);
        }
        // 重置消费位点
        if (parameterTool.getBoolean(KAFKA_OFFSET_RESET_LATEST)) {
            consumer.setStartFromLatest();
        }
        return env.addSource(consumer);
    }
    /**--------------------------------------- 福利严选 kafka 配置 End ---------------------------------------------*/


    /**--------------------------------------- 标准商品 kafka 配置 Start ---------------------------------------------*/
    private static Properties buildKafkaPropsStandard(ParameterTool parameterTool) {
        Properties props = parameterTool.getProperties();
        props.put("bootstrap.servers", parameterTool.get(PropertiesConstants.KAFKA_STANDARD_BROKERS, DEFAULT_KAFKA_BROKERS));
        props.put("group.id", parameterTool.get(KAFKA_GROUP_ID_STANDARD, DEFAULT_KAFKA_GROUP_ID));
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        return props;
    }

    public static DataStreamSource<String> buildStandardSource(StreamExecutionEnvironment env) {
        ParameterTool parameter = (ParameterTool) env.getConfig().getGlobalJobParameters();
        String topic = parameter.getRequired(PropertiesConstants.KAFKA_STANDARD_TOPIC);
        Long time = parameter.getLong(PropertiesConstants.CONSUMER_FROM_TIME, 0L);
        return buildStandardSource(env, topic, time);
    }

    private static DataStreamSource<String> buildStandardSource(StreamExecutionEnvironment env, String topic, Long time) {
        ParameterTool parameterTool = (ParameterTool) env.getConfig().getGlobalJobParameters();
        Properties props = buildKafkaPropsStandard(parameterTool);
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
                topic,
                new SimpleStringSchema(),
                props);
        //重置offset到time时刻
        if (time != 0L) {
            Map<KafkaTopicPartition, Long> partitionOffset = buildOffsetByTime(props, parameterTool, time);
            consumer.setStartFromSpecificOffsets(partitionOffset);
        }
        // 重置消费位点
        if (parameterTool.getBoolean(KAFKA_OFFSET_RESET_LATEST)) {
            consumer.setStartFromLatest();
        }
        return env.addSource(consumer);
    }
    /**--------------------------------------- 标准商品 kafka 配置 End ---------------------------------------------*/


    /**--------------------------------------- 基础商品实时同步es kafka 配置 Start ---------------------------------------------*/
    private static Properties buildKafkaPropsBasic2Es(ParameterTool parameterTool) {
        Properties props = parameterTool.getProperties();
        props.put("bootstrap.servers", parameterTool.get(PropertiesConstants.KAFKA_BROKERS, DEFAULT_KAFKA_BROKERS));
        props.put("group.id", parameterTool.get(KAFKA_GROUP_ID_BASIC_2_ES, DEFAULT_KAFKA_GROUP_ID));
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        return props;
    }

    public static DataStreamSource<PolarDbBinlogBatch> buildBasic2EsSource(StreamExecutionEnvironment env) {
        ParameterTool parameter = (ParameterTool) env.getConfig().getGlobalJobParameters();
        String topic = parameter.getRequired(PropertiesConstants.KAFKA_TOPIC);
        Long time = parameter.getLong(PropertiesConstants.CONSUMER_FROM_TIME, 0L);
        return buildBasic2EsSource(env, topic, time);
    }

    private static DataStreamSource<PolarDbBinlogBatch> buildBasic2EsSource(StreamExecutionEnvironment env, String topic, Long time) {
        ParameterTool parameterTool = (ParameterTool) env.getConfig().getGlobalJobParameters();
        Properties props = buildKafkaPropsBasic2Es(parameterTool);
        FlinkKafkaConsumer<PolarDbBinlogBatch> consumer = new FlinkKafkaConsumer<>(
                topic,
                new BasicBinlog2EsDeserializationSchema(),
                props);
        //重置offset到time时刻
        if (time != 0L) {
            Map<KafkaTopicPartition, Long> partitionOffset = buildOffsetByTime(props, parameterTool, time);
            consumer.setStartFromSpecificOffsets(partitionOffset);
        }
        // 重置消费位点
        if (parameterTool.getBoolean(KAFKA_OFFSET_RESET_LATEST)) {
            consumer.setStartFromLatest();
        }
        return env.addSource(consumer);
    }
    /**--------------------------------------- 基础商品实时同步es kafka 配置 ---------------------------------------------*/

    /**--------------------------------------- 基础数据全国搜索 kafka 配置 Start ---------------------------------------------*/
    private static Properties buildBasicDataGoodsAllKafkaPropsGoods(ParameterTool parameterTool) {
        Properties props = parameterTool.getProperties();
        props.put("bootstrap.servers", parameterTool.get(PropertiesConstants.KAFKA_BROKERS, DEFAULT_KAFKA_BROKERS));
//        props.put("zookeeper.connect", parameterTool.get(PropertiesConstants.KAFKA_ZOOKEEPER_CONNECT, DEFAULT_KAFKA_ZOOKEEPER_CONNECT));
        props.put("group.id", parameterTool.get(KAFKA_GROUP_ID_BASIC_GOODS_ALL, KAFKA_GROUP_ID_BASIC_GOODS_ALL));
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        return props;
    }

    public static DataStreamSource<BasicModel<Model>> buildBasicDataGoodsAllGoodsSource(StreamExecutionEnvironment env) {
        ParameterTool parameter = (ParameterTool) env.getConfig().getGlobalJobParameters();
        String topic = parameter.getRequired(PropertiesConstants.KAFKA_TOPIC_BASIC_GOODS_ALL);
        Long time = parameter.getLong(PropertiesConstants.CONSUMER_FROM_TIME, 0L);
        return buildBasicDataGoodsAllGoodsSource(env, topic, time);
    }

    private static DataStreamSource<BasicModel<Model>> buildBasicDataGoodsAllGoodsSource(StreamExecutionEnvironment env, String topic, Long time) {
        ParameterTool parameterTool = (ParameterTool) env.getConfig().getGlobalJobParameters();
        Properties props = buildBasicDataGoodsAllKafkaPropsGoods(parameterTool);
        FlinkKafkaConsumer consumer = new FlinkKafkaConsumer<>(
                topic,
                new BasicDataGoodsAllDeserializationSchema(),
                props);
        //重置offset到time时刻
        if (time != 0L) {
            Map<KafkaTopicPartition, Long> partitionOffset = buildOffsetByTime(props, parameterTool, time);
            consumer.setStartFromSpecificOffsets(partitionOffset);
        }
        // 重置消费位点
        if (parameterTool.getBoolean(KAFKA_OFFSET_RESET_LATEST)) {
            consumer.setStartFromLatest();
        }

        return env.addSource(consumer);

    }


    /**--------------------------------------- 源数据 kafka 配置 End ---------------------------------------------*/


    /**------------------------------------- 连锁商品数据变更实时触发门店 Start ------------------------------------*/
    private static Properties buildKafkaPropsGoodsStore(ParameterTool parameterTool) {
        Properties props = parameterTool.getProperties();
        props.put("bootstrap.servers", parameterTool.get(PropertiesConstants.KAFKA_BROKERS_PARTNER_GOODSALL, DEFAULT_KAFKA_BROKERS));
        props.put("group.id", parameterTool.get(KAFKA_GROUP_ID_PARTNER_GOODSALL, DEFAULT_KAFKA_GROUP_ID));
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        return props;
    }

    public static DataStreamSource<BasicModel<ElasticGoodsDTO>> buildGoodsStoreSource(StreamExecutionEnvironment env) {
        ParameterTool parameter = (ParameterTool) env.getConfig().getGlobalJobParameters();
        String topic = parameter.getRequired(PropertiesConstants.KAFKA_TOPIC_PARTNER_GOODSALL);
        Long time = parameter.getLong(PropertiesConstants.CONSUMER_FROM_TIME, 0L);
        return buildGoodsStoreSource(env, topic, time);
    }

    private static DataStreamSource<BasicModel<ElasticGoodsDTO>> buildGoodsStoreSource(StreamExecutionEnvironment env, String topic, Long time) {
        ParameterTool parameterTool = (ParameterTool) env.getConfig().getGlobalJobParameters();
        Properties props = buildKafkaPropsGoodsStore(parameterTool);
        FlinkKafkaConsumer<BasicModel<ElasticGoodsDTO>> consumer = new FlinkKafkaConsumer<>(
                topic,
                new ElasticGoodsDeserializationSchema(),
                props);
        //重置offset到time时刻
        if (time != 0L) {
            Map<KafkaTopicPartition, Long> partitionOffset = buildOffsetByTime(props, parameterTool, time);
            consumer.setStartFromSpecificOffsets(partitionOffset);
        }
        // 重置消费位点
        if (parameterTool.getBoolean(KAFKA_OFFSET_RESET_LATEST)) {
            consumer.setStartFromLatest();
        }
        return env.addSource(consumer);
    }
    /**------------------------------------- 连锁商品数据变更实时触发门店 End ------------------------------------*/


    /**------------------------------------ 连锁商品/基础商品数据变更推送 kafka es kafka 配置 ----------------------------*/
    public static FlinkKafkaProducer<BasicModel<ElasticO2O>> buildGoodsChangeProducer(ParameterTool parameterTool) {
        Properties props = new Properties();
        props.put("bootstrap.servers", parameterTool.get(KAFKA_BROKERS_PARTNER_GOODSALL, DEFAULT_KAFKA_BROKERS));
        return new FlinkKafkaProducer<>(parameterTool.get(KAFKA_TOPIC_PARTNER_GOODSALL),
                new GoodsStoreSerializationSchema(), props);
    }

    /**------------------------------------ 连锁商品/基础商品数据变更推送新索引 kafka es kafka 配置 ----------------------------*/
    public static FlinkKafkaProducer<BasicModel<ElasticO2O>> buildGoodsChangeProducerPartner(ParameterTool parameterTool) {
        Properties props = new Properties();
        props.put("bootstrap.servers", parameterTool.get(KAFKA_BROKERS_PARTNER_GOODSALL, DEFAULT_KAFKA_BROKERS));
        return new FlinkKafkaProducer<>(parameterTool.get(KAFKA_TOPIC_PARTNER_GOODS_CHANGE),
                new GoodsChangeSerializationSchema(), props);
    }

    public static FlinkKafkaProducer<BasicModel<Model>> buildBasicGoodsChangeProducer(ParameterTool parameterTool) {
        Properties props = new Properties();
        props.put("bootstrap.servers", parameterTool.get(KAFKA_BROKERS_GOODSALL, DEFAULT_KAFKA_BROKERS));
        return new FlinkKafkaProducer<>(parameterTool.get(KAFKA_TOPIC_GOODSALL),
                new BasicGoodsSerializationSchema(), props);
    }
    /**------------------------------------ 连锁商品/基础商品数据变更推送 kafka es kafka 配置 ----------------------------*/

    /**------------------------------------ 标准商品es字段监控binlog kafka es kafka 配置 start ----------------------------*/
    private static Properties buildKafkaPropsStandardGoods(ParameterTool parameterTool) {
        Properties props = parameterTool.getProperties();
        props.put("bootstrap.servers", parameterTool.get(PropertiesConstants.KAFKA_BROKERS, DEFAULT_KAFKA_BROKERS));
        props.put("group.id", parameterTool.get(KAFKA_GROUP_ID_STANDARDGOODS, DEFAULT_KAFKA_GROUP_ID));
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        return props;
    }

    public static DataStreamSource<PolarDbBinlogBatch> buildStandardGoodsSource(StreamExecutionEnvironment env) {
        ParameterTool parameter = (ParameterTool) env.getConfig().getGlobalJobParameters();
        String topic = parameter.getRequired(PropertiesConstants.KAFKA_TOPIC);
        Long time = parameter.getLong(PropertiesConstants.CONSUMER_FROM_TIME, 0L);
        return buildStandardGoodsSource(env, topic, time);
    }

    private static DataStreamSource<PolarDbBinlogBatch> buildStandardGoodsSource(StreamExecutionEnvironment env, String topic, Long time) {
        ParameterTool parameterTool = (ParameterTool) env.getConfig().getGlobalJobParameters();
        Properties props = buildKafkaPropsStandardGoods(parameterTool);
        FlinkKafkaConsumer<PolarDbBinlogBatch> consumer = new FlinkKafkaConsumer<>(
                topic,
                new StandardGoodsBinlogSchema(),
                props);
        //重置offset到time时刻
        if (time != 0L) {
            Map<KafkaTopicPartition, Long> partitionOffset = buildOffsetByTime(props, parameterTool, time);
            consumer.setStartFromSpecificOffsets(partitionOffset);
        }
        // 重置消费位点
        if (parameterTool.getBoolean(KAFKA_OFFSET_RESET_LATEST)) {
            consumer.setStartFromLatest();
        }

        return env.addSource(consumer);
    }
    /**------------------------------------ 标准商品es字段监控binlog kafka es kafka 配置 end ----------------------------*/


    /**--------------------------------------- 对码 kafka 配置 Start ---------------------------------------------*/
    private static Properties buildKafkaPropsMatchCode(ParameterTool parameterTool) {
        Properties props = parameterTool.getProperties();
        props.put("bootstrap.servers", parameterTool.get(KAFKA_MATCHCODE_BROKERS, DEFAULT_KAFKA_BROKERS));
        props.put("group.id", parameterTool.get(KAFKA_GROUP_ID_MATCHCODE, DEFAULT_KAFKA_GROUP_ID));
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        return props;
    }

    public static DataStreamSource<MatchCodeDTO> buildMatchCodeSource(StreamExecutionEnvironment env) {
        ParameterTool parameter = (ParameterTool) env.getConfig().getGlobalJobParameters();
        String topic = parameter.getRequired(KAFKA_MATCHCODE_TOPIC);
        Long time = parameter.getLong(PropertiesConstants.CONSUMER_FROM_TIME, 0L);
        return buildMatchCodeSource(env, topic, time);
    }

    private static DataStreamSource<MatchCodeDTO> buildMatchCodeSource(StreamExecutionEnvironment env, String topic, Long time) {
        ParameterTool parameterTool = (ParameterTool) env.getConfig().getGlobalJobParameters();
        Properties props = buildKafkaPropsMatchCode(parameterTool);
        FlinkKafkaConsumer<MatchCodeDTO> consumer = new FlinkKafkaConsumer<>(
                topic,
                new MatchCodeDeserializationSchema(),
                props);
        //重置offset到time时刻
        if (time != 0L) {
            Map<KafkaTopicPartition, Long> partitionOffset = buildOffsetByTime(props, parameterTool, time);
            consumer.setStartFromSpecificOffsets(partitionOffset);
        }
        // 重置消费位点
        if (parameterTool.getBoolean(KAFKA_OFFSET_RESET_LATEST)) {
            consumer.setStartFromLatest();
        }
        return env.addSource(consumer);
    }
    /**--------------------------------------- 基础数据 kafka 配置 End ---------------------------------------------*/



    /**--------------------------------------- 商品中心消息通知报警 kafka 配置 Start ---------------------------------------------*/
    public static DataStreamSource<PolarDbBinlogBatch> buildSourceGoodsCenterAlarm(StreamExecutionEnvironment env) {
        ParameterTool parameterTool = (ParameterTool) env.getConfig().getGlobalJobParameters();
        String topic = parameterTool.getRequired(KAFKA_GC_ALARM_TOPIC);
        Long time = parameterTool.getLong(PropertiesConstants.CONSUMER_FROM_TIME, 0L);
        Properties props = buildKafkaProps(parameterTool, KAFKA_GC_ALARM_BROKERS, KAFKA_GROUP_ID_GC_ALARM);
        FlinkKafkaConsumer<PolarDbBinlogBatch> consumer = new FlinkKafkaConsumer<>(
                topic,
                new BinlogDeserializationSchema(),
                props);
        //重置offset到time时刻
        if (time != 0L) {
            Map<KafkaTopicPartition, Long> partitionOffset = buildOffsetByTime(props, parameterTool, time);
            consumer.setStartFromSpecificOffsets(partitionOffset);
        }
        // 重置消费位点
        if (parameterTool.getBoolean(KAFKA_OFFSET_RESET_LATEST)) {
            consumer.setStartFromLatest();
        }
        return env.addSource(consumer);
    }
    /**--------------------------------------- 商品中心消息通知报警 kafka 配置 End ---------------------------------------------*/



    /**--------------------------------------- 库存 kafka 配置 Start ---------------------------------------------*/
    private static Properties buildKafkaPropsStock(ParameterTool parameterTool) {
        Properties props = parameterTool.getProperties();
        props.put("bootstrap.servers", parameterTool.get(KAFKA_STOCK_BROKERS, DEFAULT_KAFKA_BROKERS));
        props.put("group.id", parameterTool.get(KAFKA_GROUP_ID_STOCK, DEFAULT_KAFKA_GROUP_ID));
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        return props;
    }

    public static DataStreamSource<PolarDbBinlogBatch> buildStockSource(StreamExecutionEnvironment env) {
        ParameterTool parameter = (ParameterTool) env.getConfig().getGlobalJobParameters();
        String topic = parameter.getRequired(KAFKA_STOCK_TOPIC);
        Long time = parameter.getLong(PropertiesConstants.CONSUMER_FROM_TIME, 0L);
        return buildStockSource(env, topic, time);
    }

    private static DataStreamSource<PolarDbBinlogBatch> buildStockSource(StreamExecutionEnvironment env, String topic, Long time) {
        ParameterTool parameterTool = (ParameterTool) env.getConfig().getGlobalJobParameters();
        Properties props = buildKafkaPropsStock(parameterTool);
        FlinkKafkaConsumer<PolarDbBinlogBatch> consumer = new FlinkKafkaConsumer<>(
                topic,
                new BinlogDeserializationSchema(),
                props);
        //重置offset到time时刻
        if (time != 0L) {
            Map<KafkaTopicPartition, Long> partitionOffset = buildOffsetByTime(props, parameterTool, time);
            consumer.setStartFromSpecificOffsets(partitionOffset);
        }
        // 重置消费位点
        if (parameterTool.getBoolean(KAFKA_OFFSET_RESET_LATEST)) {
            consumer.setStartFromLatest();
        }
        return env.addSource(consumer);
    }

    /**--------------------------------------- 基础数据 kafka 配置 End ---------------------------------------------*/


    /**--------------------------------------- 价格 kafka 配置 Start ---------------------------------------------*/
    private static Properties buildKafkaPropsPrice(ParameterTool parameterTool) {
        Properties props = parameterTool.getProperties();
        props.put("bootstrap.servers", parameterTool.get(KAFKA_PRICE_BROKERS, DEFAULT_KAFKA_BROKERS));
        props.put("group.id", parameterTool.get(KAFKA_GROUP_ID_PRICE, DEFAULT_KAFKA_GROUP_ID));
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");
        return props;
    }

    public static DataStreamSource<PolarDbBinlogBatch> buildPriceSource(StreamExecutionEnvironment env) {
        ParameterTool parameter = (ParameterTool) env.getConfig().getGlobalJobParameters();
        String topic = parameter.getRequired(KAFKA_PRICE_TOPIC);
        Long time = parameter.getLong(PropertiesConstants.CONSUMER_FROM_TIME, 0L);
        return buildPriceSource(env, topic, time);
    }

    private static DataStreamSource<PolarDbBinlogBatch> buildPriceSource(StreamExecutionEnvironment env, String topic, Long time) {
        ParameterTool parameterTool = (ParameterTool) env.getConfig().getGlobalJobParameters();
        Properties props = buildKafkaPropsPrice(parameterTool);
        FlinkKafkaConsumer<PolarDbBinlogBatch> consumer = new FlinkKafkaConsumer<>(
                topic,
                new BinlogDeserializationSchema(),
                props);
        //重置offset到time时刻
        if (time != 0L) {
            Map<KafkaTopicPartition, Long> partitionOffset = buildOffsetByTime(props, parameterTool, time);
            consumer.setStartFromSpecificOffsets(partitionOffset);
        }
        // 重置消费位点
        if (parameterTool.getBoolean(KAFKA_OFFSET_RESET_LATEST)) {
            consumer.setStartFromLatest();
        }
        return env.addSource(consumer);
    }
    /**--------------------------------------- 基础数据 kafka 配置 End ---------------------------------------------*/



    /**--------------------------------------- 门店级商品上下架状态变更 kafka 配置 Start ---------------------------------------------*/
    public static DataStreamSource<PolarDbBinlogBatch> buildSourceGoodsStateChange(StreamExecutionEnvironment env) {
        ParameterTool parameterTool = (ParameterTool) env.getConfig().getGlobalJobParameters();
        String topic = parameterTool.getRequired(KAFKA_GOODS_STATE_CHANGE_TOPIC);
        Long time = parameterTool.getLong(PropertiesConstants.CONSUMER_FROM_TIME, 0L);
        Properties props = buildKafkaProps(parameterTool, KAFKA_GOODS_STATE_CHANGE_BROKERS, KAFKA_GROUP_ID_GOODS_STATE_CHANGE);
        FlinkKafkaConsumer<PolarDbBinlogBatch> consumer = new FlinkKafkaConsumer<>(
                topic,
                new BinlogDeserializationSchema(),
                props);
        //重置offset到time时刻
        if (time != 0L) {
            Map<KafkaTopicPartition, Long> partitionOffset = buildOffsetByTime(props, parameterTool, time);
            consumer.setStartFromSpecificOffsets(partitionOffset);
        }
        // 重置消费位点
        if (parameterTool.getBoolean(KAFKA_OFFSET_RESET_LATEST)) {
            consumer.setStartFromLatest();
        }
        return env.addSource(consumer);
    }

    /**
     * DTP 门店级商品状态变更推送kafka
     * @param parameterTool
     * @return
     */
    public static FlinkKafkaProducer<StockGoods> buildPushDtpGoodsStateProducer(ParameterTool parameterTool) {
        Properties props = new Properties();
        props.put("bootstrap.servers", parameterTool.get(KAFKA_PUSH_STOCK_STATE_BROKERS));
        String topic = parameterTool.get(KAFKA_PUSH_STOCK_STATE_DTP_TOPIC);
        return new FlinkKafkaProducer<>(topic,
                new PushGoodsStateSchema(topic), props, FlinkKafkaProducer.Semantic.AT_LEAST_ONCE);
    }

    /**
     * 门店级商品状态变更推送kafka
     * @param parameterTool
     * @return
     */
    public static FlinkKafkaProducer<StockGoods> buildPushGoodsStateProducer(ParameterTool parameterTool) {
        Properties props = new Properties();
        props.put("bootstrap.servers", parameterTool.get(KAFKA_PUSH_STOCK_STATE_BROKERS));
        String topic = parameterTool.get(KAFKA_PUSH_STOCK_STATE_TOPIC);
        return new FlinkKafkaProducer<>(topic,
                new PushGoodsStateSchema(topic), props, FlinkKafkaProducer.Semantic.AT_LEAST_ONCE);
    }
    /**--------------------------------------- 门店级商品上下架状态变更 kafka 配置 End ---------------------------------------------*/
}
