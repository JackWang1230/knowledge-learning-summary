package cn.wr.generateData.eCommerce;

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
public class Payment {

    private String paymentId;
    private String orderId;
    private Timestamp paymentTime;
    private String paymentMethod;
    private BigDecimal payAmount;
}
