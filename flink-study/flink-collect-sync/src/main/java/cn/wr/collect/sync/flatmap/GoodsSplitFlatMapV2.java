package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.constants.PropertiesConstants;
import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.function.Compute;
import cn.wr.collect.sync.hbase.HBaseService;
import cn.wr.collect.sync.model.BasicTimeGoods;
import cn.wr.collect.sync.model.BasicTimeStoreGoods;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import cn.wr.collect.sync.service.MultiFieldService;
import cn.wr.collect.sync.utils.HBaseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.CommonConstants.*;


public class GoodsSplitFlatMapV2 extends RichFlatMapFunction<BasicModel<Model>, BasicModel<ElasticO2O>> {
    private static final long serialVersionUID = -8963452641085490877L;
    private static final Logger log = LoggerFactory.getLogger(GoodsSplitFlatMapV2.class);
    private HBaseService hBaseService;
    private ParameterTool tool;
    private List<Integer> filterDbIdList;

    @Override
    public void open(Configuration parameters) throws Exception {
        log.info("GoodsSplitFlatMap2 open");
        super.open(parameters);
        tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        this.hBaseService = new HBaseService(HBaseUtils.getConnection(tool));
        String filterDbIdStr = tool.get(PropertiesConstants.FILTER_DBID);
        if (StringUtils.isNotBlank(filterDbIdStr)) {
            filterDbIdList = Arrays.stream(filterDbIdStr.split(SymbolConstants.COMMA_EN))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        }
        else {
            filterDbIdList = Collections.emptyList();
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
        // 关闭hbase连接
        if (null != hBaseService) {
            hBaseService.closeConnection();
        }
        // DingTalkUtil.operateJob(tool.get(), getRuntimeContext().getTaskName(), tool.ge);
        log.info("GoodsSplitFlatMapV2 close");
    }

    @Override
    public void flatMap(BasicModel<Model> model, Collector<BasicModel<ElasticO2O>> collector) {
        if (Objects.isNull(model)
                || StringUtils.isBlank(model.getTableName())
                || Objects.isNull(model.getData())) {
            return;
        }

        if (Compute.isFilterDbId(model.getData(), filterDbIdList)) {
            return;
        }

        MultiFieldService multiFieldService = new MultiFieldService(tool);
        switch (model.getTableName()) {
            case BASIC_TIME_GOODS:
                multiFieldService.splitByBasicTimeGoods(model.getTableName(), model.getOperate(),
                        (BasicTimeGoods) model.getData(), model.getModFieldList(), collector);
                break;

            case BASIC_TIME_STORE_GOODS:
                multiFieldService.splitByBasicTimeStoreGoods(model.getTableName(), model.getOperate(),
                        model.getModFieldList(), (BasicTimeStoreGoods) model.getData(), collector);
                break;

            case PARTNER_GOODS:
                multiFieldService.splitByPartnerGoods(model.getTableName(), model.getOperate(),
                        model.getModFieldList(), (PartnerGoods) model.getData(), collector);
                break;

            case PARTNER_STORE_GOODS:
                multiFieldService.splitByPartnerStoreGoods(model.getTableName(), model.getOperate(),
                        model.getModFieldList(), (PartnerStoreGoods) model.getData(), collector);
                break;

            default:
                log.error("GoodsSplitFlatMapV2 table unknown, {}", model.getTableName());
                break;
        }
    }



}
