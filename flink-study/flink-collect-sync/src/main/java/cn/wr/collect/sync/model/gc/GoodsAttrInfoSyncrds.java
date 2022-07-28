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
@Table(name = "gc_goods_attr_info_syncrds")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoodsAttrInfoSyncrds implements Model {

  private static final long serialVersionUID = 8860030446570357758L;
  @QueryField(order = 0)
  @Correspond(type = Correspond.Type.Both,
          field = {EsFieldConst.is_double, EsFieldConst.is_ephedrine})
  @Column(name = "id")
  private Long id;
  @Column(name = "title")
  private String title;
  @Column(name = "key_word")
  private String keyWord;
  @Column(name = "parent_id")
  private Long parentId;
  @Column(name = "pids")
  @Correspond(mode = Correspond.Mode.Multi, field = {EsFieldConst.attr_ids})
  private String pids;
  @Column(name = "level")
  private Integer level;
  @Column(name = "remarks")
  private String remarks;
  @Column(name = "allow")
  private Integer allow;
  @Column(name = "deleted")
  @Correspond(mode = Correspond.Mode.Multi, field = {EsFieldConst.attr_ids})
  private Integer deleted;
  @Column(name = "gmt_updated")
  @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  private LocalDateTime gmtUpdated;
  @Column(name = "gmt_created")
  @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  private LocalDateTime gmtCreated;
  @Column(name = "creator")
  private String creator;
  @Column(name = "updator")
  private String updator;
  @Column(name = "cate_id")
  private String cateId;

}
