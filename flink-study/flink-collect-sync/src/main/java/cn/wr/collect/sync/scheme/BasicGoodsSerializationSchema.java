package cn.wr.collect.sync.scheme;

import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.basic.BasicModel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.flink.api.common.serialization.SerializationSchema;

public class BasicGoodsSerializationSchema implements SerializationSchema<BasicModel<Model>> {
    private static final long serialVersionUID = 6667402456591249929L;

    @Override
    public byte[] serialize(BasicModel<Model> model) {
        return JSON.toJSONBytes(model, SerializerFeature.WriteMapNullValue);
    }
}
