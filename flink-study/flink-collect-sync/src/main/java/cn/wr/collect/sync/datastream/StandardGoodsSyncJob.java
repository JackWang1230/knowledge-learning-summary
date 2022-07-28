package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.flatmap.StandardFlatMap;
import cn.wr.collect.sync.model.standard.StandardElastic;
import cn.wr.collect.sync.sink.StandardElasticSink;
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

public class StandardGoodsSyncJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(WelfareGoodsSyncJob.class);

    /**
     * main run
     */
    public static void main(String[] args) {
        LOGGER.info("StandardGoodsSyncJob start ......");
        try {
            // 获取参数配置
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                LOGGER.info("StandardGoodsSyncJob parameterTool is null");
                return;
            }

            // 1.设置执行环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 2.接收解析kafka
            DataStreamSource<String> dataSource = KafkaConfigUtil.buildStandardSource(env)
                    .setParallelism(parameterTool.getInt(STREAM_STANDARD_SOURCE_PARALLELISM, 5));

            // 3.数据清洗
            SingleOutputStreamOperator<StandardElastic> assembledData = dataSource
                    .flatMap(new StandardFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_STANDARD_FLATMAP_PARALLELISM, 5));

            // 4.写入es
            if (parameterTool.getBoolean(SINK_ELASTIC)) {
                // es sink
                assembledData
                        .keyBy(StandardElastic::getTradeCode)
                        .addSink(ESSinkUtil.getSink(assembledData,
                                new StandardElasticSink(),
                                new RetryRequestFailureHandler(),
                                parameterTool))
                        .setParallelism(parameterTool.getInt(STREAM_STANDARD_SINK_PARALLELISM, 5))
                        .name("sink-standard-goods");
            }
            else {
                assembledData.keyBy(StandardElastic::getTradeCode)
                        .print()
                        .setParallelism(parameterTool.getInt(STREAM_STANDARD_SINK_PARALLELISM, 5));
            }

            env.execute("[PRD][ES] - standard_goods(V2)");
        }
        catch (Exception e) {
            LOGGER.error("StandardGoodsSyncJob error:{}", e);
        }
    }
}
