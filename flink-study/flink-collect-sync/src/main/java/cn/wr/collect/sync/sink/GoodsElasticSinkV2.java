package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.ElasticEnum;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.ParamsConcat;
import cn.wr.collect.sync.model.basic.BasicModel;
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

import java.util.Objects;

public class GoodsElasticSinkV2 implements ElasticsearchSinkFunction<BasicModel<ElasticO2O>> {
    private static final long serialVersionUID = 8915491246596370643L;
    private static final Logger log = LoggerFactory.getLogger(GoodsElasticSinkV2.class);

    @Override
    public void process(BasicModel<ElasticO2O> model, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
        if (null == model) {
            log.info("GoodsElasticSinkV2 model is null");
            return;
        }
        if (StringUtils.isBlank(model.getOperate()) || StringUtils.isBlank(model.getTableName())
                || Objects.isNull(model.getData()) || StringUtils.isBlank(model.getData().getSkuCode())) {
            log.info("GoodsElasticSinkV2 model params is null, params:{}", model);
            return;
        }
        switch (model.getOperate()) {
            case CommonConstants.OPERATE_DELETE:
            case CommonConstants.OPERATE_UPDATE_DELETE:
                this.delete(model, requestIndexer);
                break;

            case CommonConstants.OPERATE_INSERT:
            case CommonConstants.OPERATE_UPDATE:
            case CommonConstants.OPERATE_UPDATE_INSERT:
                // (下架) && 非DTP商品
                if (((Objects.isNull(model.getData().getIsOffShelf()) || model.getData().getIsOffShelf()))
                        && !CommonConstants.IS_DTP_TRUE.equals(model.getData().getIsDtp())) {
                    this.delete(model, requestIndexer);
                } else {
                    this.insertOrUpdate(model, requestIndexer);
                }
                break;

            default:
                log.error("GoodsElasticSinkV2 process unknown operate");
                break;
        }
    }

    /**
     * delete es
     * @param model
     * @param requestIndexer
     */
    private void delete(BasicModel<ElasticO2O> model, RequestIndexer requestIndexer) {
        log.info("GoodsElasticSinkV2 delete table:{}, operate:{}, skuCode:{}, isOffShelf:{}, isDtp:{}, isDtpStore:{}",
                model.getTableName(), model.getOperate(), model.getData().getSkuCode(),
                model.getData().getIsOffShelf(), model.getData().getIsDtp(), model.getData().getIsDtpStore());
        String skuCode = model.getData().getSkuCode();
        // 删除es
        requestIndexer.add(Requests.deleteRequest(ElasticEnum.O2O.getIndex())
                .type(ElasticEnum.O2O.getType())
                .id(skuCode));
    }

    /**
     * upsert es
     * @param model
     * @param requestIndexer
     */
    private void insertOrUpdate(BasicModel<ElasticO2O> model, RequestIndexer requestIndexer) {
        ParamsConcat param = new ParamsConcat(model.getData().getSkuCode(),
                JSON.toJSONBytes(model.getData(), SerializerFeature.WriteMapNullValue));
        // 写入es
        requestIndexer.add(Requests.indexRequest()
                .index(ElasticEnum.O2O.getIndex())
                .type(ElasticEnum.O2O.getType())
                .source(param.getContent(), XContentType.JSON)
                .id(param.getSkuCode()));
    }
}
