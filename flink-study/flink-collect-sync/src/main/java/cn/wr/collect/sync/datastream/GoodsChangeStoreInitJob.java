package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.datasource.BasicSelfSyncSource;
import cn.wr.collect.sync.filter.GoodsChangeStoreFilter;
import cn.wr.collect.sync.flatmap.GoodsStoreSplitFlatMap;
import cn.wr.collect.sync.flatmap.Params2GoodsStoreFlatMap;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.kafka.ElasticGoodsDTO;
import cn.wr.collect.sync.sink.GoodsStoreSink;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;

@Deprecated
public class GoodsChangeStoreInitJob {
    private static final Logger log = LoggerFactory.getLogger(GoodsChangeStoreJob.class);

    public static void main(String[] args) {
        log.info("GoodsChangeStoreInitJob start ...");
        try {
            // 配置文件路径
            String proFilePath = ParameterTool.fromArgs(args).get(START_ARGS);

            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);

            if (null == parameterTool) {
                log.info("GoodsChangeStoreInitJob parameterTool is null");
                return;
            }

            // 环境配置
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 组装商品查询参数
            DataStreamSource<BasicModel<PgConcatParams>> source = env.addSource(new BasicSelfSyncSource())
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2REDIS_INIT_SOURCE_PARALLELISM, 5));

            // 根据参数输出商品数据
            SingleOutputStreamOperator<BasicModel<Model>> goodsData = source.flatMap(new Params2GoodsStoreFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2REDIS_INIT_FLATMAP01_PARALLELISM, 5));

            // 拆分
            SingleOutputStreamOperator<BasicModel<ElasticGoodsDTO>> splitData = goodsData.flatMap(new GoodsStoreSplitFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2REDIS_INIT_FLATMAP02_PARALLELISM, 5));

            // 过滤
            SingleOutputStreamOperator<BasicModel<ElasticGoodsDTO>> assembly = splitData.filter(new GoodsChangeStoreFilter())
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2REDIS_INIT_FILTER_PARALLELISM, 5));

            // sink
            assembly.keyBy(model -> (model.getData().getTradeCode() + model.getData().getMerchantId()
                            + model.getData().getStoreId()))
                    .addSink(new GoodsStoreSink())
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2REDIS_INIT_SINK_PARALLELISM, 1))
                    .name("sink-redis");

            env.execute("[PRD][REDIS] - goods_all_sync_job(init)");
        }
        catch (Exception e) {
            log.error("GoodsChangeStoreInitJob Exception:{}", e);
        }
    }
}
