package cn.wr.model;

import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author RWang
 * @Date 2022/8/5
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbnormalStock {

    @JsonProperty("stock_no")
    private String stockNo;

    @JsonProperty("merchant_id")
    private Long merchantId;

    @JsonProperty("store_id")
    private Long storeId;

    @JsonProperty("internal_id")
    private String internalId;

    @JsonProperty("is_abnormal")
    private int  isAbnormal;

}
