package cn.wr.collect.sync.model.middledb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsCenterTime implements Serializable {
    private Integer dbId;
    private Integer merchantId;
    private String goodsInternalId;

}
