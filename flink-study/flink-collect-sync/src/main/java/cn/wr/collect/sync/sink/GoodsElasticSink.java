package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.ElasticEnum;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.MetricItem;
import cn.wr.collect.sync.model.ParamsConcat;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.collect.sync.constants.CommonConstants.*;
import static cn.wr.collect.sync.constants.PropertiesConstants.*;

@Deprecated
public class GoodsElasticSink implements ElasticsearchSinkFunction<MetricItem<ElasticO2O>> {
    private static final long serialVersionUID = 8915491246596370643L;
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsElasticSink.class);

    @Override
    public void process(MetricItem<ElasticO2O> metric, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
        if (null == metric) {
            LOGGER.info("GoodsElasticSink metric is null");
            return;
        }
        if (StringUtils.isBlank(metric.getOperate()) || StringUtils.isBlank(metric.getTableName()) || null == metric.getItem()) {
            LOGGER.info("GoodsElasticSink metric params is null, params:{}", metric);
            return;
        }
        switch (metric.getOperate()) {
            case CommonConstants.OPERATE_DELETE:
                delete(metric, requestIndexer);
                break;

            case CommonConstants.OPERATE_INSERT:
            case CommonConstants.OPERATE_UPDATE:
                insertOrUpdate(metric, requestIndexer);
                break;

            default:
                LOGGER.error("### GoodsElasticSink process unknown operate");
                break;
        }
    }

    private void delete(MetricItem<ElasticO2O> metric, RequestIndexer requestIndexer) {
        String skuCode = metric.getItem().getSkuCode();
        LOGGER.info("### GoodsElasticSink delete skuCode:{}, metric:{}", skuCode, metric);
        // 删除es
        requestIndexer.add(Requests.deleteRequest(ElasticEnum.O2O.getIndex())
                .type(ElasticEnum.O2O.getType())
                .id(skuCode));
    }

    private void insertOrUpdate(MetricItem<ElasticO2O> metric, RequestIndexer requestIndexer) {
        ParamsConcat param = paramsConcat(metric);
        // 写入es
        requestIndexer.add(Requests.indexRequest()
                .index(ElasticEnum.O2O.getIndex())
                .type(ElasticEnum.O2O.getType())
                .source(param.getContent(), XContentType.JSON)
                .id(param.getSkuCode()));
    }

    /**
     * 参数拼接
     * partner_goods: db_id
     * partner_store_goods: db_id、group_id
     */
    private ParamsConcat paramsConcat(MetricItem<ElasticO2O> item) {
        return new ParamsConcat(item.getItem().getSkuCode(),
                JSON.toJSONBytes(item.getItem(), SerializerFeature.WriteMapNullValue));
    }
}
