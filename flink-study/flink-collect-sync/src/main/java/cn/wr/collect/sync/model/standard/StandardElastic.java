package cn.wr.collect.sync.model.standard;

import cn.wr.collect.sync.common.CustomLocalDateTimeDeserializer;
import cn.wr.collect.sync.common.CustomLocalDateTimeSerializer;
import cn.wr.collect.sync.constants.EsFieldConst;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StandardElastic implements Serializable {
    private static final long serialVersionUID = 4892702613366676559L;

    @JSONField(serialize = false)
    private Long id;

    /**
     * spuId
     */
    @JSONField(name = "spu_id")
    private Long spuId;

    /**
     * 商品名称
     */
    @JSONField(name = "goods_name")
    private String goodsName;

    /**
     * 主标题
     */
    @JSONField(name = "main_title")
    private String mainTitle;

    /**
     * 副标题
     */
    @JSONField(name = "sub_title")
    private String subTitle;

    /**
     * 条码
     */
    @JSONField(name = "trade_code")
    private String tradeCode;

    /**
     * 规格 标准商品表规格字段
     */
    @JSONField(name = "specification_standard")
    private String specificationStandard;

    /**
     * 规格 说明书表规格字段
     */
    @JSONField(name = "specification_manual")
    private String specificationManual;

    /**
     * 基准价
     */
    @JSONField(name = "retail_price")
    private BigDecimal retailPrice;

    /**
     * 单位
     */
    @JSONField(name = "gross_weight")
    private String grossWeight;

    /**
     * 地区
     */
    @JSONField(name = "origin_place")
    private String originPlace;

    /**
     * 品牌
     */
    @JSONField(name = "brand")
    private String brand;

    /**
     * 商品主类目
     */
    @JSONField(name = "category_id")
    private Long categoryId;

    /**
     * 搜索关键字
     */
    @JSONField(name = "search_keywords")
    private String searchKeywords;

    /**
     * 搜索相关词汇
     */
    @JSONField(name = "search_association_words")
    private String searchAssociationWords;

    /**
     * 亮点
     */
    @JSONField(name = "highlights")
    private String highlights;

    /**
     * 商品详情
     */
    @JSONField(name = "details_code")
    private String detailsCode;

    /**
     * 图片
     */
    @JSONField(name = "urls")
    private String urls;

    /**
     * 状态 0:未审核 1:审核通过 2:审核不通过 3下市
     */
    @JSONField(name = "status")
    private Integer status;

    /**
     * 联合用药说明
     */
    @JSONField(name = "join_remarks")
    private String joinRemarks;

    /**
     * 重量 千克
     */
    @JSONField(name = "weight")
    private BigDecimal weight;

    /**
     * 体积 立方米
     */
    @JSONField(name = "volume")
    private BigDecimal volume;

    /**
     * 通用名
     */
    @JSONField(name = "common_name")
    private String commonName;

    /**
     * 英文名
     */
    @JSONField(name = "en_name")
    private String enName;

    /**
     * 拼音
     */
    @JSONField(name = "pinyin_name")
    private String pinyinName;

    /**
     * 批准文号
     */
    @JSONField(name = "approval_number")
    private String approvalNumber;

    /**
     * 禁忌
     */
    @JSONField(name = "taboo")
    private String taboo;

    /**
     * 相互作用
     */
    @JSONField(name = "interaction")
    private String interaction;

    /**
     * 成分
     */
    @JSONField(name = "composition")
    private String composition;

    /**
     * 药理作用
     */
    @JSONField(name = "pharmacological_effects")
    private String pharmacologicalEffects;

    /**
     * 剂型
     */
    @JSONField(name = "dosage")
    private String dosage;

    /**
     * 临床分类
     */
    @JSONField(name = "clinical_classification")
    private String clinicalClassification;

    /**
     * 治疗疫病
     */
    @JSONField(name = "cure_disease")
    private String cureDisease;

    /**
     * 注意事项
     */
    @JSONField(name = "attentions")
    private String attentions;

    /**
     * 生产厂家
     */
    @JSONField(name = "manufacturer")
    private String manufacturer;

    /**
     * 药物动力学
     */
    @JSONField(name = "pharmacokinetics")
    private String pharmacokinetics;

    /**
     * 存储
     */
    @JSONField(name = "storage")
    private String storage;

    /**
     * 儿童使用
     */
    @JSONField(name = "pediatric_use")
    private String pediatricUse;

    /**
     * 老年使用
     */
    @JSONField(name = "geriatric_use")
    private String geriatricUse;

    /**
     * 怀孕和哺乳期市使用
     */
    @JSONField(name = "pregnancy_and_nursing_mothers")
    private String pregnancyAndNursingMothers;

    /**
     * 用量
     */
    @JSONField(name = "over_dosage")
    private String overDosage;

    /**
     * 有效期
     */
    @JSONField(name = "validity")
    private String validity;

    /**
     * 药物名称
     */
    @JSONField(name = "drug_name")
    private String drugName;

    /**
     * 相关疾病
     */
    @JSONField(name = "relative_sickness")
    private String relativeSickness;

    /**
     * 处方类型 1 处方药 2甲类OTC 3乙类OTC 4非药品 5双轨处方
     */
    @JSONField(name = "prescription_type")
    private String prescriptionType;

    /**
     * 适应症
     */
    @JSONField(name = "indications")
    private String indications;

    /**
     * 药物类型
     */
    @JSONField(name = "drug_type")
    private String drugType;

    /**
     * 包装
     */
    @JSONField(name = "packaging")
    private String packaging;

    /**
     * 副作用
     */
    @JSONField(name = "side_effect")
    private String sideEffect;

    /**
     * 温馨提示
     */
    @JSONField(name = "hint")
    private String hint;

    /**
     * 拼音简拼
     */
    @JSONField(name = "simple_pinyin")
    private String simplePinyin;

    /**
     * 拼音全拼
     */
    @JSONField(name = "full_pinyin")
    private String fullPinyin;

    /**
     * 属性id
     */
    @JSONField(name = "attrs")
    private List<String> attrs;

    /**
     * 分类id
     */
    @JSONField(name = "cates")
    private List<String> cates;

    /**
     * 配伍禁忌
     */
    @JSONField(name = "mutexs")
    private List<ManualMutex> mutexs;

    /**
     * 平台展示价
     */
    @JSONField(name = "middle_price")
    private BigDecimal middlePrice;

    /**
     * 总销量
     */
    @JSONField(name = "history_sales")
    private BigDecimal historySales;

    /**
     * 是否线上  0:线下1:线上
     */
    @JSONField(name = "is_online")
    private Integer isOnline;

    /**
     * 同步时间
     */
    @JSONField(name = "sync_date")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime syncDate;

    /**
     * 是否超重（0-否 1-是）
     */
    @JSONField(name = EsFieldConst.is_overweight)
    private Integer isOverweight;

    /**
     * otc类型  0:非处方  1:处方药
     */
    @JSONField(name = EsFieldConst.is_prescription)
    private Boolean isPrescription;

    /**
     * 是否麻黄碱 0:不含麻黄碱 1:含麻黄碱
     */
    @JSONField(name = EsFieldConst.is_ephedrine)
    private Boolean isEphedrine;
}
