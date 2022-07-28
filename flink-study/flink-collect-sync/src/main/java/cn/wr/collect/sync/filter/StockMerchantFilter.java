package cn.wr.collect.sync.filter;

import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.annotations.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_CN_UD_MID_STOCK;


public class StockMerchantFilter implements FilterFunction<PolarDbBinlogBatch> {
    private static final long serialVersionUID = -1961258992652255443L;

    @Override
    public boolean filter(PolarDbBinlogBatch binlog) throws Exception {
        return StringUtils.equals(SCHEMA_CN_UD_MID_STOCK, binlog.getDatabase())
                && StringUtils.equals(Table.BaseDataTable.stock_merchant.name(), binlog.getTable());
    }
}
