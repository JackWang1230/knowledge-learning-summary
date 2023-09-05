package cn.wr.generateData.vehicle;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author : WangRui
 * @date : 2023/8/21
 */
@Data
@Getter
@Setter
@Builder
public class FuelConsumeBase {

    private Integer tripId;
    private Integer vehicleId;
    private Double distance;
    private String brand;
    private Timestamp updateTime;

}
