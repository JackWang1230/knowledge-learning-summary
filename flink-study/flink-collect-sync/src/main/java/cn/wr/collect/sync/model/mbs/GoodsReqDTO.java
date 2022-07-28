package cn.wr.collect.sync.model.mbs;

import java.io.Serializable;
import java.util.List;

public class GoodsReqDTO implements Serializable {
    private static final long serialVersionUID = 4864682628724946196L;
    /**
     * 数据变更类型 1-增加 2-删除 3-修改
     */
    private Integer changeType;
    /**
     * 变更的sku id 列表
     */
    private List<String> skuIds;

    public GoodsReqDTO() {
    }

    public GoodsReqDTO(Integer changeType, List<String> skuIds) {
        this.changeType = changeType;
        this.skuIds = skuIds;
    }

    public Integer getChangeType() {
        return changeType;
    }

    public void setChangeType(Integer changeType) {
        this.changeType = changeType;
    }

    public List<String> getSkuIds() {
        return skuIds;
    }

    public void setSkuIds(List<String> skuIds) {
        this.skuIds = skuIds;
    }
}
