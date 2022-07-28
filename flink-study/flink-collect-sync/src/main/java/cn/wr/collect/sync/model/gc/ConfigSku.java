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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Data
@Table(name = "gc_config_sku")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigSku implements Model {
    private static final long serialVersionUID = 7626466628547163959L;

    /**
     * id
     */
    @Column(name = "id")
    private Long id;

    /**
     * sku编号(同gc_ug_spu_goods.goods_no)
     * {merchant_id}-{goods_internal_id}
     */
    @QueryField
    @Correspond(type = Correspond.Type.Key)
    @Column(name = "sku_no")
    private String skuNo;

    /**
     * 标准条码
     */
    @Correspond(mode = Correspond.Mode.Multi, field = {EsFieldConst.trade_code})
    @Column(name = "barcode")
    private String barcode;

    /**
     * 批准文号
     */
    @Column(name = "barcode")
    private String approvalNumber;

    /**
     * 商品类别/商品类型
     */
    @Correspond(field = {EsFieldConst.goods_type})
    @Column(name = "goods_type")
    private Integer goodsType;

    /**
     * # 商品类型定义
     * | 主类型                 | 子类型                | 描述            |
     * | :---                | :--------          | ------------   |
     * |1| 11(药品),12(器械),13(化妆品),14(保健品),18(带金商品),19(其他)        |   实物商品        |
     * |2| 21(药联权益)      |  药联权益            |
     * |3| 31(第三方虚拟商品) ,301(体检) ,302(口腔健康) ,303(问诊)  |  第三方健康服务            |
     * |4| 41(药品过期换新服务)   |  药品过期换新服务        |
     * |5| 32(年卡),51(月卡)     |      药联会员资格      |
     * |6| 33(营销卡),61(抵扣卷)   |      药联优惠券      |
     * |7| 71(药联积分商品)                 |      药联积分商品      |
     * |8| 22(打包商品),81(联销商品套餐)  ,82(普通用药服务包) ,83(慢病专用服务包) ,84(DTP专用服务包)                |      组合商品      |
     */
    @Correspond(field = {EsFieldConst.goods_sub_type})
    @Column(name = "goods_sub_type")
    private Integer goodsSubType;

    /**
     * sku状态 0 正常  1 下架
     */
    @Correspond(field = {EsFieldConst.is_standard_off_shelf})
    @Column(name = "state")
    private Integer state;

    /**
     * 审核状态 0未审核 1已审核
     */
    @Correspond(field = {EsFieldConst.sku_audit_status})
    @Column(name = "audit_state")
    private Integer auditState;

    /**
     * 创建时间
     */
    @Column(name = "gmtCreated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtCreated;

    /**
     * 更新时间
     */
    @Column(name = "gmtUpdated")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtUpdated;


    /**
     * 商户id
     */
    @Column(name = "merchant_id")
    private Long merchantId;

    /**
     * 商品名
     */
    @Column(name = "source_name")
    private String sourceName;

    /**
     * 价格
     */
    @Column(name = "origin_price")
    private BigDecimal originPrice;

    /**
     * 规格名称
     */
    @Column(name = "spec_name")
    private String specName;

    /**
     * 参数转换
     * @param rs
     * @return
     * @throws SQLException
     */
    public ConfigSku convert(ResultSet rs) throws SQLException {
        this.setId(ResultSetConvert.getLong(rs, 1));
        this.setSkuNo(ResultSetConvert.getString(rs, 2));
        this.setBarcode(ResultSetConvert.getString(rs, 3));
        this.setApprovalNumber(ResultSetConvert.getString(rs, 4));
        this.setGoodsType(ResultSetConvert.getInt(rs, 5));
        this.setGoodsSubType(ResultSetConvert.getInt(rs, 6));
        this.setState(ResultSetConvert.getInt(rs, 7));
        this.setAuditState(ResultSetConvert.getInt(rs, 8));
        this.setGmtCreated(ResultSetConvert.getLocalDateTime(rs, 9));
        this.setGmtUpdated(ResultSetConvert.getLocalDateTime(rs, 10));
        this.setMerchantId(ResultSetConvert.getLong(rs, 11));
        this.setSourceName(ResultSetConvert.getString(rs, 12));
        this.setOriginPrice(ResultSetConvert.getBigDecimal(rs, 13));
        this.setSpecName(ResultSetConvert.getString(rs, 14));
        return this;
    }
}
