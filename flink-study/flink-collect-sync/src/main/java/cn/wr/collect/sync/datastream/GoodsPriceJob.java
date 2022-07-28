package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.filter.GoodsPriceFilter;
import cn.wr.collect.sync.flatmap.Basic2EsFlatMap;
import cn.wr.collect.sync.flatmap.BinlogList2SingleFlatMapV4;
import cn.wr.collect.sync.keyby.BinlogKeyBy;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.sink.Basic2EsSink02;
import cn.wr.collect.sync.sink.PriceRedisKeySink;
import cn.wr.collect.sync.utils.ESSinkUtil;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import cn.wr.collect.sync.utils.KafkaConfigUtil;
import cn.wr.collect.sync.utils.PlatformRetryRequestFailureHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;

@Slf4j
public class GoodsPriceJob {

    public static void main(String[] args) {

        log.info("GoodsPriceJob start....");

        try {

            // 获取参数配置
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                log.info("GoodsPriceJob parameterTool is null");
                return;
            }

            // 设置执行环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 接收解析binlog基础数据
            DataStreamSource<PolarDbBinlogBatch> source = KafkaConfigUtil.buildPriceSource(env)
                    .setParallelism(parameterTool.getInt(STREAM_PRICE_SOURCE_PARALLELISM, 5));

            // 拆分binlog
            SingleOutputStreamOperator<BasicModel<Model>> splitData = source.filter(new GoodsPriceFilter())
                    .setParallelism(parameterTool.getInt(STREAM_PRICE_FLATMAP01_PARALLELISM, 5))
                    .flatMap(new BinlogList2SingleFlatMapV4())
                    .setParallelism(parameterTool.getInt(STREAM_PRICE_FLATMAP01_PARALLELISM, 5));

            // 分组/参数转换
            SingleOutputStreamOperator<BasicModel<ElasticO2O>> assembly = splitData.keyBy(new BinlogKeyBy())
                    .flatMap(new Basic2EsFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_BASIC2ES_FLATMAP02_PARALLELISM, 5));

            // sink es
            assembly.keyBy(basic -> basic.getData().getSkuCode())
                    .addSink(ESSinkUtil.getSink(assembly, new Basic2EsSink02(), new PlatformRetryRequestFailureHandler(), parameterTool))
                    .setParallelism(parameterTool.getInt(STREAM_PRICE_SINK01_PARALLELISM, 5))
                    .name("sink-es");

            // 写入es成功后，去掉redis分布式锁
            assembly.keyBy(basic -> basic.getData().getSkuCode())
                            .addSink(new PriceRedisKeySink())
                            .setParallelism(parameterTool.getInt(STREAM_PRICE_SINK01_PARALLELISM, 5))
                    .name("clean-redis");

            env.execute("[PRD][ES] - goods_index_2.0_real_time(price_rt " + parameterTool.get(FLINK_COLLECT_VERSION) + ")");

        } catch (Exception e) {

            log.error("GoodsPriceJob message: {} error:{}", e.getMessage(), e);

        }
    }
}
