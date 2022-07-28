package cn.wr.collect.sync.model;

import cn.wr.collect.sync.common.CustomLocalDateTimeDeserializer;
import cn.wr.collect.sync.common.CustomLocalDateTimeSerializer;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElasticSearchKeywords implements Serializable {
    private static final long serialVersionUID = 5275488855376066086L;

    @JsonProperty("id")
    @JSONField(name = "id")
    private String id;
    @JsonProperty("word")
    @JSONField(name = "word")
    private String word;
    @JsonProperty("weight")
    @JSONField(name = "weight")
    private Double weight;

    @JsonProperty("@timestamp")
    @JSONField(name = "@timestamp")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime timestamp;
}
