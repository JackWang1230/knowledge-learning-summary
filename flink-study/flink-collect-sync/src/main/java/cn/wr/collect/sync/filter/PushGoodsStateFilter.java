package cn.wr.collect.sync.filter;

import cn.wr.collect.sync.model.stock.StockGoodsSideOut;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;

import java.util.Objects;

public class PushGoodsStateFilter implements FilterFunction<StockGoodsSideOut> {
    private static final long serialVersionUID = 8200813321632092869L;

    @Override
    public boolean filter(StockGoodsSideOut sideOut) throws Exception {
        return Objects.nonNull(sideOut) && Objects.nonNull(sideOut.getStockGoods())
                && Objects.nonNull(sideOut.getStockGoods().getMerchantId())
                && Objects.nonNull(sideOut.getStockGoods().getStoreId())
                && StringUtils.isNotBlank(sideOut.getStockGoods().getInternalId())
                && Objects.nonNull(sideOut.getStockGoods().getSaleState());
    }
}
