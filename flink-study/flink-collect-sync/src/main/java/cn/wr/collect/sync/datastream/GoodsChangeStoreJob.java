package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.filter.GoodsChangeStoreFilter;
import cn.wr.collect.sync.sink.GoodsStoreSink;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import cn.wr.collect.sync.utils.KafkaConfigUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;

@Deprecated
public class GoodsChangeStoreJob {
    private static final Logger log = LoggerFactory.getLogger(GoodsChangeStoreJob.class);

    public static void main(String[] args) {
        log.info("GoodsChangeStoreJob start ...");
        try {
            // 配置文件路径
            String proFilePath = ParameterTool.fromArgs(args).get("conf");

            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);

            if (null == parameterTool) {
                log.info("GoodsChangeStoreJob parameterTool is null");
                return;
            }

            // 环境配置
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 接收kafka数据
            KafkaConfigUtil.buildGoodsStoreSource(env)
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2REDIS_SOURCE_PARALLELISM, 5))

                    // 过滤
                    .filter(new GoodsChangeStoreFilter())
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2REDIS_FILTER_PARALLELISM, 5))

                    // 分组
                    .keyBy(model -> (model.getData().getTradeCode() + model.getData().getMerchantId()
                                + model.getData().getStoreId()))
                    // sink
                    .addSink(new GoodsStoreSink())
                    .setParallelism(parameterTool.getInt(STREAM_GOODS2REDIS_SINK_PARALLELISM, 1))
                    .name("sink-redis");

            env.execute("[PRD][REDIS] - partner_goods_all_sync_job");
        }
        catch (Exception e) {
            log.error("GoodsChangeStoreJob Exception:{}", e);
        }
    }
}
