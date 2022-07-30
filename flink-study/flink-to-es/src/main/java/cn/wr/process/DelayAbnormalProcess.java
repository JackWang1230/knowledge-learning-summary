package cn.wr.process;

import cn.wr.model.StockData;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author RWang
 * @Date 2022/7/26
 */

public class DelayAbnormalProcess extends KeyedProcessFunction<String, Tuple3<String,StockData,Long>,Tuple2<StockData,Long>> {


    private static final long serialVersionUID = 7233046503397766368L;
    // private ValueState<StockData> stockDataValueState;
    private MapState<StockData,Long> mapState;
    private final Long internal;
    private final ReentrantLock lock = new ReentrantLock();

    public DelayAbnormalProcess(long internal) {
        this.internal = internal;
    }


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
//        ValueStateDescriptor<StockData> valueStateDescriptor = new ValueStateDescriptor<>("ts_time", StockData.class);
//        stockDataValueState = getRuntimeContext().getState(valueStateDescriptor);
        MapStateDescriptor<StockData,Long> mapStateDescriptor = new MapStateDescriptor<>("map-state", StockData.class, Long.class);
        mapState = getRuntimeContext().getMapState(mapStateDescriptor);

    }

    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<Tuple2<StockData, Long>> out) throws Exception {

        super.onTimer(timestamp, ctx, out);
        lock.lock();
        try {
            // 多线程情况下会出现 并发异常 MapState 例如在如下的 iterator中
            // 会出现删除的同时 在进行修改 导致异常 因此需要加锁
            Iterator<Map.Entry<StockData, Long>> iterator = mapState.iterator();
            while (iterator.hasNext()){
                Map.Entry<StockData, Long> entry = iterator.next();
                out.collect(Tuple2.of(entry.getKey(),entry.getValue()));
                mapState.remove(entry.getKey());
            }
        } finally {
            lock.unlock();
        }
       //  out.collect(Tuple2.of(stockDataValueState.value(),timestamp));


    }


    @Override
    public void processElement(Tuple3<String,StockData, Long> stockDataLongTuple3, Context context, Collector<Tuple2<StockData,Long>> collector) throws Exception {

////        long l = 120000L;
//        long l = 300000L;
        long delayTime = stockDataLongTuple3.f2+internal;
        mapState.put(stockDataLongTuple3.f1,stockDataLongTuple3.f2);
        // stockDataValueState.update(stockDataLongTuple3.f1);
        context.timerService().registerProcessingTimeTimer(delayTime);

    }
}
