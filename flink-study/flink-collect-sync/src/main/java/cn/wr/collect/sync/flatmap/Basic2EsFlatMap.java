package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.function.Compute;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.service.OprtServiceImpl;
import cn.wr.collect.sync.service.SingleFieldService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

public class Basic2EsFlatMap extends RichFlatMapFunction<BasicModel<Model>, BasicModel<ElasticO2O>> {
    private static final long serialVersionUID = -6912016129913458284L;
    private static final Logger log = LoggerFactory.getLogger(Basic2EsFlatMap.class);
    private ParameterTool tool;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void flatMap(BasicModel<Model> model, Collector<BasicModel<ElasticO2O>> collector) {
        if (StringUtils.isBlank(model.getOperate()) || StringUtils.isBlank(model.getTableName())
                || Objects.isNull(model.getData())) {
            return;
        }

        // 获取变更情况
        int modCondition = Compute.computeCondition(model.getOperate(), model.getTableName(), model.getModFieldList());
        if (CommonConstants.MOD_FIELD_NONE == modCondition) {
            // log.info("Basic2EsFlatMap MOD_FIELD_NONE table:{}, id:{}", model.getTableName(), model.getData().getId());
            return;
        }

        try {
            /*单字段变更情况下不需要查询redis获取值，可直接操作，否则等待*/
            if (!(CommonConstants.MOD_FIELD_SINGLE == modCondition
                    || (CommonConstants.MOD_FIELD_KEY == modCondition
                    && Arrays.asList(SingleFieldService.TABLE).contains(model.getTableName())))) {
                // 实时写入redis存在延时，此处设置延时时间
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            log.error("Thread sleep InterruptedException: {}", e);
        }

        // 执行更新操作
        long start = System.currentTimeMillis();
        new OprtServiceImpl(tool).operate(model, collector, modCondition);
        long end = System.currentTimeMillis();
        log.info("Basic2EsFlatMap time:{}(ms) condition:{} model:{}", (end - start), modCondition, JSON.toJSONString(model));

    }
}
