package cn.wr.model;

import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @author RWang
 * @Date 2022/8/9
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerStockData implements Serializable {

    private static final long serialVersionUID = 1968813743958326631L;

    @JsonProperty("goods_internal_id")
    private String goodsInternalId;

    @JsonProperty("store_internal_id")
    private String storeInternalId;

    @JsonProperty("stock_quantity")
    private String stockQuantity;

    private String status;

    private Long merchantId;


}
