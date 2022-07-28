package cn.wr.collect.sync.constants;

import org.apache.commons.lang3.StringUtils;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;

/**
 * es 枚举
 */
public enum ElasticEnum {
    O2O(ES_INDEX_O2O, ES_DOCUMENT_TYPE_O2O, "2"),
    B2C(ES_INDEX_O2O, ES_DOCUMENT_TYPE_O2O, "4"),
    WELFARE(ES_INDEX_WELFARE, ES_DOCUMENT_TYPE_WELFARE, ""),
    STANDARD(ES_INDEX_STANDARD, ES_DOCUMENT_TYPE_STANDARD, ""),
    ;

    /**
     * 索引
     */
    private String index;
    /**
     * 类型
     */
    private String type;
    /**
     * 对应 gc_partner_stores_all 中channel
     */
    private String channel;

    ElasticEnum(String index, String type, String channel) {
        this.index = index;
        this.type = type;
        this.channel = channel;
    }

    public String getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public String getChannel() {
        return channel;
    }

    public static ElasticEnum getByChannel(String channel) {
        if (StringUtils.isBlank(channel)) {
            return null;
        }
        for (ElasticEnum elastic : ElasticEnum.values()) {
            if (StringUtils.equals(channel, elastic.getChannel())) {
                return elastic;
            }
        }
        return null;
    }
}
