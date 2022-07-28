package cn.wr.collect.sync.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PolarDbBinlogBatch extends PolarDbBinlog {
    private static final long serialVersionUID = 4657506288454453157L;

    /**
     * 更新后数据
     */
    private List<Map<String, Object>> data;

    /**
     * 更新前数据
     * 只包含更新字段，未更新字段不在集合中
     */
    private List<Map<String, Object>> old;
}
