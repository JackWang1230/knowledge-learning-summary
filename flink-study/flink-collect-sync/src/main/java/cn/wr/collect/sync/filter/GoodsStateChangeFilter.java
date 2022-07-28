package cn.wr.collect.sync.filter;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.annotations.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;

public class GoodsStateChangeFilter implements FilterFunction<PolarDbBinlogBatch> {
    private static final long serialVersionUID = 8200813321632092869L;

    @Override
    public boolean filter(PolarDbBinlogBatch binlogBatch) throws Exception {
        return (StringUtils.equals(CommonConstants.SCHEMA_UNION_DRUG_PARTNER, binlogBatch.getDatabase())
                && StringUtils.equals(Table.BaseDataTable.partner_store_goods.name(), binlogBatch.getTable()))
                || (StringUtils.equals(CommonConstants.SCHEMA_UNION_DRUG_PARTNER, binlogBatch.getDatabase())
                && StringUtils.equals(Table.BaseDataTable.partner_stores.name(), binlogBatch.getTable()));
    }
}
