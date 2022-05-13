package cn.wr.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.*;
import java.util.Map;
import java.util.Properties;

import static cn.wr.constants.PropertiesConstants.*;

/**
 * flink 环境基础类
 * @author RWang
 * @Date 2022/5/11
 */

public class ExecutionEnvUtil {

    /**
     * @param args args from external config file
     * @return ParameterTool
     * @throws Exception Exception
     */
    public static ParameterTool createParameterTool(final String[] args) throws Exception{
        return ParameterTool
                .fromPropertiesFile(ExecutionEnvUtil.class.getResourceAsStream(PROPERTIES_FILE_NAME))
                .mergeWith(ParameterTool.fromArgs(args));
                //.mergeWith(ParameterTool.fromSystemProperties());
    }

    /**
     *
     * @param proFilePath specific config path
     * @return ParameterTool
     * @throws Exception Exception
     */
    public static ParameterTool createParameterTool(final String proFilePath) throws Exception{
        if (StringUtils.isBlank(proFilePath)) {
            return createParameterTool();
        }
        // Prevent parsing Chinese garbled characters
        Properties props = new Properties();
        InputStream inputStream = new FileInputStream(proFilePath);
        BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
        props.load(bf);
        return ParameterTool.fromMap((Map) props);
//        ParameterTool parameterTool = ParameterTool.fromMap((Map) props);
//        return ParameterTool
//                .fromPropertiesFile(proFilePath)
//                .mergeWith(ParameterTool.fromSystemProperties());
    }

    /**
     * no args
     * @return ParameterTool
     */
    public static ParameterTool createParameterTool(){
        try {
            return ParameterTool
                    .fromPropertiesFile(ExecutionEnvUtil.class.getResourceAsStream(PROPERTIES_FILE_NAME))
                    .mergeWith(ParameterTool.fromSystemProperties());
        }catch (IOException e){
            e.printStackTrace();
        }
        return ParameterTool.fromSystemProperties();
    }

    /**
     *
     * @param parameterTool ParameterTool
     * @return StreamExecutionEnvironment
     * @throws Exception Exception
     */
    public static StreamExecutionEnvironment getEnv(ParameterTool parameterTool) throws  Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // config restart strategy
        env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(4,60000));
        if (parameterTool.getBoolean(STREAM_CHECKPOINT_ENABLE,true)){
            env.enableCheckpointing(parameterTool.getLong(STREAM_CHECKPOINT_INTERVAL,6000)
                    , CheckpointingMode.EXACTLY_ONCE);
        }
       EmbeddedRocksDBStateBackend embeddedRocksDBStateBackend = new EmbeddedRocksDBStateBackend();
       embeddedRocksDBStateBackend.isIncrementalCheckpointsEnabled();
       env.setStateBackend(embeddedRocksDBStateBackend);
       env.getCheckpointConfig().setCheckpointStorage(parameterTool.get(STREAM_CHECKPOINT_PATH));
       env.getConfig().setGlobalJobParameters(parameterTool);
       return env;
    }
}
