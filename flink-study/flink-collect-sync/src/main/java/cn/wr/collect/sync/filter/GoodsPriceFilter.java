package cn.wr.collect.sync.filter;

import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.annotations.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_CN_UD_GSBP_PRICE;

public class GoodsPriceFilter implements FilterFunction<PolarDbBinlogBatch> {

    private static final long serialVersionUID = -1961258992652255445L;

    @Override
    public boolean filter(PolarDbBinlogBatch binlog) throws Exception {
        return StringUtils.equals(SCHEMA_CN_UD_GSBP_PRICE, binlog.getDatabase())
                && (StringUtils.equals(Table.BaseDataTable.price_list_details.name(), binlog.getTable()) ||
                StringUtils.equals(Table.BaseDataTable.price_store.name(), binlog.getTable())
                );
    }
}
