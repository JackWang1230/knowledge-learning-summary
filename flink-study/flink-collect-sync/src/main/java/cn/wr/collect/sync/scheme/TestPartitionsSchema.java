package cn.wr.collect.sync.scheme;

import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.model.kafka.TestPartitions;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;

public class TestPartitionsSchema implements KafkaSerializationSchema<TestPartitions> {

    @Override
    public ProducerRecord<byte[], byte[]> serialize(TestPartitions element, @Nullable Long timestamp) {
        String key = element.getId() + SymbolConstants.HOR_LINE + element.getText();
        String value = JSON.toJSONString(element, SerializerFeature.WriteMapNullValue);
        return new ProducerRecord<>("topic-partitions",
                key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8));
    }
}
