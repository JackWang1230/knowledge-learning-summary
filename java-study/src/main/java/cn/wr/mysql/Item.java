package cn.wr.mysql;

import lombok.Builder;
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
@Builder
public class Item {

    private String itemId;
    private String itemName;
    private int category;
    private BigDecimal price;
    private Timestamp createTime;

}
