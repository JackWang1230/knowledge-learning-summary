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
public class BaseStockData implements Serializable {
    private static final long serialVersionUID = 8104566379034618713L;

    @JsonProperty("merchant_id")
    private Long merchantId;

    @JsonProperty("store_id")
    private Long storeId;

    @JsonProperty("internal_id")
    private String internalId;
}
