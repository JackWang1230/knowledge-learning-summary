package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.dao.time.QueryBasicChangeDao;
import cn.wr.collect.sync.dao.time.QueryStoreGoodsDao;
import cn.wr.collect.sync.model.BasicTimeGoods;
import cn.wr.collect.sync.model.BasicTimeStoreGoods;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import cn.wr.collect.sync.utils.QueryUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.wr.collect.sync.constants.SqlConstants.SQL_PARTNER_GOODS;
import static cn.wr.collect.sync.constants.SqlConstants.SQL_STORE_GOODS;


public class Params2GoodsFlatMap extends RichFlatMapFunction<BasicModel<PgConcatParams>, BasicModel<Model>> {
    private static final long serialVersionUID = -830384231329345637L;
    private static final Logger log = LoggerFactory.getLogger(Params2GoodsFlatMap.class);
    private ParameterTool tool;

    // 控制循环标识位
    private boolean flag = true;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        // 控制循环标识位
        flag = true;
    }

    @Override
    public void close() throws Exception {
        super.close();
        log.info("Params2GoodsFlatMap close");
        flag = false;
    }

    @Override
    public void flatMap(BasicModel<PgConcatParams> model, Collector<BasicModel<Model>> collector) throws Exception {
        if (Objects.isNull(model) || Objects.isNull(model.getData())) {
            return;
        }
        if (StringUtils.isNotBlank(model.getData().getGroupId())) {
            this.collectStoreGoods(model.getTableName(), model.getOperate(), model.getData(), collector);
        }
        else {
            this.collectGoods(model.getTableName(), model.getOperate(), model.getData(), collector);
        }
    }

    private void collectGoods(String table, String operate, PgConcatParams data, Collector<BasicModel<Model>> collector) {
        QueryBasicChangeDao queryBasicChangeDao = new QueryBasicChangeDao(SQL_PARTNER_GOODS, data, tool);
        // 获取表对应的实体类
        int page = 0;
        Long id = 0L;
        List<PartnerGoods> models;
        Map<String, Object> params = new HashMap<>();
        while (flag) {
            params.put("id", id);
            // 从数据库查询数据,一次查1w条
            models = QueryUtil.findLimitPlus(queryBasicChangeDao, page, params, null);
            if (CollectionUtils.isNotEmpty(models)) {
                models.forEach(e -> {
                    collector.collect(new BasicModel<>(table, operate,
                            new BasicTimeGoods().transfer(e, data)));
                });
                id = models.get(models.size() - 1).getId();
            }

            if (CollectionUtils.isEmpty(models) || models.size() != QueryUtil.QUERY_PAGE_SIZE) {
                break;
            }

            page++;
        }
    }

    private void collectStoreGoods(String table, String operate, PgConcatParams data, Collector<BasicModel<Model>> collector) {
        QueryStoreGoodsDao queryDAO = new QueryStoreGoodsDao(SQL_STORE_GOODS, data, tool);
        // 获取表对应的实体类
        int page = 0;
        Long id = 0L;
        List<PartnerStoreGoods> models;
        Map<String, Object> params = new HashMap<>();
        while (flag) {
            params.put("id", id);
            // 从数据库查询数据,一次查1w条
            models = QueryUtil.findLimitPlus(queryDAO, page, params, null);
            if (CollectionUtils.isNotEmpty(models)) {
                models.forEach(e -> {
                    collector.collect(new BasicModel<>(table, operate,
                            new BasicTimeStoreGoods().transfer(e, data)));
                });
                id = models.get(models.size() - 1).getId();
            }

            if (CollectionUtils.isEmpty(models) || models.size() != QueryUtil.QUERY_PAGE_SIZE) {
                break;
            }

            page++;
        }
    }
}
