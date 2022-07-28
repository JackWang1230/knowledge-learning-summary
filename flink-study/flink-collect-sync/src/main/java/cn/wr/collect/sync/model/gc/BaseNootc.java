package cn.wr.collect.sync.model.gc;


import cn.wr.collect.sync.common.CustomLocalDateTimeDeserializer;
import cn.wr.collect.sync.common.CustomLocalDateTimeSerializer;
import cn.wr.collect.sync.constants.EsFieldConst;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Correspond;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.annotations.Table;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

@Data
@Table(name = "gc_base_nootc")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseNootc implements Model {

  private static final long serialVersionUID = -7894673201375908722L;
  @Column(name = "id")
  private Long id;
  @QueryField
  @Column(name = "approval_number")
  @Correspond(type = Correspond.Type.Key, field = EsFieldConst.approval_number)
  private String approvalNumber;
  @Column(name = "name")
  private String name;
  @Column(name = "otc_type")
  @Correspond(field = {EsFieldConst.is_prescription}, mode = Correspond.Mode.Multi)
  private Integer otcType;
  @Column(name = "is_ephedrine")
  private Integer isEphedrine;
  @Column(name = "is_double")
  private Integer isDouble;
  @Column(name = "gmtUpdated")
  @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  private LocalDateTime gmtUpdated;
  @Column(name = "gmtCreated")
  @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  private LocalDateTime gmtCreated;

}
