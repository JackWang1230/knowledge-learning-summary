package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.ElasticEnum;
import cn.wr.collect.sync.model.welfare.WelfareElastic;
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


public class WelfareElasticSink implements ElasticsearchSinkFunction<WelfareElastic> {
    private static final long serialVersionUID = 5491081553874972108L;
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsElasticSink.class);

    @Override
    public void process(WelfareElastic welfareElastic, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
        if (Objects.isNull(welfareElastic)
                || (!Objects.equals(CommonConstants.WELFARE_CHANNEL_7, welfareElastic.getChannel())
                && !Objects.equals(CommonConstants.WELFARE_CHANNEL_8, welfareElastic.getChannel()))) {
            return;
        }
        LOGGER.info("WelfareElasticSink json: {}",
                JSON.toJSONString(welfareElastic, SerializerFeature.WriteMapNullValue));
        requestIndexer.add(Requests.indexRequest()
                .index(ElasticEnum.WELFARE.getIndex())
                .type(ElasticEnum.WELFARE.getType())
                .source(JSON.toJSONBytes(welfareElastic, SerializerFeature.WriteMapNullValue), XContentType.JSON)
                .id(welfareElastic.getUniqueId()));
    }

}
