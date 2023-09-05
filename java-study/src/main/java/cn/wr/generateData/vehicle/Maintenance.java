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
public class Maintenance {

    private Integer maintenanceId;
    private Integer vehicleId;
    private Integer maintenanceType;
    private String description;
    private Timestamp createTime;
    private Timestamp updateTime;

}
