package cn.wr.collect.sync.scheme;

import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 反序列化kafka消息
 */
public class BasicDataGoodsAllDeserializationSchema implements DeserializationSchema<BasicModel<Model>> {
    private static final long serialVersionUID = 5958003487280393782L;
    private static final Logger log = LoggerFactory.getLogger(BasicDataGoodsAllDeserializationSchema.class);

    @Override
    public BasicModel<Model> deserialize(byte[] bytes) throws IOException {
        try {
            // 解析binlog
            BasicModel<Model> binlog = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), BasicModel.class);
            Class<? extends Model> clazz = Table.BaseDataTable.getClazz(binlog.getTableName());
            binlog.setData(JSON.parseObject(JSON.toJSONString(binlog.getData()), clazz));
            if(Objects.nonNull(binlog.getOld())){
                binlog.setOld(JSON.parseObject(JSON.toJSONString(binlog.getOld()), clazz));
            }
            log.info("BasicDataGoodsAllDeserializationSchema binlog:{}", JSON.toJSONString(binlog));
            return binlog;
        } catch (Exception e) {
            log.error("BasicDataGoodsAllDeserializationSchema msg:{} Exception:{}", new String(bytes, StandardCharsets.UTF_8), e);
            return null;
        }
    }

    @Override
    public boolean isEndOfStream(BasicModel binlog) {
        return false;
    }

    @Override
    public TypeInformation<BasicModel<Model>> getProducedType() {
        return TypeInformation.of(new TypeHint<BasicModel<Model>>() {});
    }
}
