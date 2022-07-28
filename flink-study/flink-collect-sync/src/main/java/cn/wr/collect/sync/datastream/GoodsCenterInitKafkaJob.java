package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.constants.OutputTagConst;
import cn.wr.collect.sync.datasource.GoodsCenterInitSource;
import cn.wr.collect.sync.flatmap.GoodsCenterInitFlatMap;
import cn.wr.collect.sync.flatmap.GoodsCenterSideOutFlatMap;
import cn.wr.collect.sync.model.goodscenter.GoodsCenterDTO;
import cn.wr.collect.sync.model.middledb.PgcMerchantInfoShortInit;
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

public class GoodsCenterInitKafkaJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsCenterInitKafkaJob.class);

    public static void main(String[] args) {
        LOGGER.info("GoodsCenterInitKafkaJob start ...");
        try {
            // 配置文件路径
            String proFilePath = ParameterTool.fromArgs(args).get("conf");

            // 获取全局配置
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);

            // 配置为空退出
            if (null == parameterTool) {
                LOGGER.info("GoodsCenterInitKafkaJob parameterTool is null");
                return;
            }

            // 环境配置
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 查询redis连锁数据作为数据源
            DataStreamSource<PgcMerchantInfoShortInit> data = env.addSource(new GoodsCenterInitSource())
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2KAFKA_INIT_SOURCE_PARALLELISM, 1));

            // 拆分组装数据
            SingleOutputStreamOperator<GoodsCenterDTO> splitData = data.flatMap(new GoodsCenterInitFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2KAFKA_INIT_FLATMAP_PARALLELISM, 1));

            // 侧路输出 dtp / 非dtp
            SingleOutputStreamOperator<GoodsCenterDTO> sideOut = splitData.process(new GoodsCenterSideOutFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2KAFKA_INIT_FLATMAP_PARALLELISM, 1));

            // dtp 流
            DataStream<GoodsCenterDTO> sideOutputDtp = sideOut.getSideOutput(OutputTagConst.OUT_PUT_TAG_DTP);

            // 非dtp流
            DataStream<GoodsCenterDTO> sideOutputNotDtp = sideOut.getSideOutput(OutputTagConst.OUT_PUT_TAG_NOT_DTP);

            // 发送kafka
            sideOutputNotDtp.addSink(KafkaConfigUtil.buildKafkaPropsGoodsCenterProducer(parameterTool))
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2KAFKA_INIT_SINK_PARALLELISM, 1))
                    .name("sink-no-dtp-kafka");
            
            // 发送kafka
            sideOutputDtp.addSink(KafkaConfigUtil.buildKafkaPropsGoodsCenterDtpProducer(parameterTool))
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2KAFKA_INIT_SINK_PARALLELISM, 1))
                    .name("sink-dtp-kafka");

            // 执行
            env.execute("[PRD][KAFKA] - goods_center(init)");
        }
        catch (Exception e) {
            LOGGER.error("GoodsCenterInitKafkaJob Exception: {}", e);
        }
    }
}
