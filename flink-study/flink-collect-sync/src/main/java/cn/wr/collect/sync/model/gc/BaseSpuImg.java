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
@Table(name = "gc_base_spu_img")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseSpuImg implements Model {

  private static final long serialVersionUID = -1457958026111131778L;
  @Column(name = "id")
  private Long id;
  @Column(name = "name")
  private String name;
  @QueryField
  @Column(name = "approval_number")
  @Correspond(type = Correspond.Type.Key, field = EsFieldConst.approval_number)
  private String approvalNumber;
  @Column(name = "pic")
  @Correspond(field = {EsFieldConst.img}, mode = Correspond.Mode.Multi)
  private String pic;
  @Column(name = "gmtUpdated")
  @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  private LocalDateTime gmtUpdated;
  @Column(name = "gmtCreated")
  @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  private LocalDateTime gmtCreated;

}
