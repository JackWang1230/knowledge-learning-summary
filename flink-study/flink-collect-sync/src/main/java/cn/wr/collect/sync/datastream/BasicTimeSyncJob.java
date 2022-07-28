package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.datasource.BasicTimeSyncSource;
import cn.wr.collect.sync.flatmap.GoodsSplitFlatMapV2;
import cn.wr.collect.sync.flatmap.Params2GoodsFlatMap;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.sink.GoodsElasticSinkV2;
import cn.wr.collect.sync.sink.GoodsMbsSinkV2;
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


public class BasicTimeSyncJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicTimeSyncJob.class);

    /**
     * main run
     */
    public static void main(String[] args) {
        LOGGER.info("BasicTimeSyncJob start ......");
        try {
            // 获取参数配置
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                LOGGER.info("BasicTimeSyncJob parameterTool is null");
                return;
            }

            // 设置执行环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 查询redis
            DataStreamSource<BasicModel<PgConcatParams>> timeSource = env.addSource(new BasicTimeSyncSource())
                    .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_PT_SOURCE_PARALLELISM, 5));

            // 拆分数据
            SingleOutputStreamOperator<BasicModel<Model>> goodsData = timeSource.flatMap(new Params2GoodsFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_PT_FLATMAP01_PARALLELISM, 5));

            // 拆分/组装数据
            SingleOutputStreamOperator<BasicModel<ElasticO2O>> assemblyData = goodsData.flatMap(new GoodsSplitFlatMapV2())
                    .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_PT_FLATMAP02_PARALLELISM, 5));

            // sink es
            if (parameterTool.getBoolean(SINK_ELASTIC)) {
                // es sink o2o
                assemblyData.keyBy(item -> item.getData().getSkuCode())
                        .addSink(ESSinkUtil.getSink(assemblyData, new GoodsElasticSinkV2(),
                                new RetryRequestFailureHandler(), parameterTool))
                        .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_PT_SINK01_PARALLELISM, 5))
                        .name("sink-elastic");
            }
            else {
                assemblyData.print().setParallelism(1);
            }

            // sink mbs
            if (parameterTool.getBoolean(SINK_MBS)) {
                assemblyData.addSink(new GoodsMbsSinkV2())
                        .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_PT_SINK02_PARALLELISM, 5))
                        .name("sink-mbs");
            }

            // 单独转发kafka给连锁维度索引
            assemblyData.keyBy(basic -> basic.getData().getSkuCode())
                    .addSink(KafkaConfigUtil.buildGoodsChangeProducerPartner(parameterTool))
                    .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_SINK02_PARALLELISM, 5))
                    .name("sink-kafka-simple");

            // sink kafka
            if (parameterTool.getBoolean(SINK_KAFKA)) {
                assemblyData.keyBy(item -> item.getData().getRealTradeCode() + item.getData().getMerchantId()
                            + item.getData().getStoreId() + item.getData().getLocation())
                        .addSink(KafkaConfigUtil.buildGoodsChangeProducer(parameterTool))
                        .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_PT_SINK03_PARALLELISM, 5))
                        .name("sink-kafka");
            }

            env.execute("[PRD][ES] - goods_index_2.0_real_time(basic_pt "
                    + parameterTool.get(FLINK_COLLECT_VERSION) + ")");
        }
        catch (Exception e) {
            LOGGER.error("BasicTimeSyncJob error:{}", e);
        }
    }
}
