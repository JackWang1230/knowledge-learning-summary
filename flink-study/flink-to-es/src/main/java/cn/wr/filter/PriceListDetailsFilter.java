package cn.wr.filter;

import cn.wr.model.CanalDataModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static cn.wr.constants.PropertiesConstants.*;

/**
 * 过滤需要的库及表数据
 * @author RWang
 * @Date 2022/8/16
 */

public class PriceListDetailsFilter implements FilterFunction<CanalDataModel> {
    private static final long serialVersionUID = 8028394622629384452L;
    private static final Logger logger = LoggerFactory.getLogger(PriceListDetailsFilter.class);

    @Override
    public boolean filter(CanalDataModel value)  {
        if (Objects.isNull(value)) return false;
        return (StringUtils.equals(SCHEMA_CN_UDC_GSBP_PRICE,value.getDataBase())
                && StringUtils.equals(TABLE_PRICE_LIST_DETAILS,value.getTable()));
    }
}
