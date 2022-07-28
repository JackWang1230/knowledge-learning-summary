package cn.wr.collect.sync.model.partner;

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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table(name = "partner_goods_info")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerGoodsInfo implements Model {
    private static final long serialVersionUID = -4142458629858526661L;

    @Column(name = "id")
    private Long id;
    @QueryField
    @Column(name = "db_id")
    @Correspond(type = Correspond.Type.Key)
    private Integer dbId;
    @Column(name = "table_id")
    private Integer tableId;
    @QueryField(order = 1)
    @Column(name = "internal_id")
    @Correspond(type = Correspond.Type.Key, field = EsFieldConst.goods_internal_id)
    private String internalId;
    @Column(name = "trade_code")
    private Long tradeCode;
    @Column(name = "approval_number")
    private String approvalNumber;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "trade_name")
    private String tradeName;
    @Column(name = "common_name")
    private String commonName;
    @Column(name = "english_name")
    private String englishName;
    @Column(name = "manufacturer")
    private String manufacturer;
    @Column(name = "otc")
    private String otc;
    @Column(name = "medical_insurance")
    private String medicalInsurance;
    @Column(name = "storage")
    private String storage;
    @Column(name = "appearance")
    private String appearance;
    @Column(name = "diseases")
    private String diseases;
    @Column(name = "drug_form")
    private String drugForm;
    @Column(name = "spec")
    private String spec;
    @Column(name = "sku")
    private String sku;
    @Column(name = "package")
    private String packagee;
    @Column(name = "validity_period")
    private String validityPeriod;
    @Column(name = "expiry_date")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime expiryDate;
    @Column(name = "component")
    private String component;
    @Column(name = "content")
    private String content;
    @Column(name = "interaction")
    private String interaction;
    @Column(name = "images")
    @Correspond(field = {EsFieldConst.img}, mode = Correspond.Mode.Multi)
    private String images;
    @Column(name = "indications")
    private String indications;
    @Column(name = "dosage")
    private String dosage;
    @Column(name = "pharmacological")
    private String pharmacological;
    @Column(name = "contraindication")
    private String contraindication;
    @Column(name = "caution")
    private String caution;
    @Column(name = "adverse_reaction")
    private String adverseReaction;
    @Column(name = "excess_reaction")
    private String excessReaction;
    @Column(name = "child_effect")
    private String childEffect;
    @Column(name = "elder_effect")
    private String elderEffect;
    @Column(name = "pregnant_effect")
    private String pregnantEffect;
    @Column(name = "warning")
    private String warning;
    @Column(name = "category")
    private String category;
    @Column(name = "created_at")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;
    @Column(name = "gmtcreated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtcreated;
    @Column(name = "gmtupdated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtupdated;

}
