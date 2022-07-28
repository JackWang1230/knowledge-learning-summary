package cn.wr.collect.sync.scheme;

import cn.wr.collect.sync.model.chain.MatchCodeDTO;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class MatchCodeDeserializationSchema implements DeserializationSchema<MatchCodeDTO> {
    private static final long serialVersionUID = -4929821848837206518L;
    private static final Logger log = LoggerFactory.getLogger(MatchCodeDeserializationSchema.class);

    @Override
    public MatchCodeDTO deserialize(byte[] message) {
        try {
            // 解析binlog
            return JSON.parseObject(new String(message, StandardCharsets.UTF_8), MatchCodeDTO.class);
        }
        catch (Exception e) {
            log.error("MatchCodeDeserializationSchema msg:{} Exception:{}", new String(message, StandardCharsets.UTF_8), e);
            return null;
        }
    }

    @Override
    public boolean isEndOfStream(MatchCodeDTO dto) {
        return false;
    }

    @Override
    public TypeInformation<MatchCodeDTO> getProducedType() {
        return TypeInformation.of(MatchCodeDTO.class);
    }

}
