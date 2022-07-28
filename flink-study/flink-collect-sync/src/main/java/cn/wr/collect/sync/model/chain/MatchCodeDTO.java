package cn.wr.collect.sync.model.chain;

import lombok.Data;

@Data
public class MatchCodeDTO {
    /**
     * skuNo 格式： 连锁ID-商品内码
     */
    private String skuNo;
    /**
     * 条码
     */
    private String barcode;
}
