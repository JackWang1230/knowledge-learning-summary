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
public class Vehicle {

    private Integer vehicleId;
    private String brand;
    private String model;
    private String licensePlate;
    private String location;
    private Integer fuelLevel;
    private String engineStatus;
    private Timestamp createTime;
    private Timestamp updateTime;

}
