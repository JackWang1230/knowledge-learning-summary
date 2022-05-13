package cn.wr.sink;


import cn.wr.model.GcConfigSkuStar;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;

import static cn.wr.constants.PropertiesConstants.ELASTICSEARCH_DOCUMENT_TYPE_WORD;
import static cn.wr.constants.PropertiesConstants.ELASTICSEARCH_INDEX_WORD;


/**
 *
 * 连锁实物商品星级数据写入es
 * @author RWang
 * @Date 2022/5/12
 */

public class GcConfigSkuStarSink implements ElasticsearchSinkFunction<GcConfigSkuStar> {

    private static final long serialVersionUID = -4820585480275528141L;
    private final ParameterTool parameterTool;

    public GcConfigSkuStarSink(ParameterTool parameterTool){
        this.parameterTool=parameterTool;
    }

    @Override
    public void process(GcConfigSkuStar gcConfigSkuStar, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {

        requestIndexer.add(Requests.indexRequest()
                .index(parameterTool.get(ELASTICSEARCH_INDEX_WORD))
                .type(parameterTool.get(ELASTICSEARCH_DOCUMENT_TYPE_WORD))
                .source(JSON.toJSONBytes(gcConfigSkuStar, SerializerFeature.WriteMapNullValue), XContentType.JSON)
                .id(gcConfigSkuStar.getSkuNo()));
    }
}
