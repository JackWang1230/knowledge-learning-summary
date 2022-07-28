package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.sink.MatchCodeSink;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import cn.wr.collect.sync.utils.KafkaConfigUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.collect.sync.constants.PropertiesConstants.STREAM_MATCHCODE_SINK_PARALLELISM;
import static cn.wr.collect.sync.constants.PropertiesConstants.STREAM_MATCHCODE_SOURCE_PARALLELISM;


@Deprecated
public class MatchCodeJob {
    private static final Logger log = LoggerFactory.getLogger(MatchCodeJob.class);

    public static void main(String[] args) {
        log.info("MatchCodeJob start ......");
        try {
            // 初始化参数
            String proFilePath = ParameterTool.fromArgs(args).get("conf");

            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                log.info("MatchCodeJob parameterTool is null");
                return;
            }

            // 获取环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 接收kafka数据
            KafkaConfigUtil.buildMatchCodeSource(env)
                    .setParallelism(parameterTool.getInt(STREAM_MATCHCODE_SOURCE_PARALLELISM, 5))

                    .addSink(new MatchCodeSink())
                    .name("sink-chain")
                    .setParallelism(parameterTool.getInt(STREAM_MATCHCODE_SINK_PARALLELISM, 5));


            env.execute("[PRD][MYSQL] - match_code");
        }
        catch (Exception e) {
            log.error("MatchCodeJob error:{}", e);
        }
    }
}
