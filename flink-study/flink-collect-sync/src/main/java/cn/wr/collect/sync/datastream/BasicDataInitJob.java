package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.datasource.BasicInitSource;
import cn.wr.collect.sync.sink.BasicInitSink;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.collect.sync.constants.PropertiesConstants.STREAM_INIT2CACHE_SINK_PARALLELISM;
import static cn.wr.collect.sync.constants.PropertiesConstants.STREAM_INIT2CACHE_SOURCE_PARALLELISM;


public class BasicDataInitJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicDataInitJob.class);

    public static void main(String[] args) {
        LOGGER.info("BasicDataInitJob start......");
        try {
            // 初始化参数
            String proFilePath = ParameterTool.fromArgs(args).get("conf");

            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);

            if (null == parameterTool) {
                LOGGER.info("BasicDataInitJob parameterTool is null");
                return;
            }

            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            env.addSource(new BasicInitSource())
                    .setParallelism(parameterTool.getInt(STREAM_INIT2CACHE_SOURCE_PARALLELISM, 1))

                    .addSink(new BasicInitSink())
                    .setParallelism(parameterTool.getInt(STREAM_INIT2CACHE_SINK_PARALLELISM, 5))
                    .name("sink-basic");

            env.execute("[PRD][HBASE/REDIS] - basic_goods_2_cache(init)");

        } catch (Exception e) {
            LOGGER.error("BasicDataInitJob Exception:{}", e);
        }
    }
}
