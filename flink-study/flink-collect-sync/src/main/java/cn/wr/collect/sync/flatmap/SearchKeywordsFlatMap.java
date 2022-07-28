package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.PropertiesConstants;
import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.model.ElasticSearchKeywords;
import cn.wr.collect.sync.model.MetricEvent;
import cn.wr.collect.sync.model.MetricItem;
import cn.wr.collect.sync.model.gc.StandardGoodsSyncrds;
import cn.wr.collect.sync.utils.ESSinkUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SearchKeywordsFlatMap extends RichFlatMapFunction<MetricEvent, ElasticSearchKeywords> {
    private static final long serialVersionUID = 4784643384703230294L;
    private static final Logger LOG = LoggerFactory.getLogger(SearchKeywordsFlatMap.class);
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        // RestHighLevelClient 客户端加载
        final ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        URL url = new URL(parameterTool.get(PropertiesConstants.ELASTICSEARCH_HOSTS));
        RestClientBuilder builder = RestClient.builder(new HttpHost(url.getHost(), url.getPort()));
        if (parameterTool.getBoolean(ESSinkUtil.ES_SECURITY_ENABLE)) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(parameterTool.getRequired(ESSinkUtil.ES_SECURITY_USERNAME),
                            parameterTool.getRequired(ESSinkUtil.ES_SECURITY_PASSWORD)));
            builder.setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider));
        }
        restHighLevelClient = new RestHighLevelClient(builder);
        LOG.info("SearchKeywordsFlatMap RestHighLevelClient open!");
    }

    @Override
    public void close() throws Exception {
        super.close();
        restHighLevelClient.close();
        LOG.info("SearchKeywordsFlatMap RestHighLevelClient close!");
    }

    @Override
    public void flatMap(MetricEvent event, Collector<ElasticSearchKeywords> collector)
            throws Exception {
        if (Objects.isNull(event) || CollectionUtils.isEmpty(event.getFields())) {
            return;
        }
        event.getFields().stream()
                .filter(item -> this.checkNotNull((MetricItem<StandardGoodsSyncrds>) item))
                .forEach(item -> this.assembly(((MetricItem<StandardGoodsSyncrds>) item).getItem().getSearchKeywords(), collector));
    }

    /**
     * 拆分组装数据
     *
     * @param searchKeywords
     * @param collector
     */
    private void assembly(String searchKeywords, Collector<ElasticSearchKeywords> collector) {
        List<String> list = Arrays.asList(searchKeywords.split(SymbolConstants.PAUSE));
        list.stream().filter(StringUtils::isNotBlank)
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(item -> new ElasticSearchKeywords(DigestUtils.md5Hex(item), item, 0d, LocalDateTime.now())) // weight 默认0 非热词
                .filter(item -> this.checkNotExist(item))
                .forEach(item -> collector.collect(item));
    }

    /**
     * 过滤空值
     *
     * @param item
     * @return
     */
    private boolean checkNotNull(MetricItem<StandardGoodsSyncrds> item) {
        return Objects.nonNull(item)
                && (StringUtils.equals(CommonConstants.OPERATE_INSERT, item.getOperate())
                || StringUtils.equals(CommonConstants.OPERATE_UPDATE, item.getOperate()))
                && Objects.nonNull(item.getItem())
                && StringUtils.isNotBlank(item.getItem().getSearchKeywords());
    }

    /**
     * 校验es是否已经存在id
     *
     * @param keywords
     * @return
     */
    private boolean checkNotExist(ElasticSearchKeywords keywords) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("id", keywords.getId())).size(0);
        SearchRequest rq = new SearchRequest();
        // 索引
        rq.indices(PropertiesConstants.ES_INDEX_WORD);
        rq.types(PropertiesConstants.ES_DOCUMENT_TYPE_WORD);
        rq.source(sourceBuilder);
        try {
            SearchResponse search = restHighLevelClient.search(rq);
            long totalHits = search.getHits().getTotalHits();
            LOG.info("SearchKeywordsFlatMap json:{} totalHits:{}", JSON.toJSONString(keywords), totalHits);
            return totalHits == 0;
        } catch (IOException e) {
            LOG.error("SearchKeywordsFlatMap IOException:{}", e);
        }
        return false;
    }
}
