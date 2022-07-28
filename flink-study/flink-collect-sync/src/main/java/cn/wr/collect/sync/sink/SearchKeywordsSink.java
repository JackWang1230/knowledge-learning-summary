package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.PropertiesConstants;
import cn.wr.collect.sync.model.ElasticSearchKeywords;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchKeywordsSink implements ElasticsearchSinkFunction<ElasticSearchKeywords> {
    private static final long serialVersionUID = -3653860119670298246L;
    private static final Logger LOG = LoggerFactory.getLogger(SearchKeywordsSink.class);

    @Override
    public void process(ElasticSearchKeywords keywords, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
        requestIndexer.add(Requests.indexRequest()
                .index(PropertiesConstants.ES_INDEX_WORD)
                .type(PropertiesConstants.ES_DOCUMENT_TYPE_WORD)
                .source(JSON.toJSONBytes(keywords, SerializerFeature.WriteMapNullValue), XContentType.JSON)
                .id(keywords.getId()));
    }
}
