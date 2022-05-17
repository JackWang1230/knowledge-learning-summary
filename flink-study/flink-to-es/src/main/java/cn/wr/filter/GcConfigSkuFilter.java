package cn.wr.filter;

import cn.wr.model.CanalDataModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import static cn.wr.constants.PropertiesConstants.SCHEMA_CN_UNIONDRUG_MIDDLEED_GOODSCENTER;
import static cn.wr.constants.PropertiesConstants.TABLE_GC_CONFIG_SKU;


/**
 * 提取gc_config_sku表的binlog数据
 * @author RWang
 * @Date 2022/5/11
 */

public class GcConfigSkuFilter implements FilterFunction<String> {

    private static final long serialVersionUID = 53606346504478720L;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean filter(String value) throws Exception {

        if (StringUtils.isBlank(value)) return false;
        CanalDataModel canalDataModel = objectMapper.readValue(value, CanalDataModel.class);
        return StringUtils.equals(SCHEMA_CN_UNIONDRUG_MIDDLEED_GOODSCENTER,canalDataModel.getDataBase())
                && StringUtils.equals(TABLE_GC_CONFIG_SKU,canalDataModel.getTable());
    }
}
