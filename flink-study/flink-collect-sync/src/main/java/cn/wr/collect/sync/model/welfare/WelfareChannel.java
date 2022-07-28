package cn.wr.collect.sync.model.welfare;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class WelfareChannel implements Serializable {
    private static final long serialVersionUID = -4975432688646171613L;

    /**
     * 渠道
     */
    private Integer channel;
    /**
     * 价格
     */
    private BigDecimal salePrice;
    /**
     * 购买链接
     */
    private String buyLink;
    /**
     * 销售状态（0：在售，1：下架）
     */
    private Integer saleState;

    /**
     * 状态 0 正常 1停用
     */
    private RedisGoodsDetail redisGoodsDetails;


}
