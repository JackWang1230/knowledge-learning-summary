package cn.wr.collect.sync.model.redis;

import lombok.Data;

@Data
public class DbMerchant {
    /**
     * dbId
     */
    private Integer dbId;
    /**
     * merchantId
     */
    private Integer merchantId;
    /**
     * merchantName
     */
    private String merchantName;

    public DbMerchant(Integer dbId, Integer merchantId, String merchantName) {
        this.dbId = dbId;
        this.merchantId = merchantId;
        this.merchantName = merchantName;
    }
}
