package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.constants.PropertiesConstants;
import cn.wr.collect.sync.filter.StockMerchantFilter;
import cn.wr.collect.sync.flatmap.BinlogList2SingleFlatMapV4;
import cn.wr.collect.sync.keyby.BinlogKeyBy;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.sink.BasicSyncSinkV2;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import cn.wr.collect.sync.utils.KafkaConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;

@Slf4j
public class StockMerchantJob {

    public static void main(String[] args) {
        log.info("StockMerchantJob start....");

        try {
            // 获取参数配置
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                log.info("StockMerchantJob parameterTool is null");
                return;
            }

            // 设置执行环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 接收解析binlog基础数据
            DataStreamSource<PolarDbBinlogBatch> source = KafkaConfigUtil.buildStockSource(env)
                    .setParallelism(parameterTool.getInt(STREAM_STOCK_SOURCE_PARALLELISM, 5));

            // 拆分binlog
            SingleOutputStreamOperator<BasicModel<Model>> singleFlat = source
                    .filter(new StockMerchantFilter())
                    .setParallelism(parameterTool.getInt(STREAM_STOCK_FLATMAP01_PARALLELISM, 5))
                    .flatMap(new BinlogList2SingleFlatMapV4())
                    .setParallelism(parameterTool.getInt(STREAM_STOCK_FLATMAP01_PARALLELISM, 5));


            // 写入 redis
            singleFlat.keyBy(new BinlogKeyBy())
                    .addSink(new BasicSyncSinkV2())
                    .setParallelism(parameterTool.getInt(PropertiesConstants.STREAM_BASIC2CACHE_SINK01_PARALLELISM))
                    .name("sink-basic");


            env.execute("[PRD][REDIS] - stock_merchant(" + parameterTool.get(FLINK_COLLECT_VERSION) + ")");

        } catch (Exception e) {
            log.error("StockMerchantJob message: {} error:{}", e.getMessage(), e);
        }
    }


}
