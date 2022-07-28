package cn.wr.collect.sync.model.mbs;

import java.io.Serializable;


public class NewPartnerDTO implements Serializable {
    private static final long serialVersionUID = 1183380106715325324L;
    /**
     * 连锁id
     */
    private Long merchantId;
    /**
     * 总数
     */
    private Integer totalCnt;

    public NewPartnerDTO(Long merchantId, Integer totalCnt) {
        this.merchantId = merchantId;
        this.totalCnt = totalCnt;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(Integer totalCnt) {
        this.totalCnt = totalCnt;
    }
}
