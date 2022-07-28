package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.datasource.ComplementDataSource;
import cn.wr.collect.sync.flatmap.GoodsSplitFlatMapV2;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.sink.GoodsElasticSinkV2;
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

@Deprecated
public class ComplementDataJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicDataInitJob.class);

    public static void main(String[] args) {
        LOGGER.info("ComplementDataJob start......");
        try {
            // 初始化参数
            String proFilePath = ParameterTool.fromArgs(args).get("conf");

            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);

            if (null == parameterTool) {
                LOGGER.info("ComplementDataJob parameterTool is null");
                return;
            }
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 查询数据
            DataStreamSource<BasicModel<Model>> complementData = env.addSource(new ComplementDataSource())
                    .setParallelism(parameterTool.getInt(STREAM_ES_COMP_SOURCE_PARALLELISM, 1));

            // 拆分
            SingleOutputStreamOperator<BasicModel<ElasticO2O>> assemblyData = complementData.flatMap(new GoodsSplitFlatMapV2())
                    .setParallelism(parameterTool.getInt(STREAM_ES_COMP_FLATMAP01_PARALLELISM, 5));


            if (parameterTool.getBoolean(SINK_ELASTIC)) {
                // es sink o2o
                assemblyData.keyBy(item -> item.getData().getSkuCode())
                        .addSink(ESSinkUtil.getSink(assemblyData, new GoodsElasticSinkV2(),
                                new RetryRequestFailureHandler(), parameterTool))
                        .setParallelism(parameterTool.getInt(STREAM_ES_COMP_SINK01_PARALLELISM, 5))
                        .name("sink-elastic");
            }

            // 发送kafka
            if (parameterTool.getBoolean(SINK_KAFKA)) {
                assemblyData.keyBy(item -> item.getData().getRealTradeCode() + item.getData().getMerchantId()
                        + item.getData().getStoreId() + item.getData().getLocation())
                        .addSink(KafkaConfigUtil.buildGoodsChangeProducer(parameterTool))
                        .setParallelism(parameterTool.getInt(STREAM_ES_COMP_SINK02_PARALLELISM, 5))
                        .name("sink-kafka");
            }

            env.execute("Complement Data Job");
        }
        catch (Exception e) {
            LOGGER.error("ComplementDataJob Exception:{}", e);
        }
    }
}
