package cn.wr.aggregate;

import cn.wr.model.StockData;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;

/**
 * @author RWang
 * @Date 2022/7/20
 */

public class StockDataAggregateFunction implements AggregateFunction<Tuple2<String, StockData>,Tuple2<String, StockData>,Tuple2<String, StockData>> {

    private static final long serialVersionUID = -253570339275592649L;

    @Override
    public Tuple2<String, StockData> createAccumulator() {
        return Tuple2.of("",new StockData());
    }

    @Override
    public Tuple2<String, StockData> add(Tuple2<String, StockData> value, Tuple2<String, StockData> accumulator) {

        String f0 = value.f0;
        StockData f1 = value.f1;
        // 通过变更f1 中的数据来最终获取最新值
        accumulator.f0 = f0;
        accumulator.f1 = f1;
        return accumulator;
    }

    @Override
    public Tuple2<String, StockData> getResult(Tuple2<String, StockData> accumulator) {
        return accumulator;
    }

    @Override
    public Tuple2<String, StockData> merge(Tuple2<String, StockData> a, Tuple2<String, StockData> b) {
        return null;
    }

}
