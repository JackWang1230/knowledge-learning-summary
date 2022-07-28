package cn.wr.collect.sync.model;

import cn.wr.collect.sync.model.partner.PartnerGoods;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class BasicTimeGoods extends PartnerGoods {
    private static final long serialVersionUID = -6742406228147134738L;
    /**
     * 连锁id
     */
    private Integer merchantId;
    /**
     * 门店id
     */
    private Integer storeId;

    /**
     * 经纬度
     */
    private String location;

    public BasicTimeGoods transfer(PartnerGoods goods, PgConcatParams concatParams) {
        this.setId(goods.getId());
        this.setDbId(goods.getDbId());
        this.setTableId(goods.getTableId());
        this.setInternalId(goods.getInternalId());
        this.setCommonName(goods.getCommonName());
        this.setTradeCode(goods.getTradeCode());
        this.setApprovalNumber(goods.getApprovalNumber());
        this.setForm(goods.getForm());
        this.setPack(goods.getPack());
        this.setPrice(goods.getPrice());
        this.setManufacturer(goods.getManufacturer());
        this.setStatus(goods.getStatus());
        this.setGoodsCreateTime(goods.getGoodsCreateTime());
        this.setGoodsUpdateTime(goods.getGoodsUpdateTime());
        this.setCreatedAt(goods.getCreatedAt());
        this.setUpdatedAt(goods.getUpdatedAt());
        this.setGmtcreated(goods.getGmtcreated());
        this.setGmtupdated(goods.getGmtupdated());
        this.setMerchantId(StringUtils.isNotBlank(concatParams.getMerchantId()) ? Integer.valueOf(concatParams.getMerchantId()) : null);
        this.setStoreId(StringUtils.isNotBlank(concatParams.getStoreId()) ? Integer.valueOf(concatParams.getStoreId()) : null);
        this.setLocation(concatParams.getLocation());
        return this;
    }

}
