import cn.wr.model.StockData;
import org.apache.flink.api.java.functions.KeySelector;

/**
 * @author RWang
 * @Date 2022/7/29
 */

public class KeySelectorImpl implements KeySelector<StockData,String> {
    private static final long serialVersionUID = -1428162114875254112L;

    @Override
    public String getKey(StockData value) throws Exception {
        return value.getMerchantId()+value.getStoreId()+value.getInternalId();
    }
}
