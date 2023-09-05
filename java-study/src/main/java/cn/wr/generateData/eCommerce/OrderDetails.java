package cn.wr.generateData.eCommerce;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author : WangRui
 * @date : 2023/3/10
 */

@Data
@Getter
@Setter
public class OrderDetails {

    private String orderId;
    private String subOrderId;
    private String itemId;
    private int subOrderStatus;
    private BigDecimal itemAmount;
    private Timestamp orderTime;
}
