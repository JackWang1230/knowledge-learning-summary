package cn.wr.collect.sync.model.standard;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class ManualMutex {
    /**
     * 批准文号
     */
    @JSONField(name = "approval_number")
    private String approvalNumber;
    /**
     * 互斥类型
     */
    @JSONField(name = "mutex_type")
    private Integer mutexType;

    /**
     * convert
     * @param mutexRef
     * @return
     */
    public ManualMutex convert(RedisMutex.RedisMutexRef mutexRef) {
        this.setApprovalNumber(mutexRef.getApprovalNumber());
        this.setMutexType(mutexRef.getMutexType());
        return this;
    }
}
