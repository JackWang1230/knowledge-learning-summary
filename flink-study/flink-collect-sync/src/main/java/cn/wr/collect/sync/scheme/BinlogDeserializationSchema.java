package cn.wr.collect.sync.scheme;

import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class BinlogDeserializationSchema implements DeserializationSchema<PolarDbBinlogBatch> {
    private static final long serialVersionUID = 1869052620002503780L;
    private static final Logger log = LoggerFactory.getLogger(BinlogDeserializationSchema.class);

    @Override
    public PolarDbBinlogBatch deserialize(byte[] message) {
        PolarDbBinlogBatch binlog = JSON.parseObject(new String(message, StandardCharsets.UTF_8), PolarDbBinlogBatch.class);
        if (binlog.getIsDdl()) {
            log.info("BinlogDeserializationSchema ddl: {}", JSON.toJSONString(binlog));
            return null;
        }
        return binlog;
    }

    @Override
    public boolean isEndOfStream(PolarDbBinlogBatch binlog) {
        return false;
    }

    @Override
    public TypeInformation<PolarDbBinlogBatch> getProducedType() {
        return TypeInformation.of(PolarDbBinlogBatch.class);
    }

}
