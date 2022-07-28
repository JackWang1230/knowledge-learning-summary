package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.dao.time.QueryStandardGoodsDao;
import cn.wr.collect.sync.function.Compute;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.gc.*;
import cn.wr.collect.sync.model.standard.StandardElastic;
import cn.wr.collect.sync.redis.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class StandardSplitFlatMap extends RichFlatMapFunction<BasicModel<Model>, BasicModel<StandardElastic>> {
    private static final long serialVersionUID = -7899393403884065613L;
    private static final Logger log = LoggerFactory.getLogger(StandardSplitFlatMap.class);
    private ParameterTool tool;
    private RedisService redisService;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        redisService = new RedisService(tool);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void flatMap(BasicModel<Model> model, Collector<BasicModel<StandardElastic>> collector) throws Exception {
        if (Objects.isNull(model) || StringUtils.isBlank(model.getTableName()) || Objects.isNull(model.getData())) {
            return;
        }

        Table.BaseDataTable table = Table.BaseDataTable.getEnum(model.getTableName());
        if (Objects.isNull(table)) {
            return;
        }

        /*try {
            // 实时写入redis存在延时，此处设置延时时间
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.error("Thread sleep InterruptedException: {}", e);
        }*/

        Model data = model.getData();
        switch (table) {
            case gc_goods_overweight:
                this.collectGoodsOverweight(model, collector, (GoodsOverweight) data);
                break;

            case gc_base_nootc:
                this.collectNootc(model, collector, (BaseNootc) data);
                break;

            case gc_goods_dosage:
                this.collectDosage(model, collector, (GoodsDosage) data);
                break;

            case gc_goods_spu_attr_syncrds:
                this.collectSpuAttr(model, collector, (GoodsSpuAttrSyncrds) data);
                break;

            case gc_goods_spu:
                this.collectSpuAttr(model, collector, (GoodsSpu) data);
                break;
            default:
                return;
        }
    }

    private void collectSpuAttr(BasicModel<Model> model, Collector<BasicModel<StandardElastic>> collector, GoodsSpu data) {
        List<String> tradeCodeList = new QueryStandardGoodsDao(tool).queryTradeCodeBySpuId(data.getId());
        if (CollectionUtils.isEmpty(tradeCodeList)) {
            return;
        }
        tradeCodeList.forEach(tradeCode -> {
            StandardGoodsSyncrds standard = redisService.queryGcStandardGoodsSyncrds(tradeCode);
            if (Objects.isNull(standard)) {
                return;
            }
            GoodsSpu goodsSpu = redisService.queryGoodsSpu(standard.getSpuId());
            BaseNootc baseNootc = null;
            if (Objects.nonNull(goodsSpu)) {
                baseNootc = redisService.queryGcBaseNootc(goodsSpu.getApprovalNumber());
            }
            GoodsDosage dosage = redisService.queryGcGoodsDosage(tradeCode);
            StandardElastic elastic = new StandardElastic();
            elastic.setId(standard.getId());
            elastic.setIsPrescription(Compute.isPrescription(baseNootc, dosage));
            collector.collect(new BasicModel<>(model.getTableName(), model.getOperate(), elastic, model.getModFieldList()));
        });

    }

    private void collectSpuAttr(BasicModel<Model> model, Collector<BasicModel<StandardElastic>> collector, GoodsSpuAttrSyncrds data) {
        StandardGoodsSyncrds standard = redisService.queryGcStandardGoodsSyncrds(data.getBarCode());
        if (Objects.isNull(standard)) {
            return;
        }

        StandardElastic elastic = new StandardElastic();
        elastic.setId(standard.getId());
        elastic.setIsEphedrine(false);
        List<GoodsSpuAttrSyncrds> spuAttrList = redisService.queryGcGoodsSpuAttrSyncrds(standard.getTradeCode());
        spuAttrList.forEach(spuAttr -> {
            GoodsAttrInfoSyncrds attr = redisService.queryGcGoodsAttrInfoSyncrds(spuAttr.getAttrId());
            if (Objects.isNull(attr)) {
                return;
            }
            if (!elastic.getIsEphedrine()) {
                elastic.setIsEphedrine(Compute.isEphedrine(attr));
            }
        });
        collector.collect(new BasicModel<>(model.getTableName(), model.getOperate(), elastic, model.getModFieldList()));
    }

    private void collectDosage(BasicModel<Model> model, Collector<BasicModel<StandardElastic>> collector, GoodsDosage data) {
        StandardGoodsSyncrds standard = redisService.queryGcStandardGoodsSyncrds(data.getTradeCode());
        if (Objects.isNull(standard)) {
            return;
        }

        GoodsSpu goodsSpu = redisService.queryGoodsSpu(standard.getSpuId());
        BaseNootc baseNootc = null;
        if (Objects.nonNull(goodsSpu)) {
            baseNootc = redisService.queryGcBaseNootc(goodsSpu.getApprovalNumber());
        }
        GoodsDosage dosage = redisService.queryGcGoodsDosage(data.getTradeCode());
        StandardElastic elastic = new StandardElastic();
        elastic.setId(standard.getId());
        elastic.setIsPrescription(Compute.isPrescription(baseNootc, dosage));
        collector.collect(new BasicModel<>(model.getTableName(), model.getOperate(), elastic, model.getModFieldList()));
    }


    private void collectNootc(BasicModel<Model> model, Collector<BasicModel<StandardElastic>> collector, BaseNootc data) {
        List<String> tradeCodeList = new QueryStandardGoodsDao(tool).queryTradeCodeByApprovalNumber(data.getApprovalNumber());
        if (CollectionUtils.isEmpty(tradeCodeList)) {
            return;
        }
        tradeCodeList.forEach(tradeCode -> {
            StandardGoodsSyncrds standard = redisService.queryGcStandardGoodsSyncrds(tradeCode);
            if (Objects.isNull(standard)) {
                return;
            }
            GoodsSpu goodsSpu = redisService.queryGoodsSpu(standard.getSpuId());
            BaseNootc baseNootc = null;
            if (Objects.nonNull(goodsSpu)) {
                baseNootc = redisService.queryGcBaseNootc(goodsSpu.getApprovalNumber());
            }
            StandardElastic elastic = new StandardElastic();
            elastic.setId(standard.getId());
            GoodsDosage dosage = redisService.queryGcGoodsDosage(tradeCode);
            elastic.setIsPrescription(Compute.isPrescription(baseNootc, dosage));
            collector.collect(new BasicModel<>(model.getTableName(), model.getOperate(), elastic, model.getModFieldList()));
        });
    }

    private void collectGoodsOverweight(BasicModel<Model> model, Collector<BasicModel<StandardElastic>> collector, GoodsOverweight data) {
        StandardGoodsSyncrds standard = redisService.queryGcStandardGoodsSyncrds(data.getTradeCode());
        if (Objects.isNull(standard)) {
            return;
        }
        GoodsOverweight goodsOverweight = redisService.queryGoodsOverweight(data.getTradeCode());
        StandardElastic elastic = new StandardElastic();
        elastic.setId(standard.getId());
        elastic.setIsOverweight(Compute.isOverweight(goodsOverweight));
        collector.collect(new BasicModel<>(model.getTableName(), model.getOperate(), elastic, model.getModFieldList()));
    }
}
