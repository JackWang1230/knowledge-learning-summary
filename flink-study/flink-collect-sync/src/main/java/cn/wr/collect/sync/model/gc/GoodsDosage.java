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
@Table(name = "gc_goods_dosage")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoodsDosage implements Model {

  @Column(name = "id")
  private Long id;

  /**
   * 条形码
   */
  @QueryField(order = 0)
  @Column(name = "trade_code")
  @Correspond(type = Correspond.Type.Key, field = EsFieldConst.trade_code)
  private String tradeCode;

  /**
   * 包装总量
   */
  @Column(name = "pack_total")
  private Double packTotal;

  /**
   * 包装总量单位
   */
  @Column(name = "pack_total_unit")
  private Integer packTotalUnit;

  /**
   * 处方类型
   */
  @Column(name = "prescription_type")
  @Correspond(field = {EsFieldConst.is_prescription}, mode = Correspond.Mode.Multi)
  private Integer prescriptionType;

  /**
   * 单疗程
   */
  @Column(name = "single_course")
  private Double singleCourse;


  /**
   * 单疗程单位
   */
  @Column(name = "single_course_unit")
  private Integer singleCourseUnit;

  /**
   * 用法
   */
  @Column(name = "drug_use")
  private String drugUse;


  /**
   * 标准一次用药量
   */
  @Column(name = "standard_dosage_once")
  private Double standardDosageOnce;

  /**
   * 标准一次用药量单位
   */
  @Column(name = "standard_dosage_once_unit")
  private Integer standardDosageOnceUnit;

  /**
   * 标准一日用量
   */
  @Column(name = "standard_dosage_day")
  private Double standardDosageDay;

  /**
   * 标准一日用量单位
   */
  @Column(name = "standard_dosage_day_unit")
  private Integer standardDosageDayUnit;

  /**
   * 儿童用药类型 1:指定用量 2:遵医嘱 3:禁用
   */
  @Column(name = "pediatric_use_type")
  private Integer pediatricUseType;

  /**
   * 儿童一次用药量
   */
  @Column(name = "pediatric_dosage_once")
  private Double pediatricDosageOnce;

  /**
   * 儿童一次用药量单位
   */
  @Column(name = "pediatric_dosage_once_unit")
  private Integer pediatricDosageOnceUnit;

  /**
   * 儿童一日用量
   */
  @Column(name = "pediatric_dosage_day")
  private Double pediatricDosageDay;

  /**
   * 儿童一日用量单位
   */
  @Column(name = "pediatric_dosage_day_unit")
  private Integer pediatricDosageDayUnit;

  /**
   * 孕妇及哺乳期妇女用药类型 1:指定用量 2:遵医嘱 3:禁用
   */
  @Column(name = "pregnancy_use_type")
  private Integer pregnancyUseType;

  /**
   * 孕妇及哺乳期妇女一次用药量
   */
  @Column(name = "pregnancy_dosage_once")
  private Double pregnancyDosageOnce;

  /**
   * 孕妇及哺乳期妇女一次用药量单位
   */
  @Column(name = "pregnancy_dosage_once_unit")
  private Integer pregnancyDosageOnceUnit;

  /**
   * 孕妇及哺乳期妇女一日用量
   */
  @Column(name = "pregnancy_dosage_day")
  private Double pregnancyDosageDay;

  /**
   * 孕妇及哺乳期妇女一日用量单位
   */
  @Column(name = "pregnancy_dosage_day_unit")
  private Integer pregnancyDosageDayUnit;

  /**
   * 老年人用药类型 1:指定用量 2:遵医嘱 3:禁用
   */
  @Column(name = "geriatric_use_type")
  private Integer geriatricUseType;

  /**
   * 老年人一次用药量
   */
  @Column(name = "geriatric_dosage_once")
  private Double geriatricDosageOnce;

  /**
   * 老年人一次用药量单位
   */
  @Column(name = "geriatric_dosage_once_unit")
  private Integer geriatricDosageOnceUnit;

  /**
   * 老年人一日用量
   */
  @Column(name = "geriatric_dosage_day")
  private Double geriatricDosageDay;

  /**
   * 老年人一日用量单位
   */
  @Column(name = "geriatric_dosage_day_unit")
  private Integer geriatricDosageDayUnit;

  /**
   * 创建时间
   */
  @Column(name = "gmt_created")
  @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  private LocalDateTime gmtCreated;

  /**
   * 更新时间
   */
  @Column(name = "gmt_updated")
  @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  private LocalDateTime gmtUpdated;

}
