package cn.wr.bloomfilter;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

/**
 *
 *
 * 1. 使用状态编程ValueState/MapState实现去重
 *    常用方式，可以使用内存/文件系统/RocksDB作为状态后端存储。
 *
 * 2. 结合Redis使用布隆过滤器实现去重
 *    适用对上亿数据量进行去重实现，占用资源少效率高，有小概率误判。
 *
 *
 * @author : WangRui
 * @date : 2022/10/10
 */

public class UserVisitorTest {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //指定时间语义
        WatermarkStrategy<UserBehavior> wms = WatermarkStrategy
                .<UserBehavior>forMonotonousTimestamps()
                .withTimestampAssigner(new SerializableTimestampAssigner<UserBehavior>() {
                    @Override
                    public long extractTimestamp(UserBehavior element, long recordTimestamp) {
                        return element.getTimeStamp() * 1000L;
                    }
                });

        //读取数据、映射、过滤
        SingleOutputStreamOperator<UserBehavior> userBehaviorDS = env
                .readTextFile("input/UserBehavior.csv")
                .map(new MapFunction<String, UserBehavior>() {
                    @Override
                    public UserBehavior map(String value) throws Exception {
                        String[] split = value.split(",");
                        return new UserBehavior(Long.parseLong(split[0])
                                , Long.parseLong(split[1])
                                , Integer.parseInt(split[2])
                                , split[3]
                                , Long.parseLong(split[4]));
                    }
                })
                //.filter(data -> "pv".equals(data.getBehavior()))  //lambda表达式写法
                .filter(new FilterFunction<UserBehavior>() {
                    @Override
                    public boolean filter(UserBehavior value) throws Exception {
                        if (value.getUserId().equals("pv")) {
                            return true;
                        }return false; }})
                .assignTimestampsAndWatermarks(wms);



        //去重按全局去重，故使用行为分组，仅为后续开窗使用、开窗
        WindowedStream<UserBehavior, String, TimeWindow> windowDS = userBehaviorDS.keyBy(UserBehavior::getUserId)
                .window(TumblingEventTimeWindows.of(Time.hours(1)));

        SingleOutputStreamOperator<UserVisitorCount> processDS = windowDS
                .trigger(new MyTrigger()).process(new UserVisitorWindowFunc());

        processDS.print();
        env.execute("ddd");
    }
}
