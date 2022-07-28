package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.function.Compute;
import cn.wr.collect.sync.hbase.HBaseService;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import cn.wr.collect.sync.model.stock.StockGoods;
import cn.wr.collect.sync.utils.HBaseUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static cn.wr.collect.sync.constants.CommonConstants.*;

public class Goods2HBaseFlatMap extends RichFlatMapFunction<BasicModel<Model>, BasicModel<Model>> {
    private static final long serialVersionUID = -408721641391568537L;
    private static final Logger log = LoggerFactory.getLogger(Goods2HBaseFlatMap.class);
    private HBaseService hBaseService;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        hBaseService = new HBaseService(HBaseUtils.getConnection(tool));
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void flatMap(BasicModel<Model> model, Collector<BasicModel<Model>> collector) throws Exception {
        if (StringUtils.isBlank(model.getTableName())
            || Objects.isNull(model.getData())
            || !(StringUtils.equals(Table.BaseDataTable.partner_goods.name(), model.getTableName())
                || StringUtils.equals(Table.BaseDataTable.partner_store_goods.name(), model.getTableName())
                || StringUtils.equals(Table.BaseDataTable.stock_goods.name(), model.getTableName()))) {
            return;
        }

        this.saveHBaseThenCollect(model, collector);
    }

    private void saveHBaseThenCollect(BasicModel<Model> model, Collector<BasicModel<Model>> collector) {
        String key;
        if (StringUtils.equals(Table.BaseDataTable.partner_goods.name(), model.getTableName())) {
            key = Compute.goodsHbsKey((PartnerGoods) model.getData());
        }
        else if (StringUtils.equals(Table.BaseDataTable.partner_store_goods.name(), model.getTableName())) {
            key = Compute.storeGoodsHbsKey((PartnerStoreGoods) model.getData());
        }
        else if (StringUtils.equals(Table.BaseDataTable.stock_goods.name(), model.getTableName())) {
            key = Compute.stockGoodsHbsKey((StockGoods) model.getData());
        }
        else return;

        if (StringUtils.isBlank(key)) {
            return;
        }

        switch (model.getOperate()) {
            case OPERATE_INSERT:
            case OPERATE_UPDATE:
                hBaseService.put(HBASE_PREFIX + model.getTableName(), key, JSON.toJSONString(model.getData()));
                break;
            case OPERATE_DELETE:
            case OPERATE_UPDATE_DELETE:
                hBaseService.delete(HBASE_PREFIX + model.getTableName(), key);
                break;
            default:
                log.error("saveHBaseThenCollect unknown operate:{}", model.getOperate());
                return;
        }

        collector.collect(model);
    }
}
