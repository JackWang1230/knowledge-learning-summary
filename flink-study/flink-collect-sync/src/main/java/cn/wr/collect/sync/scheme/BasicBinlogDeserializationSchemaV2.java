package cn.wr.collect.sync.scheme;

import cn.wr.collect.sync.constants.SchemaTableRelative;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class BasicBinlogDeserializationSchemaV2 implements DeserializationSchema<PolarDbBinlogBatch> {
    private static final long serialVersionUID = -4929821848837206518L;
    private static final Logger log = LoggerFactory.getLogger(BasicBinlogDeserializationSchemaV2.class);

    @Override
    public PolarDbBinlogBatch deserialize(byte[] message) {
        try {
            // 解析binlog
            PolarDbBinlogBatch binlog = JSON.parseObject(new String(message, StandardCharsets.UTF_8), PolarDbBinlogBatch.class);
            if (binlog.getIsDdl() || !SchemaTableRelative.checkBasicValid(binlog.getDatabase(), binlog.getTable())) {
                return null;
            }
            return binlog;
        }
        catch (Exception e) {
            log.error("BasicBinlogDeserializationSchemaV2 msg:{} Exception:{}", new String(message, StandardCharsets.UTF_8), e);
            return null;
        }
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
