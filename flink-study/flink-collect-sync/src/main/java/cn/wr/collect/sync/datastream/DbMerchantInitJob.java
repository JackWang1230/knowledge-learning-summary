package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.datasource.DbMerchantInitSource;
import cn.wr.collect.sync.sink.DbMerchantSink;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DbMerchantInitJob {
    private static final Logger log = LoggerFactory.getLogger(BasicDataInitJob.class);

    public static void main(String[] args) {
        log.info("DbMerchantInitJob start......");
        try {
            // 初始化参数
            String proFilePath = ParameterTool.fromArgs(args).get("conf");

            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);

            if (null == parameterTool) {
                log.info("DtpStoreInitJob parameterTool is null");
                return;
            }

            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            env.addSource(new DbMerchantInitSource())
                    .setParallelism(1)

                    .addSink(new DbMerchantSink())
                    .setParallelism(1)
                    .name("sink-redis");

            env.execute("[PRD][REDIS] - db_merchant(init)");

        } catch (Exception e) {
            log.error("DbMerchantInitJob msg:{} Exception:{}", e.getMessage(), e);
        }
    }
}
