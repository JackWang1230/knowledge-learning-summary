package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.model.stock.StockGoods;
import cn.wr.collect.sync.model.stock.StockGoodsSideOut;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import static cn.wr.collect.sync.constants.OutputTagConst.*;


public class GoodsStateSideOutFlatMap extends ProcessFunction<StockGoodsSideOut, StockGoods> {
    private static final long serialVersionUID = 2648271047705678271L;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void processElement(StockGoodsSideOut sideOut, Context context, Collector<StockGoods> collector) throws Exception {
        if (CommonConstants.NET_TYPE_2.equals(sideOut.getNetType())) {
            context.output(OUT_PUT_TAG_STOCK_GOODS_DTP, sideOut.getStockGoods());
        }
        else {
            context.output(OUT_PUT_TAG_STOCK_GOODS_NOT_DTP, sideOut.getStockGoods());
        }
    }

}
