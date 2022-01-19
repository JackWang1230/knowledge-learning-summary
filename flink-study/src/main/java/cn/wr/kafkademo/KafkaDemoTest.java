package cn.wr.kafkademo;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.sink.KafkaSinkBuilder;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Test;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author RWang
 * @Date 2022/1/19
 */

public class KafkaDemoTest {

    private static StreamExecutionEnvironment env  = StreamExecutionEnvironment.getExecutionEnvironment();

   @Test
   /**
    * flink 1.13版本对应 flink sink端接收数据的样式
    */
    public void testFlinkKafka13(){
       Properties properties = new Properties();
       properties.setProperty("bootstrap.servers","localhost:9092");
       DataStreamSource<String> data = env.addSource(new SourceFunction<String>() {
           private static final long serialVersionUID = 6850747545869479036L;

           @Override
           public void run(SourceContext<String> ctx) throws Exception {

           }

           @Override
           public void cancel() {

           }
       });
       KafkaSerializationSchema<String> serializationSchema = new KafkaSerializationSchema<String>() {
           @Override
           public ProducerRecord<byte[], byte[]> serialize(String s, @Nullable Long aLong) {
               return new ProducerRecord<>("my-topic", s.getBytes(StandardCharsets.UTF_8));
           }
       };
       FlinkKafkaProducer<String> myProducer = new FlinkKafkaProducer<>("my-topic", serializationSchema, properties, FlinkKafkaProducer.Semantic.EXACTLY_ONCE);

       data.addSink(myProducer);

   }



    @Test
    /**
     * flink 1.14版本对应 flink sink端接收数据的样式
     */
    public void testFlinkKafka14(){

        DataStreamSource<String> data = env.addSource(new SourceFunction<String>() {
            private static final long serialVersionUID = 6850747545869479036L;

            @Override
            public void run(SourceContext<String> ctx) throws Exception {

            }

            @Override
            public void cancel() {

            }
        });
        KafkaSink<String> build = KafkaSink.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("my-topic")
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()
                        )
                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
        data.sinkTo(build);




    }
}
