package cn.wr.collect.sync.scheme;

import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.basic.BasicModel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.flink.api.common.serialization.SerializationSchema;

public class GoodsChangeSerializationSchema implements SerializationSchema<BasicModel<ElasticO2O>> {
    private static final long serialVersionUID = 1856873690465155613L;

    @Override
    public byte[] serialize(BasicModel<ElasticO2O> model) {
        return JSON.toJSONBytes(model, SerializerFeature.WriteMapNullValue);
    }
}
