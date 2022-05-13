package cn.wr.utils;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
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
}
