package cn.wr.model;

import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author RWang
 * @Date 2022/5/16
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GcDisableJudge extends BaseJudge{

    @JsonProperty("store_id")
    private long storeId;

    @JsonProperty("internal_id")
    private String internalId;

}
