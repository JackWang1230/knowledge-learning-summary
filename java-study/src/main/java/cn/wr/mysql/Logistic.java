package cn.wr.mysql;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author : WangRui
 * @date : 2023/3/10
 */

@Data
@Getter
@Setter
public class Logistic {

    private String logisticsId;
    private String orderId;
    private String status;
    private Timestamp updateTime;
}
