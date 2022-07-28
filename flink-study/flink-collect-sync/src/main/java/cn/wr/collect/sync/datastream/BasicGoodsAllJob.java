package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.filter.GoodsChangeStoreFilter;
import cn.wr.collect.sync.flatmap.BasicGoodsAllSplitFlatMap;
import cn.wr.collect.sync.flatmap.GoodsStoreSplitFlatMap;
import cn.wr.collect.sync.flatmap.Params2GoodsStoreFlatMap;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.kafka.ElasticGoodsDTO;
import cn.wr.collect.sync.sink.GoodsStoreSink;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import cn.wr.collect.sync.utils.KafkaConfigUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class BasicGoodsAllJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicGoodsAllJob.class);

    public static void main(String[] args) {
        LOGGER.info("### BasicGoodsAllJob start ......");
        try {
            // 初始化参数
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                LOGGER.info("### BasicGoodsAllJob parameterTool is null");
                return;
            }

            // 设置执行环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);
            // 接收解析binlog基础数据
            DataStreamSource<BasicModel<Model>> kafkaSource = KafkaConfigUtil.buildBasicDataGoodsAllGoodsSource(env)
                    .setParallelism(parameterTool.getInt(STREAM_BASIC_GOODS_ALL_INIT_FILTER_PARALLELISM, 6));

            SingleOutputStreamOperator<BasicModel<PgConcatParams>> basicModelSingleOutputStreamOperator = kafkaSource.flatMap(new BasicGoodsAllSplitFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_BASIC_GOODS_ALL_INIT_FILTER_PARALLELISM, 6));

            // 根据参数输出商品数据
            SingleOutputStreamOperator<BasicModel<Model>> goodsData = basicModelSingleOutputStreamOperator.flatMap(new Params2GoodsStoreFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_BASIC_GOODS_ALL_INIT_FLATMAP_PARALLELISM, 6));

            // 拆分
            SingleOutputStreamOperator<BasicModel<ElasticGoodsDTO>> splitData = goodsData.flatMap(new GoodsStoreSplitFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_BASIC_GOODS_ALL_INIT_FLATMAP_PARALLELISM, 6));

            // 过滤
            SingleOutputStreamOperator<BasicModel<ElasticGoodsDTO>> assembly = splitData.filter(new GoodsChangeStoreFilter())
                    .setParallelism(parameterTool.getInt(STREAM_BASIC_GOODS_ALL_INIT_FLATMAP_PARALLELISM, 6));

            // sink
            assembly.keyBy(model -> (model.getData().getTradeCode() + model.getData().getMerchantId()
                    + model.getData().getStoreId()))
                    .addSink(new GoodsStoreSink())
                    .setParallelism(parameterTool.getInt(STREAM_BASIC_GOODS_ALL_INIT_SINK_PARALLELISM, 6))
                    .name("sink-redis");

            env.execute("[PRD][REDIS] - basic_goods_all_sync_job ");

        }catch (Exception e){
            LOGGER.error("### BasicGoodsAllJob error:{}", e);
        }

    }
}
