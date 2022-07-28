package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.dao.time.QueryBasicChangeDao;
import cn.wr.collect.sync.model.BasicTimeGoods;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.utils.QueryUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.wr.collect.sync.constants.CommonConstants.OPERATE_DELETE;
import static cn.wr.collect.sync.constants.CommonConstants.OPERATE_UPDATE_DELETE;
import static cn.wr.collect.sync.constants.SqlConstants.SQL_PARTNER_GOODS;


public class Params2GoodsStoreFlatMap extends RichFlatMapFunction<BasicModel<PgConcatParams>, BasicModel<Model>> {
    private static final long serialVersionUID = 8755575858841096450L;
    private static final Logger log = LoggerFactory.getLogger(Params2GoodsStoreFlatMap.class);
    private ParameterTool tool;

    // 控制循环标识位
    private boolean flag = true;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        log.info("Params2GoodsStoreFlatMap open");
        flag = true;
    }

    @Override
    public void close() throws Exception {
        super.close();
        log.info("Params2GoodsStoreFlatMap close");
        flag = false;
    }

    @Override
    public void flatMap(BasicModel<PgConcatParams> model, Collector<BasicModel<Model>> collector) throws Exception {
        if (this.checkIfTradeCodeModify(model)) {
            this.collect2TradeCode(model, collector);
        } else {
            this.collect2Goods(model, collector);
        }
    }

    /**
     * gc_standard_goods_syncrds 删除trade_code / gc_goods_spu_attr_syncrds 删除trade_code
     * @param model
     * @return
     */
    private boolean checkIfTradeCodeModify(BasicModel<PgConcatParams> model) {
        return (StringUtils.equals(OPERATE_UPDATE_DELETE, model.getOperate())
                || StringUtils.equals(OPERATE_DELETE, model.getOperate()))
                && StringUtils.equals(Table.BaseDataTable.gc_standard_goods_syncrds.name(), model.getTableName())
                && StringUtils.isNotBlank(model.getData().getTradeCode());
    }

    /**
     * 组装条码相关数据下发下个算子
     * @param model
     * @param collector
     */
    private void collect2TradeCode(BasicModel<PgConcatParams> model, Collector<BasicModel<Model>> collector) {
        BasicTimeGoods goods = new BasicTimeGoods();
        goods.setTradeCode(model.getData().getTradeCode());
        collector.collect(new BasicModel<>(model.getTableName(), model.getOperate(), goods));
    }

    /**
     * 查询商品下发下个算子
     * @param model
     * @param collector
     */
    private void collect2Goods(BasicModel<PgConcatParams> model, Collector<BasicModel<Model>> collector) {
        QueryBasicChangeDao queryBasicChangeDao = new QueryBasicChangeDao(SQL_PARTNER_GOODS, model.getData(), tool);
        // 获取表对应的实体类
        int page = 0;
        Long id = 0L;
        List<PartnerGoods> models;
        Map<String, Object> params = new HashMap<>();
        while (flag) {
            params.put("id", id);
            // 从数据库查询数据,一次查1w条
            models = QueryUtil.findLimitPlus(queryBasicChangeDao, page, params, null);
            if (CollectionUtils.isNotEmpty(models)) {
                models.forEach(e -> {
                    collector.collect(new BasicModel<>(model.getTableName(), model.getOperate(),
                            new BasicTimeGoods().transfer(e, model.getData())));
                });
                id = models.get(models.size() - 1).getId();
            }

            if (CollectionUtils.isEmpty(models) || models.size() != QueryUtil.QUERY_PAGE_SIZE) {
                break;
            }

            page++;
        }
    }
}
