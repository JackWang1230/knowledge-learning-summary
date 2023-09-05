package cn.wr.generateData.vehicle;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author : WangRui
 * @date : 2023/8/8
 */
@Data
@Getter
@Setter
@Builder
public class Alert {

    private Integer alertId;
    private Integer vehicleId;
    private Integer alertType;
    private Long timeStamp;
    private Timestamp createTime;
    private Timestamp updateTime;
}
