package cn.wr.model.price;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author RWang
 * @Date 2022/8/16
 */
@Data
public class PriceListDetailsInitialEvent implements Serializable {
    private static final long serialVersionUID = 316092840720595057L;

    private String tableName;
    private List<PriceListDetails>  priceListDetailsList;

    public PriceListDetailsInitialEvent(String tableName, List<PriceListDetails> priceListDetailsList) {
        this.tableName = tableName;
        this.priceListDetailsList = priceListDetailsList;
    }
}
