package cn.wr.collect.sync.keyby;

import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.model.stock.StockGoods;
import cn.wr.collect.sync.utils.MathUtil;
import org.apache.flink.api.java.functions.KeySelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class PushGoodsStateKeyBy implements KeySelector<StockGoods, String> {
    private static final long serialVersionUID = 4050634811521271540L;
    private static final Logger log = LoggerFactory.getLogger(PushGoodsStateKeyBy.class);

    @Override
    public String getKey(StockGoods stockGoods) throws Exception {
        if (Objects.isNull(stockGoods)) {
            // null key 异常，生成随机数
            return String.valueOf(MathUtil.random(1, 12));
        }
        return stockGoods.getMerchantId() + SymbolConstants.HOR_LINE + stockGoods.getStoreId()
                + SymbolConstants.HOR_LINE + stockGoods.getInternalId();
    }
}
