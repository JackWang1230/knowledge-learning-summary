package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.ElasticEnum;
import cn.wr.collect.sync.model.gc.PlatformGoods;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Deprecated
public class PlatformGoodsSink implements ElasticsearchSinkFunction<PlatformGoods> {
    private static final long serialVersionUID = 8015525385072818599L;
    private static final Logger log = LoggerFactory.getLogger(PlatformGoodsSink.class);
    private static final String SKU_CODE = "%s-%s-%s-%s";
    private static final String IS_wr_OFF_SHELF = "is_wr_off_shelf";
    private static final String SYNC_DATE = "sync_date";

    @Override
    public void process(PlatformGoods goods, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
        if (null == goods) {
            return;
        }
        log.info(JSON.toJSONString(goods));
        UpdateRequest updateRequest = new UpdateRequest();
        try {
            String skuCode = String.format(SKU_CODE, goods.getMerchantId(), goods.getStoreId(),
                    goods.getChannel(), goods.getGoodsInternalId());
            boolean status = goods.getStatus() == 1;
            updateRequest.index(ElasticEnum.O2O.getIndex())
                    .type(ElasticEnum.O2O.getType())
                    .id(skuCode)
                    .doc(XContentFactory.jsonBuilder().startObject()
                            .field(IS_wr_OFF_SHELF, status)
                            .endObject());
            requestIndexer.add(updateRequest);
        } catch (IOException e) {
            log.error("### PlatformGoodsSink IOException :{}", e);
        }


    }
}
