package cn.wr.generateData.vehicle;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author : WangRui
 * @date : 2023/8/11
 */

@Data
@Setter
@Getter
@Builder
public class Trip {

    private Integer tripId;
    private Integer vehicleId;
    private Integer driverId;
    private Timestamp startTime;
    private Timestamp endTime;
    private Double distance;
    private Timestamp createTime;
    private Timestamp updateTime;

}
