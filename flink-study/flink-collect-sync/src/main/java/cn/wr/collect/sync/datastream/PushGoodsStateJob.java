package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.constants.OutputTagConst;
import cn.wr.collect.sync.filter.GoodsStateChangeFilter;
import cn.wr.collect.sync.filter.PushGoodsStateFilter;
import cn.wr.collect.sync.flatmap.AssemblyGoodsStateFlatMap;
import cn.wr.collect.sync.flatmap.BinlogList2SingleFlatMapV3;
import cn.wr.collect.sync.flatmap.GoodsStateSideOutFlatMap;
import cn.wr.collect.sync.keyby.GoodsStateChangeKeyBy;
import cn.wr.collect.sync.keyby.PushGoodsStateKeyBy;
import cn.wr.collect.sync.model.stock.StockGoods;
import cn.wr.collect.sync.model.stock.StockGoodsSideOut;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import cn.wr.collect.sync.utils.KafkaConfigUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class PushGoodsStateJob {
    private static final Logger log = LoggerFactory.getLogger(PushGoodsStateJob.class);

    public static void main(String[] args) {
        try {
            // 初始化参数
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                log.info("GoodsStateChangeJob parameterTool is null");
                return;
            }
            // 获取环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // kafka binlog
            SingleOutputStreamOperator<StockGoodsSideOut> assembly = KafkaConfigUtil.buildSourceGoodsStateChange(env)
                    .setParallelism(parameterTool.getInt(STREAM_PUSH_GOODS_STATE_SOURCE_PARALLELISM))

                    .filter(new GoodsStateChangeFilter())
                    .setParallelism(parameterTool.getInt(STREAM_PUSH_GOODS_STATE_FLATMAP01_PARALLELISM))
                    .name("filter-binlog")

                    .flatMap(new BinlogList2SingleFlatMapV3())
                    .setParallelism(parameterTool.getInt(STREAM_PUSH_GOODS_STATE_FLATMAP01_PARALLELISM))
                    .name("split-binlog")

                    .keyBy(new GoodsStateChangeKeyBy())
                    .flatMap(new AssemblyGoodsStateFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_PUSH_GOODS_STATE_FLATMAP02_PARALLELISM))
                    .name("assembly-data");

            SingleOutputStreamOperator<StockGoods> sideOut = assembly
                    .filter(new PushGoodsStateFilter())
                    .name("filter")
                    .setParallelism(parameterTool.getInt(STREAM_PUSH_GOODS_STATE_FLATMAP02_PARALLELISM))
                    .process(new GoodsStateSideOutFlatMap())
                    .name("side-out")
                    .setParallelism(parameterTool.getInt(STREAM_PUSH_GOODS_STATE_FLATMAP02_PARALLELISM));

            // dtp 流
            DataStream<StockGoods> sideOutputDtp = sideOut.getSideOutput(OutputTagConst.OUT_PUT_TAG_STOCK_GOODS_DTP);

            // 非dtp流
            DataStream<StockGoods> sideOutputNotDtp = sideOut.getSideOutput(OutputTagConst.OUT_PUT_TAG_STOCK_GOODS_NOT_DTP);

            sideOutputDtp
                    .keyBy(new PushGoodsStateKeyBy())
                    .addSink(KafkaConfigUtil.buildPushDtpGoodsStateProducer(parameterTool))
                    .setParallelism(parameterTool.getInt(STREAM_PUSH_GOODS_STATE_SINK01_PARALLELISM))
                    .name("sink-dtp-kafka");

            sideOutputNotDtp
                    .keyBy(new PushGoodsStateKeyBy())
                    .addSink(KafkaConfigUtil.buildPushGoodsStateProducer(parameterTool))
                    .setParallelism(parameterTool.getInt(STREAM_PUSH_GOODS_STATE_SINK01_PARALLELISM))
                    .name("sink-common-kafka");
            env.execute("[PRD][KAFKA] - push_store_goods_state");
        }
        catch (Exception e) {
            log.error("GoodsStateChangeJob msg:{}, Exception", e.getMessage(), e);
        }

    }
}
