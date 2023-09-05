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
@Builder
@Getter
@Setter
public class FuelConsumption {

    private Integer fuelId;
    private Integer tripId;
    private Integer vehicleId;
    private String fuelType;
    private Double fuelAmount;
    private Double fuelFee;
    private Timestamp createTime;
    private Timestamp updateTime;
}
