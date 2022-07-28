package cn.wr.collect.sync.constants;

import java.util.Arrays;
import java.util.Objects;

public enum ChainDbTypeEnum {
    Normal(1), // 普通
    Single(2); // 单体

    public Integer type;

    ChainDbTypeEnum(Integer type) {
        this.type = type;
    }

    /**
     * 获取数据
     * @param type
     * @return
     */
    public static ChainDbTypeEnum get(Integer type) {
        if (Objects.isNull(type)) {
            return null;
        }

        return Arrays.stream(ChainDbTypeEnum.values()).filter(val -> val.type.equals(type)).findFirst().orElse(null);
    }
}
