package cn.wr.collect.sync.model.stock;

import java.io.Serializable;

public class StockGoodsSideOut implements Serializable {
    private static final long serialVersionUID = 632824633510620014L;
    /**
     * dtp标识
     */
    private Integer netType;

    private StockGoods stockGoods;

    public Integer getNetType() {
        return netType;
    }

    public void setNetType(Integer netType) {
        this.netType = netType;
    }

    public StockGoods getStockGoods() {
        return stockGoods;
    }

    public void setStockGoods(StockGoods stockGoods) {
        this.stockGoods = stockGoods;
    }
}
