package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.filter.GoodsCenterAlarmFilter;
import cn.wr.collect.sync.flatmap.GoodsCenterAlarmProcess;
import cn.wr.collect.sync.flatmap.GoodsCenterAlarmSplitFlatMap;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.alarm.GoodsCenterAlarm;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import cn.wr.collect.sync.utils.KafkaConfigUtil;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.collect.sync.constants.PropertiesConstants.STREAM_GOODS_ALARM_FLATMAP_PARALLELISM;
import static cn.wr.collect.sync.constants.PropertiesConstants.STREAM_GOODS_ALARM_SOURCE_PARALLELISM;

public class GoodsCenterAlarmJob {
    private static final Logger log = LoggerFactory.getLogger(GoodsCenterKafkaJob.class);

    public static void main(String[] args) {
        log.info("GoodsCenterAlarmJob start ...");
        try {
            // 配置文件路径
            String proFilePath = ParameterTool.fromArgs(args).get("conf");

            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);

            if (null == parameterTool) {
                log.info("GoodsCenterAlarmJob parameterTool is null");
                return;
            }

            // 环境配置
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 接收解析binlog基础数据
            DataStreamSource<PolarDbBinlogBatch> data = KafkaConfigUtil.buildSourceGoodsCenterAlarm(env)
                    .setParallelism(parameterTool.getInt(STREAM_GOODS_ALARM_SOURCE_PARALLELISM, 5));

            // 拆分组装数据
            data.filter(new GoodsCenterAlarmFilter())
                    .setParallelism(parameterTool.getInt(STREAM_GOODS_ALARM_FLATMAP_PARALLELISM, 5))

                    .flatMap(new GoodsCenterAlarmSplitFlatMap())
                    .setParallelism(parameterTool.getInt(STREAM_GOODS_ALARM_FLATMAP_PARALLELISM, 5))

                    .keyBy((KeySelector<Tuple2<String, GoodsCenterAlarm>, String>) tuple2 -> tuple2.f0)
                    .window(TumblingProcessingTimeWindows.of(Time.seconds(65)))
                    .process(new GoodsCenterAlarmProcess())
                    .setParallelism(parameterTool.getInt(STREAM_GOODS_ALARM_FLATMAP_PARALLELISM, 5))
                    .print()
                    .setParallelism(1)
            ;

            env.execute("[PRD][DINGDING] - goods_change_alarm");
        }
        catch (Exception e) {
            log.error("GoodsCenterAlarmJob msg: {} Exception: {}", e.getMessage(), e);
        }
    }
}
