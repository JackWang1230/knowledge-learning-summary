package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.dao.gc.StandardGoodsSyncrdsDao;
import cn.wr.collect.sync.model.MetricEvent;
import cn.wr.collect.sync.model.MetricItem;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.utils.QueryUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.CommonConstants.OPERATE_UPDATE;

public class SearchKeywordsInitSource extends RichSourceFunction<MetricEvent> {
    private static final long serialVersionUID = 3691218939400575579L;
    private static final Logger LOG = LoggerFactory.getLogger(GoodsCenterInitSource.class);
    private StandardGoodsSyncrdsDao standardGoodsSyncrdsDao;


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        standardGoodsSyncrdsDao = new StandardGoodsSyncrdsDao(parameterTool);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void cancel() {

    }

    @Override
    public void run(SourceContext<MetricEvent> context) throws Exception {
        this.collect(Table.BaseDataTable.gc_standard_goods_syncrds.name(), standardGoodsSyncrdsDao, context);
    }

    private void collect(String tableName, QueryLimitDao queryDao, SourceContext<MetricEvent> context) {
        List<Model> models;
        long id = 0;
        int i = 0;
        do {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("id", id);
                models = QueryUtil.findLimitPlus(queryDao, i, params, null);
                if (CollectionUtils.isNotEmpty(models)) {
                    List<MetricItem<Model>> collect = models.stream()
                            .map(m -> new MetricItem<>(tableName, OPERATE_UPDATE, m))
                            .collect(Collectors.toList());
                    context.collect(new MetricEvent<>(collect));
                    id = models.get(models.size() - 1).getId();
                }
            }
            catch (Exception e) {
                LOG.error("### SearchKeywordsInitSource tableName:{}, page:{}, error:{}", tableName, i, e);
                models = Collections.emptyList();
            }
            i ++;
        } while (models.size() == QueryUtil.QUERY_PAGE_SIZE * QueryUtil.its.length);
    }
}
