package cn.wr.collect.sync.model;

import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class BasicTimeStoreGoods extends PartnerStoreGoods {
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

    public BasicTimeStoreGoods transfer(PartnerStoreGoods storeGoods, PgConcatParams concatParams) {
        this.setId(storeGoods.getId());
        this.setDbId(storeGoods.getDbId());
        this.setTableId(storeGoods.getTableId());
        this.setGoodsInternalId(storeGoods.getGoodsInternalId());
        this.setGroupId(storeGoods.getGroupId());
        this.setPrice(storeGoods.getPrice());
        this.setStatus(storeGoods.getStatus());
        this.setCreatedAt(storeGoods.getCreatedAt());
        this.setUpdatedAt(storeGoods.getUpdatedAt());
        this.setGmtcreated(storeGoods.getGmtcreated());
        this.setGmtupdated(storeGoods.getGmtupdated());
        this.setMemberPrice(storeGoods.getMemberPrice());
        this.setMerchantId(StringUtils.isNotBlank(concatParams.getMerchantId()) ? Integer.valueOf(concatParams.getMerchantId()) : null);
        this.setStoreId(StringUtils.isNotBlank(concatParams.getStoreId()) ? Integer.valueOf(concatParams.getStoreId()) : null);
        this.setLocation(concatParams.getLocation());
        return this;
    }

}
