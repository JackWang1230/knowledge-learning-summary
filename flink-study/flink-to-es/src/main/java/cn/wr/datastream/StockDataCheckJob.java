package cn.wr.datastream;


import cn.wr.aggregate.StockDataAggregateFunction;
import cn.wr.flatmap.CompareStockDataFlatMap;
import cn.wr.flatmap.DelayDataCompareFlatMap;
import cn.wr.flatmap.StockDataTransFlatMap;
import cn.wr.model.StockData;
import cn.wr.process.AbnormalData2Mysql;
import cn.wr.process.AbnormalDataDingProcess;
import cn.wr.process.DelayAbnormalProcess;
import cn.wr.utils.ExecutionEnvUtil;
import cn.wr.utils.KafkaUtil;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.constants.PropertiesConstants.*;

/**
 * @author RWang
 * @Date 2022/7/19
 */

public class StockDataCheckJob {

    private static final Logger logger = LoggerFactory.getLogger(StockDataCheckJob.class);

    public static void main(String[] args) {

        try {
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                logger.info("StockDataCheckJob parameterTool is null");
                return;
            }
            /** 基础环境配置加载 */
            StreamExecutionEnvironment env = ExecutionEnvUtil.getEnv(parameterTool);
            env.setParallelism(parameterTool.getInt(STREAM_GLOBAl_PARALLELISM));
            DataStreamSource<StockData> stockDataDataStreamSource = KafkaUtil.createKafkaSource(env)
                    .setParallelism(parameterTool.getInt(STREAM_SOURCE_PARALLELISM));

            DataStream<Tuple2<String, StockData>> stockData = stockDataDataStreamSource
                    .flatMap(new StockDataTransFlatMap());

            // 汇总15min的数据
            DataStream<Tuple2<String, StockData>> latestStockData = stockData.keyBy(stock -> stock.f0)
                    .window(TumblingProcessingTimeWindows.of(Time.minutes(parameterTool.getInt(STREAM_WINDOW_INTERNAL,10))))
//                    .window(TumblingProcessingTimeWindows.of(Time.seconds(30)))
                    .aggregate(new StockDataAggregateFunction())
                    .setParallelism(parameterTool.getInt(STREAM_PROCESS_AGG_PARALLELISM));

            // 去hbase查询结果进行对比
            DataStream<Tuple3<String,StockData, Long>> abnormalStockData = latestStockData
                    .keyBy(stock -> stock.f0)
                    .flatMap(new CompareStockDataFlatMap());

            // 异常数据 再次延迟 5min 处理
            DataStream<Tuple2<StockData, Long>> abnormalDelayStockData = abnormalStockData
                    .keyBy(f -> f.f0)
                    .process(new DelayAbnormalProcess(parameterTool.getLong(STREAM_DELAY_INTERNAL,300000)));

            // 调整 再次对比hbase 数据
            DataStream<StockData> abnormalDelayDingStockData = abnormalDelayStockData
                    .keyBy(f -> f.f0.getMerchantId()+f.f0.getStoreId()+f.f0.getInternalId())
                    .flatMap(new DelayDataCompareFlatMap());

            // 入库
            abnormalDelayDingStockData
                    .process(new AbnormalData2Mysql()).setParallelism(parameterTool.getInt(STREAM_SINK_DING_PARALLELISM,1));

            // 发送钉钉告警
//            abnormalDelayDingStockData
//                    .process(new AbnormalDataDingProcess());

            env.execute("[PRD][KAFKA] - abnormal stock data dingTalk msg");

        } catch (Exception e){
            e.printStackTrace();
            logger.error("StockDataCheckJob error:{}",e);
        }
    }
}
