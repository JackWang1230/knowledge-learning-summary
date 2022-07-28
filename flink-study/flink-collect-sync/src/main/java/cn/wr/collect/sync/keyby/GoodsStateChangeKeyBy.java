package cn.wr.collect.sync.keyby;

import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.basic.BasicModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.functions.KeySelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GoodsStateChangeKeyBy implements KeySelector<BasicModel<Model>, String> {
    private static final long serialVersionUID = -7292926577568477253L;
    private static final Logger log = LoggerFactory.getLogger(GoodsStateChangeKeyBy.class);

    @Override
    public String getKey(BasicModel<Model> model) throws Exception {
        List<Object> keyList = Arrays.stream(model.getData().getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(QueryField.class))
                .sorted(Comparator.comparingInt(f -> f.getDeclaredAnnotation(QueryField.class).order()))
                .map(f -> {
                    try {
                        f.setAccessible(true);
                        return f.get(model.getData());
                    } catch (IllegalAccessException e) {
                        log.error("BinlogKeyBy IllegalAccessException: {}", e);
                    }
                    return null;
                })
                .collect(Collectors.toList());
        return model.getTableName() + SymbolConstants.HOR_LINE + StringUtils.join(keyList, SymbolConstants.HOR_LINE);
    }
}
