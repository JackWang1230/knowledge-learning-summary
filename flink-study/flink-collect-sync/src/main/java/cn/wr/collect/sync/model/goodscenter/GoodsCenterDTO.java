package cn.wr.collect.sync.model.goodscenter;

import cn.wr.collect.sync.common.CustomLocalDateTimeDeserializer;
import cn.wr.collect.sync.common.CustomLocalDateTimeSerializer;
import cn.wr.collect.sync.model.gc.PartnerStoresAll;
import cn.wr.collect.sync.model.gc.PgcMerchantInfo;
import cn.wr.collect.sync.model.middledb.PgcStoreInfoShort;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.model.redis.DbMerchant;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoodsCenterDTO implements Serializable {
    private static final long serialVersionUID = -5980271918532998584L;
    /**
     * 自增长主键
     */
    private Long id;
    /**
     * 此字段为原表的db编号，和chain_partners的dbid关联
     */
    private Integer dbId;
    /**
     * 连锁ID
     */
    private Integer merchantId;
    /**
     * 连锁名称
     */
    private String merchantName;
    /**
     * 原表自增 ID
     */
    private Integer tableId;
    /**
     * 连锁商品内码
     */
    private String internalId;
    /**
     * 药品名称
     */
    private String commonName;
    /**
     * 商品的条形码
     */
    private String tradeCode;
    /**
     * 批准文号
     */
    private String approvalNumber;
    /**
     * 规格，一个规格对应一个条形码
     */
    private String form;
    /**
     * 包装
     */
    private String pack;
    /**
     * 兼容没有区域价格的零售价格
     */
    private BigDecimal price;
    /**
     * 厂商
     */
    private String manufacturer;
    /**
     * 药品是否有效
     */
    private Integer status;
    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtcreated;
    /**
     * 更新时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime gmtupdated;
    /**
     * 操作类型：I-新增 U-更新 D-删除
     */
    private String operate;

    /**
     * 参数转换
     * @param pg
     * @param dbMerchant
     * @param operate
     * @return
     */
    public GoodsCenterDTO convert(PartnerGoods pg, DbMerchant dbMerchant, String operate) {
        this.setId(pg.getId());
        this.setDbId(pg.getDbId());
        this.setMerchantId(dbMerchant.getMerchantId());
        this.setMerchantName(dbMerchant.getMerchantName());
        this.setTableId(pg.getTableId());
        this.setInternalId(pg.getInternalId());
        this.setCommonName(pg.getCommonName());
        this.setTradeCode(pg.getTradeCode());
        this.setApprovalNumber(pg.getApprovalNumber());
        this.setForm(pg.getForm());
        this.setPack(pg.getPack());
        this.setPrice(pg.getPrice());
        this.setManufacturer(pg.getManufacturer());
        this.setStatus(pg.getStatus());
        this.setGmtcreated(pg.getCreatedAt());
        this.setGmtupdated(pg.getGmtupdated());
        this.setOperate(operate);
        return this;
    }

    /**
     * 参数转换
     * @param pg
     * @param dbMerchant
     * @param operate
     * @return
     */
    public GoodsCenterDTO convert(PartnerGoods pg, Integer merchantId, String merchantName, String operate) {
        this.setId(pg.getId());
        this.setDbId(pg.getDbId());
        this.setMerchantId(merchantId);
        this.setMerchantName(merchantName);
        this.setTableId(pg.getTableId());
        this.setInternalId(pg.getInternalId());
        this.setCommonName(pg.getCommonName());
        this.setTradeCode(pg.getTradeCode());
        this.setApprovalNumber(pg.getApprovalNumber());
        this.setForm(pg.getForm());
        this.setPack(pg.getPack());
        this.setPrice(pg.getPrice());
        this.setManufacturer(pg.getManufacturer());
        this.setStatus(pg.getStatus());
        this.setGmtcreated(pg.getCreatedAt());
        this.setGmtupdated(pg.getGmtupdated());
        this.setOperate(operate);
        return this;
    }
}
