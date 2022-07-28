package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.dao.partner.PartnerGoodsDao;
import cn.wr.collect.sync.dao.partner.PartnerStoreGoodsDao;
import cn.wr.collect.sync.hbase.HBaseService;
import cn.wr.collect.sync.model.InitMap;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import cn.wr.collect.sync.utils.HBaseUtils;
import cn.wr.collect.sync.utils.QueryUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static cn.wr.collect.sync.constants.CommonConstants.OPERATE_UPDATE;
import static cn.wr.collect.sync.constants.PropertiesConstants.COMPLEMENT_DB_ID;
import static cn.wr.collect.sync.constants.PropertiesConstants.COMPLEMENT_END_TIME;
import static cn.wr.collect.sync.constants.PropertiesConstants.COMPLEMENT_START_TIME;


public class ComplementDataSource extends RichSourceFunction<BasicModel<Model>> {
    private static final long serialVersionUID = 4954574170753500071L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ComplementDataSource.class);
    private ParameterTool parameterTool;
    private LocalDateTime complementStartTime = null;
    private LocalDateTime complementEndTime = null;
    private Integer dbId = null;
    private HBaseService hBaseService;
    private boolean flag = true;

    @Override
    public void open(Configuration parameters) throws Exception {
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        super.open(parameters);
        hBaseService = new HBaseService(HBaseUtils.getConnection(parameterTool));

        String complementStartTimeStr = parameterTool.get(COMPLEMENT_START_TIME);
        String complementEndTimeStr = parameterTool.get(COMPLEMENT_END_TIME);
        String dbIdStr = parameterTool.get(COMPLEMENT_DB_ID);
        if (StringUtils.isNotBlank(complementStartTimeStr) && StringUtils.isNotBlank(complementEndTimeStr)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            complementStartTime = LocalDateTime.parse(complementStartTimeStr, formatter);
            complementEndTime = LocalDateTime.parse(complementEndTimeStr, formatter);
        }
        if (StringUtils.isNotBlank(dbIdStr)) {
            dbId = Integer.valueOf(dbIdStr);
        }
    }

    @Override
    public void cancel() {
        flag = false;
    }

    @Override
    public void run(SourceContext<BasicModel<Model>> context) {
        if ((null == complementStartTime || null == complementEndTime) && null == dbId) {
            LOGGER.info("ComplementDataSource params is null, return");
            return;
        }
        List<InitMap> list = new ArrayList<>();
        list.add(new InitMap(Table.BaseDataTable.partner_goods, new PartnerGoodsDao(parameterTool)));
        list.add(new InitMap(Table.BaseDataTable.partner_store_goods, new PartnerStoreGoodsDao(parameterTool)));
        list.forEach(item -> collect(item.getTable().name(), item.getQueryLimitDao(), context));
    }

    private void collect(String tableName, QueryLimitDao queryDao, SourceContext<BasicModel<Model>> context) {
        List<Model> models;
        long id = 0;
        int i = 0;
        while (flag) {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("id", id);
                params.put("dbId", dbId);
                params.put("complementStartTime", complementStartTime);
                params.put("complementEndTime", complementEndTime);
                models = QueryUtil.findLimitPlus(queryDao, i, params, null);
                if (CollectionUtils.isNotEmpty(models)) {
                    models.stream().forEach(item -> {
                        Model newModel = null;
                        if (StringUtils.equals(Table.BaseDataTable.partner_goods.name(), tableName)) {
                            newModel = hBaseService.queryPartnerGoods(((PartnerGoods) item).getDbId(), ((PartnerGoods) item).getInternalId());
                        }
                        else if (StringUtils.equals(Table.BaseDataTable.partner_store_goods.name(), tableName)) {
                            newModel = hBaseService.queryPartnerStoreGoods(((PartnerStoreGoods) item).getDbId(),
                                    ((PartnerStoreGoods) item).getGroupId(),
                                    ((PartnerStoreGoods) item).getGoodsInternalId());
                        }
                        if (Objects.nonNull(newModel)) {
                            context.collect(new BasicModel<>(tableName, OPERATE_UPDATE, newModel));
                        }
                    });
                    id = models.get(models.size() - 1).getId();
                }
            }
            catch (Exception e) {
                LOGGER.error("ComplementDataSource tableName:{}, page:{}, error:{}", tableName, i, e);
                models = Collections.emptyList();
            }
            if (CollectionUtils.isEmpty(models) || models.size() != QueryUtil.QUERY_PAGE_SIZE) {
                break;
            }
            i ++;
        }
    }
}
