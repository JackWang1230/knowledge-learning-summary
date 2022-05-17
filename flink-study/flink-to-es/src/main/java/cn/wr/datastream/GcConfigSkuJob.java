package cn.wr.datastream;


import cn.wr.filter.GcConfigSkuFilter;
import cn.wr.flatmap.CanalTransModelFlatMap;
import cn.wr.flatmap.GcConfigSkuFlatMap;
import cn.wr.map.CanalTransModelMap;
import cn.wr.model.CanalDataModel;
import cn.wr.model.GcConfigSkuStar;
import cn.wr.sink.GcConfigSkuStarSink;
import cn.wr.sink.GoodsSkuStarSink;
import cn.wr.utils.EsSinkUtil;
import cn.wr.utils.ExecutionEnvUtil;
import cn.wr.utils.KafkaUtil;
import cn.wr.utils.RetryRequestFailureHandler;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.constants.PropertiesConstants.FLINK_JOB_NAME;
import static cn.wr.constants.PropertiesConstants.STREAM_SOURCE_PARALLELISM;


/**
 * gc_config_sku 监听binlog 主启动类
 * @author RWang
 * @Date 2022/5/11
 */

public class GcConfigSkuJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcConfigSkuJob.class);

    public static void main(String[] args) {
        LOGGER.info("GcConfigSkuJob start ....");
        try {
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(args);
            if (null == parameterTool) {
                LOGGER.info("GcConfigSkuJob parameterTool is null");
                return;
            }
            StreamExecutionEnvironment env = ExecutionEnvUtil.getEnv(parameterTool);
            env.setParallelism(1);
            DataStreamSource<String> canalSource = KafkaUtil.createCanalSource(env)
                    .setParallelism(parameterTool.getInt(STREAM_SOURCE_PARALLELISM));

            // 过滤无用数据 提取gc_config_sku需要的数据 并数据转换
            DataStream<GcConfigSkuStar> gcConfigSKuStar = canalSource
                    .filter(new GcConfigSkuFilter())
                    .map(new CanalTransModelMap())
                    .flatMap(new GcConfigSkuFlatMap());


            // 写入es
            gcConfigSKuStar
                    .keyBy(GcConfigSkuStar::getSkuNo)
                    .addSink(EsSinkUtil.getSink(gcConfigSKuStar,
                            new GcConfigSkuStarSink(parameterTool),
                            new RetryRequestFailureHandler(),
                            parameterTool)).name("sink-es");

            // 写入polardb
            gcConfigSKuStar.keyBy(GcConfigSkuStar::getSkuNo).addSink(new GoodsSkuStarSink()).name("sink-polardb");

            env.execute(parameterTool.get(FLINK_JOB_NAME));


        } catch (Exception e){
            e.printStackTrace();
            LOGGER.error("GcConfigSkuJob error:{}",e);
        }

    }
}
