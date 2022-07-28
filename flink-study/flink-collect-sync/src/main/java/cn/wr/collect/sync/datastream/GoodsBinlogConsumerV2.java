package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.flatmap.BinlogList2SingleFlatMapV2;
import cn.wr.collect.sync.flatmap.Goods2HBaseFlatMap;
import cn.wr.collect.sync.flatmap.GoodsSplitFlatMapV2;
import cn.wr.collect.sync.keyby.BinlogKeyByGoods;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.sink.GoodsElasticSinkV2;
import cn.wr.collect.sync.sink.GoodsMbsSinkV2;
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


public class GoodsBinlogConsumerV2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsBinlogConsumerV2.class);

    /**
     * main run
     */
    public static void main(String[] args) {
        LOGGER.info("GoodsBinlogConsumerV2 start ......");
        try {
            // 获取参数配置
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                LOGGER.info("GoodsBinlogConsumerV2 parameterTool is null");
                return;
            }

            // 设置执行环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 接收解析binlog基础数据
            DataStreamSource<PolarDbBinlogBatch> kafkaSource = KafkaConfigUtil.buildGoodsSourceV2(env)
                    .setParallelism(parameterTool.getInt(STREAM_GOODS_SOURCE_PARALLELISM, 5));

            // 拆分binlog多条数据为单条
            SingleOutputStreamOperator<BasicModel<Model>> singleData = kafkaSource
                    .flatMap(new BinlogList2SingleFlatMapV2())
                    .setParallelism(parameterTool.getInt(STREAM_GOODS_FLATMAP01_PARALLELISM, 5))
                    .name("split-binlog");

            // 多版本并行情况下，保证单一版本写入hbase
            SingleOutputStreamOperator<BasicModel<ElasticO2O>> splitData;
            if (parameterTool.getBoolean(SINK_HBASE)) {
                SingleOutputStreamOperator<BasicModel<Model>> sinkHbase = singleData
                        .keyBy(new BinlogKeyByGoods())
                        .flatMap(new Goods2HBaseFlatMap())
                        .setParallelism(parameterTool.getInt(STREAM_GOODS_FLATMAP02_PARALLELISM, 5))
                        .name("save-hbase");
                splitData = sinkHbase.flatMap(new GoodsSplitFlatMapV2())
                        .setParallelism(parameterTool.getInt(STREAM_GOODS_FLATMAP02_PARALLELISM, 5))
                        .name("split-assembly-es");
            }
            else {
                splitData = singleData
                        .keyBy(new BinlogKeyByGoods())
                        .flatMap(new GoodsSplitFlatMapV2())
                        .setParallelism(parameterTool.getInt(STREAM_GOODS_FLATMAP02_PARALLELISM, 5))
                        .name("split-assembly-es");
            }

            // sink es
            if (parameterTool.getBoolean(SINK_ELASTIC)) {
                splitData.keyBy(item -> item.getData().getSkuCode())
                        .addSink(ESSinkUtil.getSink(splitData, new GoodsElasticSinkV2(),
                                new PlatformRetryRequestFailureHandler(), parameterTool))
                        .setParallelism(parameterTool.getInt(STREAM_GOODS_SINK01_PARALLELISM, 5))
                        .name("sink-elastic");
            }
            else {
                // 测试情况下打印日志
                splitData.print().setParallelism(1);
            }

            // 发送kafka
            if (parameterTool.getBoolean(SINK_KAFKA)) {
                splitData.keyBy(item -> item.getData().getRealTradeCode() + item.getData().getMerchantId()
                        + item.getData().getStoreId() + item.getData().getLocation())
                        .addSink(KafkaConfigUtil.buildGoodsChangeProducer(parameterTool))
                        .setParallelism(parameterTool.getInt(STREAM_GOODS_SINK03_PARALLELISM, 5))
                        .name("sink-kafka");
            }

            // 单独转发kafka给连锁维度索引
            splitData.keyBy(basic -> basic.getData().getSkuCode())
                    .addSink(KafkaConfigUtil.buildGoodsChangeProducerPartner(parameterTool))
                    .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_SINK02_PARALLELISM, 5))
                    .name("sink-kafka-simple");

            // sink mbs
            if (parameterTool.getBoolean(SINK_MBS)) {
                splitData.keyBy(item -> item.getData().getSkuCode())
                        .addSink(new GoodsMbsSinkV2())
                        .setParallelism(parameterTool.getInt(STREAM_GOODS_SINK02_PARALLELISM, 5))
                        .name("sink-mbs");
            }

            env.execute("[PRD][ES/MBS] - goods_index_2.0_real_time(goods_rt " + parameterTool.get(FLINK_COLLECT_VERSION) + ")");
        }
        catch (Exception e) {
            LOGGER.error("GoodsBinlogConsumerV2 error:{}", e);
        }
    }
}
