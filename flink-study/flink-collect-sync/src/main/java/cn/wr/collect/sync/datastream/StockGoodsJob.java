package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.filter.StockGoodsFilter;
import cn.wr.collect.sync.flatmap.Basic2EsFlatMap;
import cn.wr.collect.sync.flatmap.BinlogList2SingleFlatMapV4;
import cn.wr.collect.sync.flatmap.Goods2HBaseFlatMap;
import cn.wr.collect.sync.keyby.BinlogKeyBy;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.sink.Basic2EsSink02;
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

public class StockGoodsJob {
    private static final Logger log = LoggerFactory.getLogger(BasicSyncEsJob.class);

    /**
     * main run
     */
    public static void main(String[] args) {
        log.info("StockGoodsJob start ......");
        try {
            // 获取参数配置
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                log.info("StockGoodsJob parameterTool is null");
                return;
            }

            // 设置执行环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 接收解析binlog基础数据
            DataStreamSource<PolarDbBinlogBatch> source = KafkaConfigUtil.buildStockSource(env)
                    .setParallelism(parameterTool.getInt(STREAM_STOCK_SOURCE_PARALLELISM, 5));

            // 拆分binlog
            SingleOutputStreamOperator<BasicModel<Model>> singleFlat = source
                    .filter(new StockGoodsFilter())
                    .setParallelism(parameterTool.getInt(STREAM_STOCK_FLATMAP01_PARALLELISM, 5))
                    .flatMap(new BinlogList2SingleFlatMapV4())
                    .setParallelism(parameterTool.getInt(STREAM_STOCK_FLATMAP01_PARALLELISM, 5));

            SingleOutputStreamOperator<BasicModel<Model>> sinkHbase = singleFlat
                    .keyBy(new BinlogKeyBy())
                    .flatMap(new Goods2HBaseFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_STOCK_FLATMAP01_PARALLELISM, 5))
                    .name("save-hbase");

            // 分组/参数转换
            SingleOutputStreamOperator<BasicModel<ElasticO2O>> assembly = sinkHbase.keyBy(new BinlogKeyBy())
                    .flatMap(new Basic2EsFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_FLATMAP02_PARALLELISM, 5));

            // sink es
            assembly.keyBy(basic -> basic.getData().getSkuCode())
                    .addSink(ESSinkUtil.getSink(assembly, new Basic2EsSink02(), new PlatformRetryRequestFailureHandler(), parameterTool))
                    .setParallelism(parameterTool.getInt(STREAM_STOCK_SINK01_PARALLELISM, 5))
                    .name("sink-es");

            env.execute("[PRD][ES] - goods_index_2.0_real_time(stock_rt " + parameterTool.get(FLINK_COLLECT_VERSION) + ")");
        } catch (Exception e) {
            log.error("StockGoodsJob message: {} error:{}", e.getMessage(), e);
        }
    }
}
