package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.datasource.BasicPartnerStoresAllSource03;
import cn.wr.collect.sync.flatmap.GoodsSplitFlatMapV2;
import cn.wr.collect.sync.flatmap.Params2GoodsFlatMap;
import cn.wr.collect.sync.model.*;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.sink.GoodsElasticSinkV2;
import cn.wr.collect.sync.utils.ESSinkUtil;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import cn.wr.collect.sync.utils.KafkaConfigUtil;
import cn.wr.collect.sync.utils.RetryRequestFailureHandler;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class ElasticInitJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticInitJob.class);

    public static void main(String[] args) {
        LOGGER.info("ElasticInitJob start ......");
        try {
            // 初始化参数
            String proFilePath = ParameterTool.fromArgs(args).get(START_ARGS);

            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                LOGGER.info("ElasticInitJob parameterTool is null");
                return;
            }

            // 获取环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 查询数据
            DataStreamSource<BasicModel<PgConcatParams>> source = env.addSource(new BasicPartnerStoresAllSource03())
                    .setParallelism(parameterTool.getInt(STREAM_STORE2ES_INIT_SOURCE_PARALLELISM, 5));

            // 商品拆分
            SingleOutputStreamOperator<BasicModel<Model>> goodsData = source.flatMap(new Params2GoodsFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_STORE2ES_INIT_FLATMAP01_PARALLELISM, 5));

            // 拆分到各个门店/组装数据
            SingleOutputStreamOperator<BasicModel<ElasticO2O>> assemblyData = goodsData.filter((model) -> {
                        if (model.getData() instanceof BasicTimeStoreGoods) {
                            // (上架)
                            return CommonConstants.IS_OFF_SHELF.equals(((BasicTimeStoreGoods) model.getData()).getStatus());
                        }
                        else if (model.getData() instanceof BasicTimeGoods) {
                            // (上架 || 是DTP门店)
                            return CommonConstants.IS_OFF_SHELF.equals(((BasicTimeGoods) model.getData()).getStatus());
                        }
                        return false;
                    })
                    .setParallelism(parameterTool.getInt(STREAM_STORE2ES_INIT_FLATMAP02_PARALLELISM, 5))
                    .flatMap(new GoodsSplitFlatMapV2())
                    .setParallelism(parameterTool.getInt(STREAM_STORE2ES_INIT_FLATMAP02_PARALLELISM, 5));

            // es sink o2o
            if (parameterTool.getBoolean(SINK_ELASTIC)) {
                assemblyData.keyBy(item -> item.getData().getSkuCode())
                        .addSink(ESSinkUtil.getSink(assemblyData, new GoodsElasticSinkV2(),
                                new RetryRequestFailureHandler(), parameterTool))
                        .setParallelism(parameterTool.getInt(STREAM_STORE2ES_INIT_SINK01_PARALLELISM, 5))
                        .name("sink-elastic");
            }

            // 发送kafka
            if (parameterTool.getBoolean(SINK_KAFKA)) {
                assemblyData.keyBy(item -> item.getData().getRealTradeCode() + item.getData().getMerchantId()
                        + item.getData().getStoreId() + item.getData().getLocation())
                        .addSink(KafkaConfigUtil.buildGoodsChangeProducer(parameterTool))
                        .setParallelism(parameterTool.getInt(STREAM_STORE2ES_INIT_SINK02_PARALLELISM, 5))
                        .name("sink-kafka");
            }

            String jobName = "[PRD][ES] - goods_index_2.0_real_time(store_init  ["
                    + parameterTool.get(INIT_ES_START_ID) + ", "
                    + parameterTool.get(INIT_ES_END_ID) + ", "
                    + parameterTool.get(INIT_ES_DB_ID)
                    + "] "
                    + parameterTool.get(FLINK_COLLECT_VERSION)
                    + ")";
            env.execute(jobName);
        }
        catch (Exception e) {
            LOGGER.error("ElasticInitJob error:{}", e);
        }
    }
}
