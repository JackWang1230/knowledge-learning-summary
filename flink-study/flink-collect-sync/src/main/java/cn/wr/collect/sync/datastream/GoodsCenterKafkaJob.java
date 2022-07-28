package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.constants.OutputTagConst;
import cn.wr.collect.sync.flatmap.GoodsCenterSideOutFlatMap;
import cn.wr.collect.sync.flatmap.GoodsCenterSplitFlatMap;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.goodscenter.GoodsCenterDTO;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import cn.wr.collect.sync.utils.KafkaConfigUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;

public class GoodsCenterKafkaJob {
    private static final Logger log = LoggerFactory.getLogger(GoodsCenterKafkaJob.class);

    public static void main(String[] args) {
        log.info("GoodsCenterKafkaJob start ...");
        try {
            // 配置文件路径
            String proFilePath = ParameterTool.fromArgs(args).get("conf");

            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);

            if (null == parameterTool) {
                log.info("GoodsCenterKafkaJob parameterTool is null");
                return;
            }

            // 环境配置
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 接收解析binlog基础数据
            DataStreamSource<PolarDbBinlogBatch> data = KafkaConfigUtil.buildGoodsCenterSourceConsumer(env)
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2KAFKA_SOURCE_PARALLELISM, 5));

            // 拆分组装数据
            SingleOutputStreamOperator<GoodsCenterDTO> splitData = data.flatMap(new GoodsCenterSplitFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2KAFKA_FLATMAP_PARALLELISM, 1));

            // 侧路输出 dtp / 非dtp
            SingleOutputStreamOperator<GoodsCenterDTO> sideOut = splitData.process(new GoodsCenterSideOutFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2KAFKA_FLATMAP_PARALLELISM, 1));

            // dtp 流
            DataStream<GoodsCenterDTO> sideOutputDtp = sideOut.getSideOutput(OutputTagConst.OUT_PUT_TAG_DTP);

            // 非dtp流
            DataStream<GoodsCenterDTO> sideOutputNotDtp = sideOut.getSideOutput(OutputTagConst.OUT_PUT_TAG_NOT_DTP);

            // 发送kafka
            sideOutputNotDtp.addSink(KafkaConfigUtil.buildKafkaPropsGoodsCenterProducer(parameterTool))
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2KAFKA_SINK_PARALLELISM, 1))
                    .name("sink-no-dtp-kafka");

            // 发送kafka
            sideOutputDtp.addSink(KafkaConfigUtil.buildKafkaPropsGoodsCenterDtpProducer(parameterTool))
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2KAFKA_SINK_PARALLELISM, 1))
                    .name("sink-dtp-kafka");

            env.execute("[PRD][KAFKA] - goods_center");
        }
        catch (Exception e) {
            log.error("GoodsCenterKafkaJob msg: {} Exception: {}", e.getMessage(), e);
        }
    }
}
