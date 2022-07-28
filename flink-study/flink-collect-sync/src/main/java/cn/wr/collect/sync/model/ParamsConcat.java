package cn.wr.collect.sync.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ParamsConcat implements Serializable {
    private static final long serialVersionUID = -2521518804168425199L;
    /**
     * 数据唯一标识
     */
    private String skuCode;
    /**
     * 基础数据
     */
    private byte[] content;

    public ParamsConcat() {
    }

    public ParamsConcat(String skuCode, byte[] content) {
        this.skuCode = skuCode;
        this.content = content;
    }
}
