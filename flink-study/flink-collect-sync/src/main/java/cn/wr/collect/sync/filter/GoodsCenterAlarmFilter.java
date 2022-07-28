package cn.wr.collect.sync.filter;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.annotations.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;

import java.util.Objects;

public class GoodsCenterAlarmFilter implements FilterFunction<PolarDbBinlogBatch> {
    private static final long serialVersionUID = -885127475615044175L;

    @Override
    public boolean filter(PolarDbBinlogBatch binlogBatch) throws Exception {
        return Objects.nonNull(binlogBatch)
                && Objects.nonNull(binlogBatch.getDatabase())
                && Objects.nonNull(binlogBatch.getTable())
                && CommonConstants.SCHEMA_UNION_DRUG_PARTNER.equals(binlogBatch.getDatabase())
                && StringUtils.equals(Table.BaseDataTable.partner_goods.name(), binlogBatch.getTable());
    }
}
