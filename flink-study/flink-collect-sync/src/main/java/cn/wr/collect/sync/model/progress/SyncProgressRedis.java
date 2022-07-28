package cn.wr.collect.sync.model.progress;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SyncProgressRedis {
    /**
     * 需要同步总数量（SQL计算获取）
     */
    private Integer totalCnt;
    /**
     * 当前已同步数量（实时变更）
     */
    private Integer syncCnt;
    /**
     * 开始同步时间
     */
    private LocalDateTime beginTime;
}
