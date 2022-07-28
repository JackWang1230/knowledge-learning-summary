package cn.wr.model;

import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @author RWang
 * @Date 2022/7/19
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockData implements Serializable {
    private static final long serialVersionUID = 5833077753291337747L;
    /**
     *   "merchantId": 635,
     *   "storeId": 241408,
     *   "internalId": "95344838985367181",
     *   "virtualQuantity": "",
     *   "totalAmount": "",
     *   "Quantity": "",
     *   "source": 1,
     *   "virtualOn": 0,
     *   "saleState": "1"
     */
    /** 连锁id*/
    private long merchantId;
    /** 门店id*/
    private long storeId;
    /** 商品内码*/
    private String internalId;
    /** 虚拟库存*/
    private String virtualQuantity;
    /** 商品数量*/
    private String totalAmount;
    /** 数量*/
    @JsonProperty("Quantity")
    private String quantity;
    private int source;
    private int virtualOn;
    /** 上下线状态*/
    private String saleState;


}
