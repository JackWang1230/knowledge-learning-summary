package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.datasource.DtpStoreInitSource;
import cn.wr.collect.sync.sink.DtpStoreInitSink;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DtpStoreInitJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicDataInitJob.class);

    public static void main(String[] args) {
        LOGGER.info("DtpStoreInitJob start......");
        try {
            // 初始化参数
            String proFilePath = ParameterTool.fromArgs(args).get("conf");

            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);

            if (null == parameterTool) {
                LOGGER.info("DtpStoreInitJob parameterTool is null");
                return;
            }

            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            env.addSource(new DtpStoreInitSource())
                    .setParallelism(1)

                    .addSink(new DtpStoreInitSink())
                    .setParallelism(1)
                    .name("sink-redis");

            env.execute("[PRD][REDIS] - dtp_store(init)");

        } catch (Exception e) {
            LOGGER.error("DtpStoreInitJob Exception:{}", e);
        }
    }
}
