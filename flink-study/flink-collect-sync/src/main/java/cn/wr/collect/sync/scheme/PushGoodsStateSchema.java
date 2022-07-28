package cn.wr.collect.sync.scheme;

import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.model.stock.StockGoods;
import com.alibaba.fastjson.JSON;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;

public class PushGoodsStateSchema implements KafkaSerializationSchema<StockGoods> {
    private static final long serialVersionUID = -6898947058214436049L;
    private final String topic;

    public PushGoodsStateSchema(String topic) {
        this.topic = topic;
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(StockGoods element, @Nullable Long timestamp) {
        final String key = element.getMerchantId() + SymbolConstants.HOR_LINE + element.getStoreId()
                + SymbolConstants.HOR_LINE + element.getInternalId();
        final String value = JSON.toJSONString(element);
        return new ProducerRecord<>(topic, key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8));
    }
}
