package cn.wr.datastream;


import cn.wr.filter.GcConfigSkuAndGcDisableStoreFilter;
import cn.wr.flatmap.GcConfigSkuAndGcDisableStoreFlatMap;
import cn.wr.map.CanalTransModelMap;
import cn.wr.model.BaseJudge;
import cn.wr.model.CanalDataModel;
import cn.wr.sink.UpdateSearchEsSink;
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
            env.setParallelism(1);
            DataStreamSource<String> canalSource = KafkaUtil.createCanalSource(env)
                    .setParallelism(parameterTool.getInt(STREAM_SOURCE_PARALLELISM));

            DataStream<CanalDataModel> canalDataModelDataStream = canalSource
                    .filter(new GcConfigSkuAndGcDisableStoreFilter())
                    .map(new CanalTransModelMap());

            // 处理逻辑 感知两张表变更后数据
            DataStream<BaseJudge> baseJudge = canalDataModelDataStream.flatMap(new GcConfigSkuAndGcDisableStoreFlatMap());

            baseJudge.print();
            // 更新es数据
            baseJudge.keyBy(BaseJudge::getSkuNo).addSink(EsSinkUtil.getSink(baseJudge,
                    new UpdateSearchEsSink(parameterTool),
                    new RetryRequestFailureHandler(),
                    parameterTool)).setParallelism(2);

            env.execute(parameterTool.get(FLINK_JOB_NAME));


        } catch (Exception e){
            e.printStackTrace();
            LOGGER.error("SearchStatusJob error:{}",e);
        }

    }
}
