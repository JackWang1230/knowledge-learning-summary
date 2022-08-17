package cn.wr.schema;

import cn.wr.model.CanalDataModel;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author RWang
 * @Date 2022/8/16
 */

public class CanalDeserializerSchema implements DeserializationSchema<CanalDataModel> {

    private static final Logger logger = LoggerFactory.getLogger(CanalDeserializerSchema.class);
    private static final long serialVersionUID = 7445150046242868614L;

    @Override
    public CanalDataModel deserialize(byte[] message) throws IOException {
        try {
            return JSON.parseObject(new String(message, StandardCharsets.UTF_8), CanalDataModel.class);
        }catch (Exception e){
            logger.error("StockDeserializerSchema msg:{} Exception:{}",new String(message,StandardCharsets.UTF_8),e);
            return null;
        }
    }

    @Override
    public boolean isEndOfStream(CanalDataModel nextElement) {
        return false;
    }

    @Override
    public TypeInformation<CanalDataModel> getProducedType() {
        return TypeInformation.of(CanalDataModel.class);
    }
}
