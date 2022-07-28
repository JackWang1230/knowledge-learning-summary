package cn.wr.collect.sync.scheme;

import cn.wr.collect.sync.constants.SchemaTableRelative;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BasicBinlog2EsDeserializationSchema implements DeserializationSchema<PolarDbBinlogBatch> {
    private static final long serialVersionUID = 5958003487280393782L;
    private static final Logger log = LoggerFactory.getLogger(BasicBinlog2EsDeserializationSchema.class);

    @Override
    public PolarDbBinlogBatch deserialize(byte[] bytes) throws IOException {
        try {
            // 解析binlog
            PolarDbBinlogBatch binlog = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), PolarDbBinlogBatch.class);
            if (binlog.getIsDdl() || !SchemaTableRelative.checkBasic2EsValid(binlog.getDatabase(), binlog.getTable())) {
                return null;
            }
            log.info("BasicBinlog2EsDeserializationSchema binlog:{}", JSON.toJSONString(binlog));
            return binlog;
        }
        catch (Exception e) {
            log.error("BasicBinlog2EsDeserializationSchema msg:{} Exception:{}", new String(bytes, StandardCharsets.UTF_8), e);
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
