package cn.wr.collect.sync.constants;

import java.util.Arrays;
import java.util.Objects;

/**
 * 同步类型
 */
public enum SyncTypeEnum {
    PART(0), // 部分字段同步
    FULL(1); // 全量字段同步

    /**
     * code
     */
    public int code;

    SyncTypeEnum(int code) {
        this.code = code;
    }

    public static SyncTypeEnum get(Integer code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return Arrays.stream(SyncTypeEnum.values()).filter(sync -> sync.code == code).findFirst().orElse(null);
    }
}
