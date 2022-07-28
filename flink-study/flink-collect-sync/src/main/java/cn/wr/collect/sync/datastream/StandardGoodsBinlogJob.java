package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.flatmap.StandardGoodsBinlogFlatMap;
import cn.wr.collect.sync.flatmap.StandardSplitFlatMap;
import cn.wr.collect.sync.keyby.BinlogKeyBy;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.standard.StandardElastic;
import cn.wr.collect.sync.sink.Standard2EsSink;
import cn.wr.collect.sync.utils.ESSinkUtil;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import cn.wr.collect.sync.utils.KafkaConfigUtil;
import cn.wr.collect.sync.utils.PlatformRetryRequestFailureHandler;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;

public class StandardGoodsBinlogJob {
    private static final Logger log = LoggerFactory.getLogger(StandardGoodsBinlogJob.class);

    public static void main(String[] args) {
        log.info("StandardGoodsBinlogJob start ...");
        try {
            // 配置文件路径
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                log.info("StandardGoodsBinlogJob parameterTool is null");
                return;
            }

            // 环境配置
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 接收解析binlog基础数据
            DataStreamSource<PolarDbBinlogBatch> data = KafkaConfigUtil.buildStandardGoodsSource(env)
                    .setParallelism(parameterTool.getInt(STREAM_STANDARD_BINLOG_SOURCE_PARALLELISM, 5));

            // 拆分数据binlog
            SingleOutputStreamOperator<BasicModel<Model>> splitBinlog = data.flatMap(new StandardGoodsBinlogFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_STANDARD_BINLOG_FLATMAP01_PARALLELISM, 1));

            // 拆分组装数据
            SingleOutputStreamOperator<BasicModel<StandardElastic>> assembly = splitBinlog
                    .keyBy(binlog -> new BinlogKeyBy())
                    .flatMap(new StandardSplitFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_STANDARD_BINLOG_FLATMAP02_PARALLELISM, 1));;

            // sink es
            assembly.keyBy(basic -> basic.getData().getId())
                    .addSink(ESSinkUtil.getSink(assembly, new Standard2EsSink(), new PlatformRetryRequestFailureHandler(), parameterTool))
                    .setParallelism(parameterTool.getInt(STREAM_STANDARD_BINLOG_SINK_PARALLELISM, 5))
                    .name("sink-es");

            env.execute("[PRD][ES] - standard_goods_binlog(V2)");
        }
        catch (Exception e) {
            log.error("StandardGoodsBinlogJob Exception:{}", e);
        }
    }
}

