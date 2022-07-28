package cn.wr.utils;

import cn.wr.constants.PropertiesConstants;
import cn.wr.model.StockData;
import cn.wr.scheme.StockDeserializerScheme;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

import static cn.wr.constants.PropertiesConstants.*;

/**
 * kafka 读取工具类
 * @author RWang
 * @Date 2022/5/11
 */

public class KafkaUtil {


    private static Properties createKafkaProps(ParameterTool params) {

        Properties props = params.getProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, params.get(KAFKA_CONFIG_TABLE_SERVERS));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KAFKA_CONFIG_TABLE_GROUP);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return props;
    }

    public static DataStreamSource<String> createCanalSource(StreamExecutionEnvironment env) {

        ParameterTool parameters = (ParameterTool) env.getConfig().getGlobalJobParameters();
        Properties props = createKafkaProps(parameters);
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(parameters.get(KAFKA_CONFIG_TABLE_TOPIC),
                new SimpleStringSchema(), props);
        if (parameters.get(KAFKA_CONFIG_TABLE_OFFSET).equals("earliest")){
            consumer.setStartFromEarliest();
        }
        return env.addSource(consumer);
    }

    /**
     * flink 1.14 新版获取kafka 数据源方式
     *
     * @param env
     * @return
     */
    public static DataStreamSource<StockData> createKafkaSource(StreamExecutionEnvironment env) {

        ParameterTool parameters = (ParameterTool) env.getConfig().getGlobalJobParameters();
        KafkaSource<StockData> source = KafkaSource.<StockData>builder()
                .setBootstrapServers(parameters.get(PropertiesConstants.KAFKA_CONFIG_STOCK_SERVERS))
                .setGroupId(parameters.get(PropertiesConstants.KAFKA_CONFIG_STOCK_GROUP))
                .setTopics(parameters.get(PropertiesConstants.KAFKA_CONFIG_STOCK_TOPICS).split(COMMA_EN))
                .setDeserializer(KafkaRecordDeserializationSchema.valueOnly(new StockDeserializerScheme()))
//                .setStartingOffsets(OffsetsInitializer.earliest())
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST))
                .build();
        return env.fromSource(source, WatermarkStrategy.noWatermarks(), "kafka stock source");
        // .setValueOnlyDeserializer(new StockDeserializerScheme())
    }
}
