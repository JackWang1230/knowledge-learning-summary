package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.datasource.BasicPartnerStoresAllSource02;
import cn.wr.collect.sync.flatmap.GoodsSplitFlatMapV2;
import cn.wr.collect.sync.flatmap.Params2GoodsFlatMap;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.sink.GoodsElasticSinkV2;
import cn.wr.collect.sync.sink.GoodsMbsSinkV2;
import cn.wr.collect.sync.sink.SyncProgressIncrSink;
import cn.wr.collect.sync.utils.ESSinkUtil;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import cn.wr.collect.sync.utils.KafkaConfigUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class BatchSyncPartnerStoresAllJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchSyncPartnerStoresAllJob.class);

    public static void main(String[] args) {
        LOGGER.info("BatchSyncPartnerStoresAllJob start ......");
        try {
            // 初始化参数
            String proFilePath = ParameterTool.fromArgs(args).get("conf");

            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                LOGGER.info("BatchSyncPartnerStoresAllJob parameterTool is null");
                return;
            }

            // 获取环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 查询数据
            DataStreamSource<BasicModel<PgConcatParams>> source = env.addSource(new BasicPartnerStoresAllSource02())
                    .setParallelism(parameterTool.getInt(STREAM_STORE2ES_PT_SOURCE_PARALLELISM, 5));

            // 商品拆分
            SingleOutputStreamOperator<BasicModel<Model>> goodsData = source.flatMap(new Params2GoodsFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_STORE2ES_PT_FLATMAP01_PARALLELISM, 5));

            // 拆分
            SingleOutputStreamOperator<BasicModel<ElasticO2O>> splitData = goodsData.flatMap(new GoodsSplitFlatMapV2())
                    .setParallelism(parameterTool.getInt(STREAM_STORE2ES_PT_FLATMAP02_PARALLELISM, 5));

            // 写入es
            if (parameterTool.getBoolean(SINK_ELASTIC)) {
                // es sink o2o
                ESSinkUtil.addSink(splitData, new GoodsElasticSinkV2(), parameterTool,
                        parameterTool.getInt(STREAM_STORE2ES_PT_SINK01_PARALLELISM));
            }
            else {
                splitData.print().setParallelism(1);;
            }

            // 单独转发kafka给连锁维度索引
            splitData.keyBy(basic -> basic.getData().getSkuCode())
                    .addSink(KafkaConfigUtil.buildGoodsChangeProducerPartner(parameterTool))
                    .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_SINK02_PARALLELISM, 5))
                    .name("sink-kafka-simple");


            // sink mbs
            if (parameterTool.getBoolean(SINK_MBS)) {
                splitData.addSink(new GoodsMbsSinkV2())
                        .setParallelism(parameterTool.getInt(STREAM_STORE2ES_PT_SINK02_PARALLELISM, 5))
                        .name("sink-mbs");
            }

            // 同步进度更新redis
            if (parameterTool.getBoolean(SINK_CACHE_PRODUCT_PROGRESS)) {
                splitData.addSink(new SyncProgressIncrSink())
                        .setParallelism(parameterTool.getInt(STREAM_STORE2ES_PT_SINK03_PARALLELISM, 5))
                        .name("sink-progress-incr");
            }

            env.execute("[PRD][ES] - goods_index_2.0_real_time(store_pt "
                    + parameterTool.get(FLINK_COLLECT_VERSION) + ")");
        }
        catch (Exception e) {
            LOGGER.error("BatchSyncPartnerStoresAllJob error:{}", e);
        }
    }
}
