package cn.wr.collect.sync.scheme;

import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.kafka.ElasticGoodsDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.flink.api.common.serialization.SerializationSchema;

public class GoodsStoreSerializationSchema implements SerializationSchema<BasicModel<ElasticO2O>> {
    private static final long serialVersionUID = 1856873690465155613L;

    @Override
    public byte[] serialize(BasicModel<ElasticO2O> model) {
        return JSON.toJSONBytes(this.transfer(model), SerializerFeature.WriteMapNullValue);
    }

    /**
     * 构造kafka消息对象
     * @param model
     * @return
     */
    private BasicModel<ElasticGoodsDTO> transfer(BasicModel<ElasticO2O> model) {
        return new BasicModel<>(model.getTableName(), model.getOperate(),
                new ElasticGoodsDTO().transfer(model.getData()),
                model.getModFieldList());
    }
}
