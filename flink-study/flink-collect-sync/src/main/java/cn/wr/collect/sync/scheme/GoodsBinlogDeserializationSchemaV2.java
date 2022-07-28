package cn.wr.collect.sync.scheme;

import cn.wr.collect.sync.constants.SchemaTableRelative;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import static cn.wr.collect.sync.constants.CommonConstants.*;

public class GoodsBinlogDeserializationSchemaV2 implements DeserializationSchema<PolarDbBinlogBatch> {
    private static final long serialVersionUID = 5958003487280393782L;
    private static final Logger log = LoggerFactory.getLogger(GoodsBinlogDeserializationSchemaV2.class);

    @Override
    public PolarDbBinlogBatch deserialize(byte[] bytes) {
        try {
            // 解析binlog
            PolarDbBinlogBatch binlog = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), PolarDbBinlogBatch.class);
            if (binlog.getIsDdl()
                    || !SchemaTableRelative.checkGoods2EsValid(binlog.getDatabase(), binlog.getTable())) {
                return null;
            }
            return binlog;
        } catch (Exception e) {
            log.error("GoodsBinlogDeserializationSchema02 msg:{} Exception:{}", new String(bytes, StandardCharsets.UTF_8), e);
            return null;
        }
    }

    @Override
    public boolean isEndOfStream(PolarDbBinlogBatch binlogBatch) {
        return false;
    }

    @Override
    public TypeInformation<PolarDbBinlogBatch> getProducedType() {
        return TypeInformation.of(PolarDbBinlogBatch.class);
    }
}
