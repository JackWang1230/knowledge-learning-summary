package cn.wr.collect.sync.model.gc;


import cn.wr.collect.sync.constants.EsFieldConst;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Correspond;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.annotations.Table;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Table(name = "merchant_goods_category_mapping")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantGoodsCategoryMapping implements Model {

    private static final long serialVersionUID = 5874704761583990320L;
    @Column(name = "id")
    private Long id;
    // @QueryField(order = 1)
    @Column(name = "internal_id")
    private String internalId;
    @Column(name = "common_name")
    private String commonName;
    @QueryField(order = 0)
    @Correspond(type = Correspond.Type.Key, field = EsFieldConst.trade_code)
    @Column(name = "trade_code")
    private String tradeCode;
    @Column(name = "prescription_category")
    private String prescriptionCategory;
    @Column(name = "merchant_category")
    private String merchantCategory;
    @Column(name = "mhj")
    private String mhj;
//    @Correspond(field = EsFieldConst.category_frontend)
    @Column(name = "category_code")
    private String categoryCode;
    @Column(name = "merchant_id")
    private Long merchantId;

}
