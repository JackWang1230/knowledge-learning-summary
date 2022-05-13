package cn.wr.filter;

import cn.wr.model.CanalDataModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;

import static cn.wr.constants.PropertiesConstants.SCHEMA_CN_UNIONDRUG_MIDDLEED_GOODSCENTER;
import static cn.wr.constants.PropertiesConstants.TABLE_GC_CONFIG_SKU;


/**
 * 提取gc_config_sku表的binlog数据
 * @author RWang
 * @Date 2022/5/11
 */

public class GcConfigSkuFilter implements FilterFunction<CanalDataModel> {
    private static final long serialVersionUID = -1886439560368618897L;

    @Override
    public boolean filter(CanalDataModel value) throws Exception {
        return StringUtils.equals(SCHEMA_CN_UNIONDRUG_MIDDLEED_GOODSCENTER,value.getDataBase())
                && StringUtils.equals(TABLE_GC_CONFIG_SKU,value.getTable());
    }
}
