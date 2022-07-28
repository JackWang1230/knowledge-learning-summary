package cn.wr.collect.sync.scheme;

import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.annotations.Table;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_UNION_DRUG_PARTNER;

public class PartnerGoodsBinlogSchema implements DeserializationSchema<PolarDbBinlogBatch> {
    private static final long serialVersionUID = 1869052620002503780L;
    private static final Logger log = LoggerFactory.getLogger(PartnerGoodsBinlogSchema.class);

    @Override
    public PolarDbBinlogBatch deserialize(byte[] bytes) {
        // 解析binlog 只需要 partner_goods / partners
        PolarDbBinlogBatch binlog = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), PolarDbBinlogBatch.class);
        if (binlog.getIsDdl() || !StringUtils.equals(SCHEMA_UNION_DRUG_PARTNER, binlog.getDatabase())
            || (!StringUtils.equals(Table.BaseDataTable.partner_goods.name(), binlog.getTable())
                && !StringUtils.equals(Table.BaseDataTable.partners.name(), binlog.getTable()))) {
            return null;
        }

        log.info("PartnerGoodsBinlogSchema binlog id:{}, table:{}, type:{} data:{}, old:{}",
                binlog.getId(), binlog.getTable(), binlog.getType(), JSON.toJSONString(binlog.getData()), JSON.toJSONString(binlog.getOld()));
        return binlog;
    }

    @Override
    public boolean isEndOfStream(PolarDbBinlogBatch binlog) {
        return false;
    }

    @Override
    public TypeInformation<PolarDbBinlogBatch> getProducedType() {
        return TypeInformation.of(PolarDbBinlogBatch.class);
    }

}
