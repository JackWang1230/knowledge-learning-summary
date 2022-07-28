package cn.wr.collect.sync.model.progress;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SyncProgress {
    /**
     * 自增主键
     */
    private Long id;
    /**
     * 连锁id
     */
    private Integer merchantId;
    /**
     * 连锁名称
     */
    private String merchantName;
    /**
     * 门店id
     */
    private Integer storeId;
    /**
     * 门店名称
     */
    private String storeName;
    /**
     * 需要同步的商品总数量
     */
    private Integer totalCnt;
    /**
     * 当前门店已同步的商品数量
     */
    private Integer syncCnt;
    /**
     * 开始同步时间
     */
    private LocalDateTime beginTime;
    /**
     * 通知类型（0-不通知，1-0%，2-50%，3-100%，4-已通知）默认0
     */
    private Integer noticeType;
    /**
     * 创建时间
     */
    private LocalDateTime gmtCreated;
    /**
     * 更新时间
     */
    private LocalDateTime gmtUpdated;
}
