package cn.wr.collect.sync.scheme;

import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.kafka.ElasticGoodsDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static cn.wr.collect.sync.constants.CommonConstants.*;
import static cn.wr.collect.sync.constants.CommonConstants.GOODSSTOREFILTER;

/**
 * 反序列化kafka消息
 */
public class ElasticGoodsDeserializationSchema implements DeserializationSchema<BasicModel<ElasticGoodsDTO>> {
    private static final long serialVersionUID = 5958003487280393782L;
    private static final Logger log = LoggerFactory.getLogger(ElasticGoodsDeserializationSchema.class);

    @Override
    public BasicModel<ElasticGoodsDTO> deserialize(byte[] bytes) throws IOException {
        try {
            // 解析binlog
            BasicModel<ElasticGoodsDTO> model = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8),
                    new TypeReference<BasicModel<ElasticGoodsDTO>>(){});
            if (!GOODSSTOREFILTER.contains(model.getTableName())) {
                return null;
            }
            return model;
        }
        catch (Exception e) {
            log.error("ElasticGoodsDeserializationSchema msg:{} Exception:{}", new String(bytes, StandardCharsets.UTF_8), e);
            return null;
        }
    }

    @Override
    public boolean isEndOfStream(BasicModel model) {
        return false;
    }

    @Override
    public TypeInformation<BasicModel<ElasticGoodsDTO>> getProducedType() {
        return TypeInformation.of(new TypeHint<BasicModel<ElasticGoodsDTO>>() {});
    }
}
