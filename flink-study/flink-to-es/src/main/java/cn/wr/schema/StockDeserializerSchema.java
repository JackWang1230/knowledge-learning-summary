package cn.wr.schema;

import cn.wr.model.StockData;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author RWang
 * @Date 2022/7/19
 */

public class StockDeserializerSchema implements DeserializationSchema<StockData> {
    private static final long serialVersionUID = 5935203204849803042L;
    private static final Logger logger = LoggerFactory.getLogger(StockDeserializerSchema.class);

    @Override
    public StockData deserialize(byte[] message) {
        try {
            return JSON.parseObject(new String(message, StandardCharsets.UTF_8), StockData.class);
        }catch (Exception e){
            logger.error("StockDeserializerSchema msg:{} Exception:{}",new String(message,StandardCharsets.UTF_8),e);
            return null;
        }

    }

    @Override
    public boolean isEndOfStream(StockData nextElement) {
        return false;
    }

    @Override
    public TypeInformation<StockData> getProducedType() {
        return TypeInformation.of(StockData.class);
    }
}
