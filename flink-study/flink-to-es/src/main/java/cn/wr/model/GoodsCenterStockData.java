package cn.wr.model;


import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author RWang
 * @Date 2022/8/9
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoodsCenterStockData extends BaseStockData{
    private static final long serialVersionUID = 35059887434208638L;

    private String quantity;
    @JsonProperty("sale_state")
    private String saleState;
}
