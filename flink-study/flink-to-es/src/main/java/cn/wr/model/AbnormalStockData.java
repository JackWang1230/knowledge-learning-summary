package cn.wr.model;

import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author RWang
 * @Date 2022/8/9
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbnormalStockData extends BaseStockData{
    private static final long serialVersionUID = -7768180754152230906L;

    /** 2 代表库存 3 代表上下架*/
    private int isStockOrStatus;
}
