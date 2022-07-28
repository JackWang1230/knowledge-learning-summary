package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.ElasticEnum;
import cn.wr.collect.sync.model.standard.StandardElastic;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class StandardElasticSink implements ElasticsearchSinkFunction<StandardElastic> {
    private static final long serialVersionUID = -3748739301697801553L;
    private static final Logger log = LoggerFactory.getLogger(StandardElasticSink.class);

    @Override
    public void process(StandardElastic standardElastic, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
        if (Objects.isNull(standardElastic)) {
            return;
        }
        requestIndexer.add(Requests.indexRequest()
                .index(ElasticEnum.STANDARD.getIndex())
                .type(ElasticEnum.STANDARD.getType())
                .source(JSON.toJSONBytes(standardElastic, SerializerFeature.WriteMapNullValue), XContentType.JSON)
                .id(String.valueOf(standardElastic.getId())));
    }
}
