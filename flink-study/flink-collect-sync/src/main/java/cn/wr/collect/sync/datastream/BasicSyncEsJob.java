package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.filter.BasicGoodsSendKafkaFilter;
import cn.wr.collect.sync.flatmap.Basic2EsFlatMap;
import cn.wr.collect.sync.flatmap.BinlogList2SingleFlatMapV2;
import cn.wr.collect.sync.flatmap.EsFieldFullOrPartFlatMap;
import cn.wr.collect.sync.keyby.BinlogKeyBy;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.sink.Basic2EsSink02;
import cn.wr.collect.sync.sink.SyncProgressIncrSink;
import cn.wr.collect.sync.utils.ESSinkUtil;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import cn.wr.collect.sync.utils.KafkaConfigUtil;
import cn.wr.collect.sync.utils.PlatformRetryRequestFailureHandler;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class BasicSyncEsJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicSyncEsJob.class);

    /**
     * main run
     */
    public static void main(String[] args) {
        LOGGER.info("BasicSyncEsJob start ......");
        try {
            // 获取参数配置
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                LOGGER.info("BasicSyncEsJob parameterTool is null");
                return;
            }

            // 设置执行环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 接收解析binlog基础数据
            DataStreamSource<PolarDbBinlogBatch> source = KafkaConfigUtil.buildBasic2EsSource(env)
                    .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_SOURCE_PARALLELISM, 5));

            // 拆分binlog
            SingleOutputStreamOperator<BasicModel<Model>> split = source.flatMap(new BinlogList2SingleFlatMapV2())
                    .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_FLATMAP01_PARALLELISM, 5));

            // 分组/参数转换
            SingleOutputStreamOperator<BasicModel<ElasticO2O>> assembly = split.keyBy(new BinlogKeyBy())
                    .flatMap(new Basic2EsFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_FLATMAP02_PARALLELISM, 5));

            // sink kafka
            if (parameterTool.getBoolean(SINK_KAFKA)) {
                split.keyBy(new BinlogKeyBy())
                        .filter(new BasicGoodsSendKafkaFilter())
                        .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_FLATMAP03_PARALLELISM, 5))
                        .keyBy(new BinlogKeyBy())
                        .addSink(KafkaConfigUtil.buildBasicGoodsChangeProducer(parameterTool))
                        .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_SINK02_PARALLELISM, 5))
                        .name("sink-kafka");
            }

            // sink es
            assembly.keyBy(basic -> basic.getData().getSkuCode())
                    .addSink(ESSinkUtil.getSink(assembly, new Basic2EsSink02(), new PlatformRetryRequestFailureHandler(), parameterTool))
                    .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_SINK01_PARALLELISM, 5))
                    .name("sink-es");


            SingleOutputStreamOperator<BasicModel<ElasticO2O>> assembly1 = assembly.flatMap(new EsFieldFullOrPartFlatMap());
//
           // 单独转发kafka给连锁维度索引
            assembly1.keyBy(basic -> basic.getData().getSkuCode())
                    .addSink(KafkaConfigUtil.buildGoodsChangeProducerPartner(parameterTool))
                    .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_SINK02_PARALLELISM, 5))
                    .name("sink-kafka-simple");

            // 同步进度更新redis
            if (parameterTool.getBoolean(SINK_CACHE_STATE_PROGRESS)) {
                assembly.addSink(new SyncProgressIncrSink())
                        .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_SINK03_PARALLELISM, 5))
                        .name("sink-progress-incr");
            }


            env.execute("[PRD][ES] - goods_index_2.0_real_time(basic_rt " + parameterTool.get(FLINK_COLLECT_VERSION) + ")");
        } catch (Exception e) {
            LOGGER.error("BasicSyncEsJob error:{}", e);
        }
    }
}
