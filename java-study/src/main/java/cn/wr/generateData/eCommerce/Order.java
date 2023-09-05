package cn.wr.generateData.eCommerce;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author : WangRui
 * @date : 2023/3/10
 */

@Data
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("order_status")
    private int orderStatus;
    @JsonProperty("order_amount")
    private BigDecimal orderAmount;
    @JsonProperty("order_time")
    private Timestamp orderTime;

}
