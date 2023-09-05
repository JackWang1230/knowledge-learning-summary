package cn.wr.generateData.vehicle;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : WangRui
 * @date : 2023/8/14
 */
@Data
@Getter
@Setter
@Builder
public class CommonData {

    private Integer id;
    private String brand;
    private Integer vehicleId;
}
