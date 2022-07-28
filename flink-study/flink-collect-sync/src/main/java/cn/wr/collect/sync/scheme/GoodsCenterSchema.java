package cn.wr.collect.sync.scheme;

import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.model.goodscenter.GoodsCenterDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;

public class GoodsCenterSchema implements KafkaSerializationSchema<GoodsCenterDTO> {
    private static final long serialVersionUID = -6898947058214436049L;

    @Override
    public ProducerRecord<byte[], byte[]> serialize(GoodsCenterDTO element, @Nullable Long timestamp) {
        String key = element.getMerchantId() + SymbolConstants.HOR_LINE + element.getInternalId();
        String value = JSON.toJSONString(element, SerializerFeature.WriteMapNullValue);
        return new ProducerRecord<>("topic_merchant_partner_goods",
                key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8));
    }
}
