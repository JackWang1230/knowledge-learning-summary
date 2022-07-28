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
@Table(name = "gc_goods_manual")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoodsManual implements Model {

  private static final long serialVersionUID = -7502419481422305403L;
  @Column(name = "id")
  private Long id;
  @Correspond(mode = Correspond.Mode.Multi, field = EsFieldConst.common_name)
  @Column(name = "common_name")
  private String commonName;
  @Column(name = "en_name")
  /*@Correspond(field = {EsFieldConst.en_name})*/
  private String enName;
  @Column(name = "pinyin_name")
  /*@Correspond(field = {EsFieldConst.pinyin_name})*/
  private String pinyinName;
  @QueryField
  @Correspond(type = Correspond.Type.Key, field = EsFieldConst.approval_number)
  @Column(name = "approval_number")
  private String approvalNumber;
  @Column(name = "taboo")
  private String taboo;
  @Column(name = "interaction")
  private String interaction;
  @Column(name = "composition")
  private String composition;
  @Column(name = "pharmacological_effects")
  private String pharmacologicalEffects;
  @Column(name = "dosage")
  private String dosage;
  @Column(name = "clinical_classification")
  private String clinicalClassification;
  @Column(name = "cure_disease")
  /*@Correspond(field = {EsFieldConst.cure_disease})*/
  private String cureDisease;
  @Column(name = "attentions")
  private String attentions;
  @Column(name = "manufacturer")
  private String manufacturer;
  @Column(name = "specification")
  private String specification;
  @Column(name = "pharmacokinetics")
  private String pharmacokinetics;
  @Column(name = "storage")
  private String storage;
  @Column(name = "pediatric_use")
  /*@Correspond(field = {EsFieldConst.pediatric_use})*/
  private String pediatricUse;
  @Column(name = "geriatric_use")
  /*@Correspond(field = {EsFieldConst.geriatric_use})*/
  private String geriatricUse;
  @Column(name = "pregnancy_and_nursing_mothers")
  /*@Correspond(field = {EsFieldConst.pregnancy_and_nursing_mothers})*/
  private String pregnancyAndNursingMothers;
  @Column(name = "over_dosage")
  /*@Correspond(field = {EsFieldConst.over_dosage})*/
  private String overDosage;
  @Column(name = "validity")
  private String validity;
  @Column(name = "drug_name")
  @Correspond(field = {EsFieldConst.drug_name})
  private String drugName;
  @Column(name = "relative_sickness")
  @Correspond(field = {EsFieldConst.relative_sickness})
  private String relativeSickness;
  @Column(name = "prescription_type")
  private Integer prescriptionType;
  @Column(name = "indications")
  @Correspond(field = {EsFieldConst.indications})
  private String indications;
  @Column(name = "drug_type")
  private String drugType;
  @Column(name = "packaging")
  private String packaging;
  @Column(name = "gmtCreated")
  @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  private LocalDateTime gmtCreated;
  @Column(name = "gmtUpdated")
  @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  private LocalDateTime gmtUpdated;

}
