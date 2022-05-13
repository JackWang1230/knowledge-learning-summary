package cn.wr.datastream;


import cn.wr.utils.ExecutionEnvUtil;
import cn.wr.utils.KafkaUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.constants.PropertiesConstants.FLINK_JOB_NAME;
import static cn.wr.constants.PropertiesConstants.STREAM_SOURCE_PARALLELISM;


/**
 * @author RWang
 * @Date 2022/5/12
 */

public class SearchStatusJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchStatusJob.class);

    public static void main(String[] args) {
        LOGGER.info("SearchStatusJob start ....");
        try {
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(args);
            if (null == parameterTool) {
                LOGGER.info("SearchStatusJob parameterTool is null");
                return;
            }
            StreamExecutionEnvironment env = ExecutionEnvUtil.getEnv(parameterTool);
            DataStreamSource<String> canalSource = KafkaUtil.createCanalSource(env)
                    .setParallelism(parameterTool.getInt(STREAM_SOURCE_PARALLELISM));

            env.execute(FLINK_JOB_NAME);


        } catch (Exception e){
            e.printStackTrace();
            LOGGER.error("SearchStatusJob error:{}",e);
        }

    }
}
