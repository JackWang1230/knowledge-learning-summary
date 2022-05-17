package cn.wr.sink;

import cn.wr.model.BaseJudge;
import cn.wr.model.GcConfigJudge;
import cn.wr.model.GcDisableJudge;
import cn.wr.utils.EsUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;

import static cn.wr.constants.PropertiesConstants.*;

/**
 * @author RWang
 * @Date 2022/5/16
 */
@Slf4j
public class UpdateSearchEsSink implements ElasticsearchSinkFunction<BaseJudge> {


    private RestHighLevelClient restHighLevelClient;
    private final ParameterTool parameterTool;

    private static final String CONTROL_STATUS="control_status";

    public UpdateSearchEsSink(ParameterTool parameterTool){
        this.parameterTool=parameterTool;
    }
    @Override
    public void open() throws Exception {
        restHighLevelClient = EsUtil.initClient(parameterTool);
    }

    @Override
    public void close() throws Exception {
        EsUtil.closeClient(restHighLevelClient);
    }

    private static final long serialVersionUID = 3130681864448987077L;
    @SneakyThrows
    @Override
    public void process(BaseJudge baseJudge, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {


        // 基于表名识别实体转换
        String tableName = baseJudge.getTableName();
        switch (tableName){
            case GC_CONFIG_SKU:

                // 去es 基于连锁id 和 内码id 查询出所有的
                GcConfigJudge gcConfigJudge = (GcConfigJudge)baseJudge;
                String operate = gcConfigJudge.getOperate();
                int newControlStatus = gcConfigJudge.getNewControlStatus();
                int oldControlStatus = gcConfigJudge.getOldControlStatus();
                ArrayList<String> ids = new ArrayList<>();

                int from=0;
                if (oldControlStatus == 0 && newControlStatus == 2 && operate.equals(UPDATE)){
                    getIdsBasedPageSize(ids,from,gcConfigJudge.getMerchantId(),gcConfigJudge.getInternalId());
                    // 状态 从 0->2 相当于全国门店都禁用
                    updateSearchControlStatus(ids,true,requestIndexer);

                }
                if (oldControlStatus == 2 && newControlStatus == 0 && operate.equals(UPDATE)){
                    getIdsBasedPageSize(ids,from,gcConfigJudge.getMerchantId(),gcConfigJudge.getInternalId());
                    // 状态 从 0->2 相当于全国门店都开启
                    updateSearchControlStatus(ids,false,requestIndexer);

                }
                if (oldControlStatus == 2 && newControlStatus == 2 && operate.equals(INSERT)){
                    getIdsBasedPageSize(ids,from,gcConfigJudge.getMerchantId(),gcConfigJudge.getInternalId());
                    // 状态 从 2->2 相当于全国门店都禁用
                    updateSearchControlStatus(ids,true,requestIndexer);
                }

                break;
            case GC_DISABLE_STORE:
                GcDisableJudge gcDisableJudge=(GcDisableJudge)baseJudge;
                String operate1 = gcDisableJudge.getOperate();
                String id = gcDisableJudge.getMerchantId() +
                        "-" +
                        gcDisableJudge.getStoreId() +
                        "-2-" +
                        gcDisableJudge.getInternalId();
                // 操作是delete 相当于门店内的药品不再禁用
                // control_status 改为false
                if (operate1.equals(DELETE)){
                    updateSearchControlStatus(id,false,requestIndexer);
                }
                // 操作是insert 相当于店内的药品被禁用
                // control_status 改为 true
                if (operate1.equals(INSERT)){
                    updateSearchControlStatus(id,true,requestIndexer);
                }
                break;
        }
    }


    /**
     * 更新 goods_index_2.0_real_time_v10索引 管控状态
     * @param ids
     * @param value
     * @param requestIndexer
     * @throws IOException
     */
    public void updateSearchControlStatus(ArrayList<String> ids,Boolean value,RequestIndexer requestIndexer) throws IOException {

        for (String id : ids) {
            UpdateRequest updateRequest=new UpdateRequest();
            updateRequest.index(parameterTool.get(ELASTICSEARCH_SEARCH_INDEX_WORD))
                    .type(ELASTICSEARCH_SEARCH_DOCUMENT_TYPE_WORD)
                    .id(id)
                    .doc(XContentFactory.jsonBuilder().startObject().field(CONTROL_STATUS,value).endObject());
            requestIndexer.add(updateRequest);
        }
    }

    /**
     * 更新 goods_index_2.0_real_time_v10索引 管控状态
     * @param id
     * @param value
     * @param requestIndexer
     * @throws IOException
     */
    public void updateSearchControlStatus(String id,Boolean value,RequestIndexer requestIndexer) throws IOException {

            UpdateRequest updateRequest=new UpdateRequest();
            updateRequest.index(parameterTool.get(ELASTICSEARCH_SEARCH_INDEX_WORD))
                    .type(ELASTICSEARCH_SEARCH_DOCUMENT_TYPE_WORD)
                    .id(id)
                    .doc(XContentFactory.jsonBuilder().startObject().field(CONTROL_STATUS,value).endObject());
            requestIndexer.add(updateRequest);

    }


    /**
     * 基于分页条件查询下,获取所有的ids
     * @param ids
     * @param from
     * @param merchantId
     * @param internalId
     * @throws IOException
     */
    public void getIdsBasedPageSize(ArrayList<String> ids,int from,long merchantId,String internalId) throws IOException {

        long totalHits = getTotalHits(from, merchantId, internalId);
        if (totalHits>100){
            // 分页查询
            int size =(int)totalHits/100;
            for (from = 0;  from< size; from++) {
                getIds(ids,from,merchantId,internalId);
            }
        }else {
            getIds(ids,from,merchantId,internalId);
        }
    }


    /**
     * 基于条件查询
     * @param from
     * @param merchantId
     * @param goodsInternalId
     * @return
     * @throws IOException
     */
    public SearchResponse searchCondition(int from,long merchantId,String goodsInternalId) throws IOException {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(100);
        searchSourceBuilder.from(from);
//        searchSourceBuilder.sort()
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 744 "1018127"
        boolQueryBuilder.must(QueryBuilders.termQuery("merchant_id",merchantId))
                .must(QueryBuilders.termQuery("goods_internal_id",goodsInternalId));
        SearchSourceBuilder query = searchSourceBuilder.query(boolQueryBuilder);
        SearchRequest source = new SearchRequest(parameterTool.get(ELASTICSEARCH_SEARCH_INDEX_WORD)).source(query);
        if (restHighLevelClient == null){
            restHighLevelClient = EsUtil.initClient(parameterTool);
        }
        return restHighLevelClient.search(source, RequestOptions.DEFAULT);
    }

    /**
     * 获取总条数
     * @param from
     * @param merchantId
     * @param goodsInternalId
     * @return
     * @throws IOException
     */
    public long getTotalHits(int from,long merchantId,String goodsInternalId) throws IOException {

        SearchResponse searchResponse = searchCondition(from, merchantId, goodsInternalId);
        return searchResponse.getHits().getTotalHits();
    }


    /**
     * 获取每条数据的id
     * @param ids
     * @param from
     * @param merchantId
     * @param goodsInternalId
     * @throws IOException
     */
    public void getIds(ArrayList<String> ids,int from,long merchantId,String goodsInternalId) throws IOException {

        SearchResponse searchResponse = searchCondition(from, merchantId, goodsInternalId);
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            String str = hit.toString();
            JSONObject jsonObject = JSONObject.parseObject(str);
            String id = jsonObject.getString("_id");
            System.out.println(id);
            ids.add(id);
        }
    }

}
