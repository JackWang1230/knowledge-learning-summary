package cn.wr.collect.sync.model;

import cn.wr.collect.sync.common.CustomLocalDateTimeDeserializer;
import cn.wr.collect.sync.common.CustomLocalDateTimeSerializer;
import cn.wr.collect.sync.constants.EsFieldConst;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElasticO2O implements Model, Cloneable {
    private static final long serialVersionUID = 6200953681723613603L;
    private static final Logger log = LoggerFactory.getLogger(ElasticO2O.class);

    @JSONField(serialize = false)
    private Long id;
    /**
     * SPU编码
     */
    /*@JsonProperty("goods_no")
    @JSONField(name = EsFieldConst.goods_no")*/
    /*@JSONField(serialize = false)
    private String goodsNo;*/

    /**
     * ES同步时间
     */
    @JsonProperty(EsFieldConst.sync_date)
    @JSONField(name = EsFieldConst.sync_date)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime syncDate;

    /**
     * 商户ID+门店ID+渠道+货号
     * merchant_id store_id channel internal_id
     */
    @JsonProperty(EsFieldConst.sku_code)
    @JSONField(name = EsFieldConst.sku_code)
    private String skuCode;

    /**
     * 批准文号
     */
    @JsonProperty(EsFieldConst.approval_number)
    @JSONField(name = EsFieldConst.approval_number)
    private String approvalNumber;

    /**
     * 销售价格
     */
    @JsonProperty(EsFieldConst.sale_price)
    @JSONField(name = EsFieldConst.sale_price)
    private BigDecimal salePrice;

    /**
     * 基础价格
     */
    @JsonProperty(EsFieldConst.base_price)
    @JSONField(name = EsFieldConst.base_price)
    private BigDecimal basePrice;

    /**
     * 价格
     * null
     */
    /*@JSONField(serialize = false)
    private BigDecimal price;*/

    /**
     * 货号
     */
    @JsonProperty(EsFieldConst.goods_internal_id)
    @JSONField(name = EsFieldConst.goods_internal_id)
    private String goodsInternalId;

    /**
     * 1上架 0下架
     */
    /*@JsonProperty(EsFieldConst.state)
    @JSONField(name = EsFieldConst.state)*/
    /*@JSONField(serialize = false)
    private String state;*/

    /**
     * 条形码
     */
    @JsonProperty(EsFieldConst.trade_code)
    @JSONField(name = EsFieldConst.trade_code)
    private String tradeCode;

    /**
     * 真实条形码
     */
    @JsonProperty(EsFieldConst.real_trade_code)
    @JSONField(name = EsFieldConst.real_trade_code)
    private String realTradeCode;

    /**
     * 商品名称
     */
    @JsonProperty(EsFieldConst.common_name)
    @JSONField(name = EsFieldConst.common_name)
    private String commonName;

    /**
     * 规格
     */
    @JsonProperty(EsFieldConst.form)
    @JSONField(name = EsFieldConst.form)
    private String form;

    /**
     * 商品类型
     */
    /*@JSONField(serialize = false)
    private String goodsType;*/
    /**
     * 商品子类型
     */
    /*@JSONField(serialize = false)
    private String goodsSubType;*/

    /**
     * 渠道
     */
    @JsonProperty(EsFieldConst.channel)
    @JSONField(name = EsFieldConst.channel)
    private String channel;

    /**
     * 是否销售 0否 1是
     */
    @JsonProperty(EsFieldConst.is_off_shelf)
    @JSONField(name = EsFieldConst.is_off_shelf)
    private Boolean isOffShelf;

    /**
     * 写入 tidb gc_sales_sku_goods 缓存字段
     */
    @JSONField(serialize = false)
    private String isOffShelfSsg; // gc_sales_sku_goods

    /**
     * 商户ID
     */
    @JsonProperty(EsFieldConst.merchant_id)
    @JSONField(name = EsFieldConst.merchant_id)
    private Integer merchantId;

    /**
     * 门店ID
     */
    @JsonProperty(EsFieldConst.store_id)
    @JSONField(name = EsFieldConst.store_id)
    private Integer storeId;

    /**
     * 经纬度
     */
    @JsonProperty(EsFieldConst.location)
    @JSONField(name = EsFieldConst.location)
    private String location;

    /**
     * 厂家
     */
    @JsonProperty(EsFieldConst.manufacturer)
    @JSONField(name = EsFieldConst.manufacturer)
    private String manufacturer;

    /**
     * 分类CODE
     */
    /*@JsonProperty(EsFieldConst.category_one)
    @JSONField(name = EsFieldConst.category_one)*/
    /*@JSONField(serialize = false)
    private String categoryOne;*/

    /**
     * 分类CODE
     */
    /*@JsonProperty(EsFieldConst.category_two)
    @JSONField(name = EsFieldConst.category_two)*/
    /*@JSONField(serialize = false)
    private String categoryTwo;*/

    /**
     * 分类CODE
     */
    /*@JsonProperty(EsFieldConst.category_three)
    @JSONField(name = EsFieldConst.category_three)*/
    /*@JSONField(serialize = false)
    private String categoryThree;*/

    /**
     * 分类CODE
     */
    /*@JsonProperty(EsFieldConst.category_four)
    @JSONField(name = EsFieldConst.category_four)*/
    /*@JSONField(serialize = false)
    private String categoryFour;*/

    /**
     * 分类CODE
     */
    /*@JsonProperty(EsFieldConst.category_five)
    @JSONField(name = EsFieldConst.category_five)*/
    /*@JSONField(serialize = false)
    private String categoryFive;*/

    /**
     * 是否批准 false/true
     */
    @JsonProperty(EsFieldConst.is_standard)
    @JSONField(name = EsFieldConst.is_standard)
    private Boolean isStandard;

    /**
     * 英文名
     */
    /*@JsonProperty(EsFieldConst.en_name)
    @JSONField(name = EsFieldConst.en_name)*/
    /*@JSONField(serialize = false)
    private String enName;*/

    /**
     * 汉语拼音名称
     */
    /*@JsonProperty(EsFieldConst.pinyin_name)
    @JSONField(name = EsFieldConst.pinyin_name)*/
    /*@JSONField(serialize = false)
    private String pinyinName;*/

    /**
     * 有效成分含量
     */
    @JsonProperty(EsFieldConst.indications)
    @JSONField(name = EsFieldConst.indications)
    private String indications;

    /**
     * 功能主治
     */
    /*@JsonProperty(EsFieldConst.cure_disease)
    @JSONField(name = EsFieldConst.cure_disease)*/
    /*@JSONField(serialize = false)
    private String cureDisease;*/

    /**
     * 儿童用药
     */
    /*@JsonProperty(EsFieldConst.pediatric_use)
    @JSONField(name = EsFieldConst.pediatric_use)*/
   /* @JSONField(serialize = false)
    private String pediatricUse;*/

    /**
     * 老年用药
     */
    /*@JsonProperty(EsFieldConst.geriatric_use)
    @JSONField(name = EsFieldConst.geriatric_use)*/
    /*@JSONField(serialize = false)
    private String geriatricUse;*/

    /**
     * 孕妇及哺乳期妇女用药
     */
    /*@JsonProperty(EsFieldConst.pregnancy_and_nursing_mothers)
    @JSONField(name = EsFieldConst.pregnancy_and_nursing_mothers)*/
    /*@JSONField(serialize = false)
    private String pregnancyAndNursingMothers;*/

    /**
     * 药物过量
     */
    /*@JsonProperty(EsFieldConst.over_dosage)
    @JSONField(name = EsFieldConst.over_dosage)*/
    /*@JSONField(serialize = false)
    private String overDosage;*/

    /**
     * 商品名称
     */
    @JsonProperty(EsFieldConst.drug_name)
    @JSONField(name = EsFieldConst.drug_name)
    private String drugName;

    /**
     * 相关疾病
     */
    @JsonProperty(EsFieldConst.relative_sickness)
    @JSONField(name = EsFieldConst.relative_sickness)
    private String relativeSickness;

    /**
     * 药品类型：处方|非处方
     */
    /*@JsonProperty(EsFieldConst.drug_type)
    @JSONField(name = EsFieldConst.drug_type)*/
    /*@JSONField(serialize = false)
    private Integer drugType;*/

    /**
     * otc类型  0:非处方  1:处方药
     */
    @JsonProperty(EsFieldConst.is_prescription)
    @JSONField(name = EsFieldConst.is_prescription)
    private Boolean isPrescription;
    /**
     * 是否麻黄碱 0:不含麻黄碱 1:含麻黄碱
     */
    @JsonProperty(EsFieldConst.is_ephedrine)
    @JSONField(name = EsFieldConst.is_ephedrine)
    private Boolean isEphedrine;
    /**
     * 是否双跨
     */
    @JsonProperty(EsFieldConst.is_double)
    @JSONField(name = EsFieldConst.is_double)
    private Boolean isDouble;

    /**
     * 图片列表
     */
    @JsonProperty(EsFieldConst.img)
    @JSONField(name = EsFieldConst.img)
    private String img;

    /**
     * 销售总量
     */
    @JsonProperty(EsFieldConst.sales_volume)
    @JSONField(name = EsFieldConst.sales_volume)
    private BigDecimal salesVolume;

    /*@JsonProperty(EsFieldConst.priority)
    @JSONField(name = EsFieldConst.priority)
    private Integer priority;*/

    /**
     * v6 分类
     */
    /*@JsonProperty(EsFieldConst.category_frontend)
    @JSONField(name = EsFieldConst.category_frontend)*/
    /*@JSONField(serialize = false)
    private String categoryFrontend;*/

    /**
     * v6 包装
     */
    @JsonProperty(EsFieldConst.pack)
    @JSONField(name = EsFieldConst.pack)
    private String pack;

    /*@JsonProperty(EsFieldConst.geohash)
    @JSONField(name = EsFieldConst.geohash)*/
    /*@JSONField(serialize = false)
    private String geohash;*/

    /**
     * 品牌
     */
    @JsonProperty(EsFieldConst.brand)
    @JSONField(name = EsFieldConst.brand)
    private String brand;

    /**
     * es中增加运营上下架字段（is_wr_off_shelf）,
     * 同步O2O运营配置的状态字段。默认 false 上架
     */
    @JsonProperty(EsFieldConst.is_wr_off_shelf)
    @JSONField(name = EsFieldConst.is_wr_off_shelf)
    private Boolean iswrOffShelf;

    /**
     * 省code
     */
    @JsonProperty(EsFieldConst.province_code)
    @JSONField(name = EsFieldConst.province_code)
    private String provinceCode;
    /**
     * 省名称
     */
    @JsonProperty(EsFieldConst.province_name)
    @JSONField(name = EsFieldConst.province_name)
    private String provinceName;
    /**
     * 市code
     */
    @JsonProperty(EsFieldConst.city_code)
    @JSONField(name = EsFieldConst.city_code)
    private String cityCode;
    /**
     * 市名称
     */
    @JsonProperty(EsFieldConst.city_name)
    @JSONField(name = EsFieldConst.city_name)
    private String cityName;
    /**
     * 区code
     */
    @JsonProperty(EsFieldConst.area_code)
    @JSONField(name = EsFieldConst.area_code)
    private String areaCode;
    /**
     * 区名称
     */
    @JsonProperty(EsFieldConst.area_name)
    @JSONField(name = EsFieldConst.area_name)
    private String areaName;

    /**
     * 连锁商品名称
     */
    @JsonProperty(EsFieldConst.real_common_name)
    @JSONField(name = EsFieldConst.real_common_name)
    private String realCommonName;

    /**
     * 门店状态
     */
    @JsonProperty(EsFieldConst.store_status)
    @JSONField(name = EsFieldConst.store_status)
    private Integer storeStatus;

    /**
     * 是否DTP商品  0：否 1：是
     */
    @JsonProperty(EsFieldConst.is_dtp)
    @JSONField(name = EsFieldConst.is_dtp)
    private Integer isDtp;

    /**
     * 是否DTP门店 0：否 1：是
     */
    @JsonProperty(EsFieldConst.is_dtp_store)
    @JSONField(name = EsFieldConst.is_dtp_store)
    private Integer isDtpStore;

    /**
     * 是否超重（0-否 1-是）
     */
    @JsonProperty(EsFieldConst.is_overweight)
    @JSONField(name = EsFieldConst.is_overweight)
    private Integer isOverweight;

    /**
     * 搜索热词
     */
    @JsonProperty(EsFieldConst.search_keywords)
    @JSONField(name = EsFieldConst.search_keywords)
    private String searchKeywords;

    /**
     * 全连锁销量
     */
    @JsonProperty(EsFieldConst.full_sales_volume)
    @JSONField(name = EsFieldConst.full_sales_volume)
    private BigDecimal fullSalesVolume;

    /**
     * 助记词
     */
    @JsonProperty(EsFieldConst.spell_word)
    @JSONField(name = EsFieldConst.spell_word)
    private String spellWord;

    /**
     * 标准商品审核状态： true-审核通过 false-其他
     */
    @JsonProperty(EsFieldConst.standard_goods_status)
    @JSONField(name = EsFieldConst.standard_goods_status)
    private Boolean standardGoodsStatus;

    /**
     * 商品销售分类
     */
    /*@JsonProperty(EsFieldConst.cates)
    @JSONField(name = EsFieldConst.cates)
    private List<Long> cates;*/

    /**
     * 商品销售分类
     */
    @JsonProperty(EsFieldConst.cate_ids)
    @JSONField(name = EsFieldConst.cate_ids)
    private List<String> cateIds;

    /**
     * 商品属性分类
     */
    /*@JsonProperty(EsFieldConst.attrs)
    @JSONField(name = EsFieldConst.attrs)
    private List<Long> attrs;*/

    /**
     * 商品属性分类
     */
    @JsonProperty(EsFieldConst.attr_ids)
    @JSONField(name = EsFieldConst.attr_ids)
    private List<String> attrIds;

    /**
     * 商品类别/商品类型
     */
    @JsonProperty(EsFieldConst.goods_type)
    @JSONField(name = EsFieldConst.goods_type)
    private Integer goodsType;

    /**
     * 商品类型定义
     */
    @JsonProperty(EsFieldConst.goods_sub_type)
    @JSONField(name = EsFieldConst.goods_sub_type)
    private Integer goodsSubType;

    /**
     * 商品中心下架标识
     * true:下架 false:上架
     */
    @JsonProperty(EsFieldConst.is_standard_off_shelf)
    @JSONField(name = EsFieldConst.is_standard_off_shelf)
    private Boolean isStandardOffShelf;

    /**
     * 商品中心sku审核状态
     * 0-未审核 1-已审核
     */
    @JsonProperty(EsFieldConst.sku_audit_status)
    @JSONField(name = EsFieldConst.sku_audit_status)
    private Integer skuAuditStatus;

    /**
     * 虚拟库存
     * 1 开启 0关闭
     */
    @JsonProperty(EsFieldConst.is_virtual_stock)
    @JSONField(name = EsFieldConst.is_virtual_stock)
    private Integer isVirtualStock;

    /**
     * 库存数量
     */
    @JsonProperty(EsFieldConst.stock_quantity)
    @JSONField(name = EsFieldConst.stock_quantity)
    private BigDecimal stockQuantity;

    /**
     * 所属分组
     */
    @JSONField(serialize = false)
    private String groupId;
    /**
     * 连锁库id
     */
    @JSONField(serialize = false)
    private Integer dbId;
    /**
     * 类别
     */
    /*@JSONField(serialize = false)
    private String category;*/
    /**
     * 百度是否上线 1:上线
     */
    @JSONField(serialize = false)
    private Integer baiduOnline;// gc_partner_stores_all


    /**
     * 连锁总部上下架
     * true:下架 false:上架
     */
    @JsonProperty(EsFieldConst.control_status)
    @JSONField(name = EsFieldConst.control_status)
    private Boolean controlStatus;

    /*public ElasticO2O(SplitMiddleData middle) {
        // 冗余字段用于回写tidb
//        this.geohash = middle.getGeohash();
//        this.goodsNo = middle.getGoodsNo();
//        this.state = middle.getState();
//        this.tradeCode = middle.getTradeCode();
//        this.goodsType = null == middle.getGoodsType() ? null : String.valueOf(middle.getGoodsType());
//        this.goodsSubType = null == middle.getGoodsSubType() ? null : String.valueOf(middle.getGoodsSubType());
//        this.category = middle.getCategory();

        this.groupId = middle.getGroupId();
        this.dbId = middle.getDbId();
        this.syncDate = middle.getSyncDate();
        this.skuCode = middle.getSkuCode();
        this.approvalNumber = middle.getApprovalNumber();
        this.salePrice = middle.getSalePrice();
        this.basePrice = middle.getBasePrice();
        this.goodsInternalId = middle.getInternalId();
        // 1、索引增加一个real_trade_code字段，填入连锁goods表真实条码
        this.realTradeCode = middle.getTradeCode();
        this.commonName = middle.getCommonName();
        this.form = middle.getForm();
        this.channel = middle.getChannel();
        this.isOffShelfSsg = middle.getIsOffShelfSsg();
        this.isOffShelf = StringUtils.equals("1", middle.getIsOffShelf());
        this.merchantId = middle.getMerchantId();
        this.storeId = middle.getStoreId();
        this.location = middle.getLocation();
        this.manufacturer = middle.getManufacturer();
        this.isStandard = StringUtils.equals("1", middle.getIsStandard());
        this.baiduOnline = middle.getBaiduOnline();
        this.realCommonName = middle.getRealCommonName();
        this.pack = middle.getPack();
    }*/

    @Override
    public ElasticO2O clone() {
        try {
            ElasticO2O clone = (ElasticO2O) super.clone();
            if (CollectionUtils.isNotEmpty(this.attrIds)) {
                clone.setAttrIds(new ArrayList<>(this.attrIds.stream().map(String::valueOf).collect(Collectors.toList())));
            }
            if (CollectionUtils.isNotEmpty(this.cateIds)) {
                clone.setCateIds(new ArrayList<>(this.cateIds.stream().map(String::valueOf).collect(Collectors.toList())));
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            log.error("CloneNotSupportedException: {}", e);
            return null;
        }
    }
}
