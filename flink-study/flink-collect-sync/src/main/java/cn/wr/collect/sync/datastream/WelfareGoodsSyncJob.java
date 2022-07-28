package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.flatmap.WelfareFlatMap;
import cn.wr.collect.sync.model.welfare.WelfareElastic;
import cn.wr.collect.sync.sink.WelfareElasticSink;
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

public class WelfareGoodsSyncJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(WelfareGoodsSyncJob.class);

    /**
     * main run
     */
    public static void main(String[] args) {
        LOGGER.info("### WelfareGoodsSyncJob start ......");
        try {
            // 获取参数配置
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                LOGGER.info("### WelfareGoodsSyncJob parameterTool is null");
                return;
            }

            // 1.设置执行环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 2.接收解析kafka
            DataStreamSource<String> dataSource = KafkaConfigUtil.buildWelfareSource(env)
                    .setParallelism(parameterTool.getInt(STREAM_WELFARE_SOURCE_PARALLELISM, 5));

            // 3.数据清洗
            SingleOutputStreamOperator<WelfareElastic> assembledData = dataSource
                    .flatMap(new WelfareFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_WELFARE_FLATMAP_PARALLELISM, 5));

            // 4.写入es
            assembledData
                    .keyBy(WelfareElastic::getUniqueId)
                    .addSink(ESSinkUtil.getSink(assembledData,
                            new WelfareElasticSink(),
                            new RetryRequestFailureHandler(),
                            parameterTool))
                    .setParallelism(parameterTool.getInt(STREAM_WELFARE_SINK_PARALLELISM, 5))
                    .name("sink-es");

            env.execute("[PRD][ES] - welfare_goods");
        }
        catch (Exception e) {
            LOGGER.error("WelfareGoodsSyncJob error:{}", e);
        }
    }
}
