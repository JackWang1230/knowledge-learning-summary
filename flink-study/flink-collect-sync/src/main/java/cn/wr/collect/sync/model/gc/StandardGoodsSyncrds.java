package cn.wr.collect.sync.model.gc;


import cn.wr.collect.sync.common.CustomLocalDateTimeDeserializer;
import cn.wr.collect.sync.common.CustomLocalDateTimeSerializer;
import cn.wr.collect.sync.constants.EsFieldConst;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Correspond;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.utils.ResultSetConvert;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.sql.ResultSet;
import java.time.LocalDateTime;

@Data
@Table(name = "gc_standard_goods_syncrds")
@JsonIgnoreProperties(ignoreUnknown = true)
public class StandardGoodsSyncrds implements Model {

    private static final long serialVersionUID = -5995632066719035306L;
    @Correspond(field = {EsFieldConst.is_ephedrine, EsFieldConst.is_double, EsFieldConst.is_standard,
            EsFieldConst.attr_ids, EsFieldConst.cate_ids, EsFieldConst.trade_code},
            mode = Correspond.Mode.Multi)
    @Column(name = "id")
    private Long id;

    @Column(name = "spu_id")
    @Correspond(field = {EsFieldConst.indications, EsFieldConst.drug_name, EsFieldConst.relative_sickness,
            EsFieldConst.common_name, EsFieldConst.is_prescription, EsFieldConst.img, EsFieldConst.approval_number},
            mode = Correspond.Mode.Multi)
    private Long spuId;

    @Column(name = "goods_name")
    private String goodsName;

    @QueryField
    @Column(name = "trade_code")
    @Correspond(type = Correspond.Type.Key, field = EsFieldConst.trade_code)
    private String tradeCode;

    @Column(name = "specification")
    private String specification;

    @Column(name = "retail_price")
    private Double retailPrice;

    @Column(name = "gross_weight")
    private String grossWeight;

    @Column(name = "origin_place")
    private String originPlace;

    @Column(name = "brand")
    @Correspond(field = {EsFieldConst.brand})
    private String brand;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "search_keywords")
    @Correspond(field = {EsFieldConst.search_keywords})
    private String searchKeywords;

    @Column(name = "search_association_words")
    private String searchAssociationWords;

    @Column(name = "sub_title")
    private String subTitle;

    @Column(name = "highlights")
    private String highlights;

    // @QueryField
    @Column(name = "details_code")
    private String detailsCode;

    @Column(name = "urls")
    @Correspond(field = EsFieldConst.img, mode = Correspond.Mode.Multi)
    private String urls;

    @Correspond(field = EsFieldConst.standard_goods_status)
    @Column(name = "status")
    private Integer status;

    @Column(name = "gmtCreated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtCreated;

    @Column(name = "gmtUpdated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtUpdated;

    /**
     * jdbc 结果转换
     * @param rs
     * @return
     * @throws Exception
     */
    public StandardGoodsSyncrds convert(ResultSet rs) throws Exception {
        // `id`, `spu_id`, `goods_name`, `trade_code`, `specification`, `retail_price`, `gross_weight`, `origin_place`,
        // `brand`, `category_id`, `search_keywords`, `search_association_words`, `sub_title`, `highlights`,
        // `details_code`, `urls`, `status`, `gmtCreated`, `gmtUpdated`
        this.setId(ResultSetConvert.getLong(rs, 1));
        this.setSpuId(ResultSetConvert.getLong(rs, 2));
        this.setGoodsName(ResultSetConvert.getString(rs, 3));
        this.setTradeCode(ResultSetConvert.getString(rs, 4));
        this.setSpecification(ResultSetConvert.getString(rs, 5));
        this.setRetailPrice(ResultSetConvert.getDouble(rs, 6));
        this.setGrossWeight(ResultSetConvert.getString(rs, 7));
        this.setOriginPlace(ResultSetConvert.getString(rs, 8));
        this.setBrand(ResultSetConvert.getString(rs, 9));
        this.setCategoryId(ResultSetConvert.getLong(rs, 10));
        this.setSearchKeywords(ResultSetConvert.getString(rs, 11));
        this.setSearchAssociationWords(ResultSetConvert.getString(rs, 12));
        this.setSubTitle(ResultSetConvert.getString(rs, 13));
        this.setHighlights(ResultSetConvert.getString(rs, 14));
        this.setDetailsCode(ResultSetConvert.getString(rs, 15));
        this.setUrls(ResultSetConvert.getString(rs, 16));
        this.setStatus(ResultSetConvert.getInt(rs, 17));
        this.setGmtCreated(ResultSetConvert.getLocalDateTime(rs, 18));
        this.setGmtUpdated(ResultSetConvert.getLocalDateTime(rs, 19));
        return this;
    }
}
