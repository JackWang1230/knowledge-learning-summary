package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.TimeFields;
import cn.wr.collect.sync.dao.partner.PartnerGoodsDao;
import cn.wr.collect.sync.function.FieldCheck;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PolarDbBinlogBatch;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.utils.ReflectUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static cn.wr.collect.sync.constants.CommonConstants.MOD_FIELD_TRADE_CODE;
import static cn.wr.collect.sync.constants.CommonConstants.OPERATE_UPDATE_DELETE;

public class BinlogList2SingleFlatMapV2 extends RichFlatMapFunction<PolarDbBinlogBatch, BasicModel<Model>> {
    private static final long serialVersionUID = -4562858843760184440L;
    private static final Logger log = LoggerFactory.getLogger(BinlogList2SingleFlatMapV2.class);
    private static ParameterTool tool;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void flatMap(PolarDbBinlogBatch batch, Collector<BasicModel<Model>> collector) {
        if (Objects.isNull(batch)) {
            return;
        }
        Class<? extends Model> clazz = Table.BaseDataTable.getClazz(batch.getTable());
        if (Objects.isNull(clazz)) {
            return;
        }
        int length = batch.getData().size();
        String operate = StringUtils.lowerCase(batch.getType());

        for (int i = 0; i < length; i++) {
            switch (operate) {
                case CommonConstants.OPERATE_INSERT:
                    collector.collect(ReflectUtil.reflectData(batch.getTable(), operate, batch.getData().get(i),
                            null, clazz));
                    break;

                case CommonConstants.OPERATE_DELETE:
                    log.info("BinlogList2SingleFlatMapV2 delete: table:{} operate:{}, data:{}",
                            batch.getTable(), operate, JSON.toJSONString(batch.getData()));
                    collector.collect(ReflectUtil.reflectData(batch.getTable(), operate, batch.getData().get(i),
                            null, clazz));
                    break;

                case CommonConstants.OPERATE_UPDATE:
                    // 判断es关联字段是否发生变更，如果更新是无关字段，则跳过
                    if (!FieldCheck.esFieldCheck(batch.getOld().get(i), clazz)) {
                        log.info("BinlogList2SingleFlatMapV2 not need update: table:{} old:{}",
                                batch.getTable(), JSON.toJSONString(batch.getOld().get(i)));
                        continue;
                    }

                    // 判断是否有必要删除旧数据，key发生变更的情况下才需要删除旧数据
                    if (FieldCheck.keyCheck(batch.getOld().get(i), clazz)) {
                        log.info("BinlogList2SingleFlatMapV2 update_delete: table:{} operate:{}, data:{}, old:{}",
                                batch.getTable(), operate, JSON.toJSONString(batch.getData()),
                                JSON.toJSONString(batch.getOld()));

                        // 删除旧数据
                        collector.collect(ReflectUtil.reflectOld(batch.getTable(), OPERATE_UPDATE_DELETE,
                                batch.getData().get(i),
                                batch.getOld().get(i), clazz));

                        // partner_goods 表（db_id + internal_id 存在重复数据）需要单独处理
                        /*if (StringUtils.equals(Table.BaseDataTable.partner_goods.name(), batch.getTable())) {
                            collector.collect(this.reflectGoods(batch.getTable(), batch.getData().get(i),
                                    batch.getOld().get(i), clazz));
                        }
                        else {
                            // 删除旧数据
                            collector.collect(this.reflectOld(batch.getTable(), CommonConstants.OPERATE_UPDATE_DELETE,
                                    batch.getData().get(i),
                                    batch.getOld().get(i), clazz));
                        }*/
                    }
                    // partner_goods 表trade_code变更触发全连锁门店 Redis 更新数据，暂时没有其他好的办法只能先组装旧数据推送
                    else if (StringUtils.equals(Table.BaseDataTable.partner_goods.name(), batch.getTable())
                        && batch.getOld().get(i).containsKey(MOD_FIELD_TRADE_CODE)
                            && Objects.nonNull(batch.getOld().get(i).get(MOD_FIELD_TRADE_CODE))
                            && StringUtils.isNotBlank(batch.getOld().get(i).get(MOD_FIELD_TRADE_CODE).toString())) {
                        log.info("BinlogList2SingleFlatMapV2 goods update_delete: table:{} operate:{}, data:{}, old:{}",
                                batch.getTable(), operate, JSON.toJSONString(batch.getData()),
                                JSON.toJSONString(batch.getOld()));

                        // 删除旧数据
                        collector.collect(ReflectUtil.reflectOld(batch.getTable(), OPERATE_UPDATE_DELETE,
                                batch.getData().get(i),
                                batch.getOld().get(i), clazz));
                    }

                    // 更新新数据
                    collector.collect(ReflectUtil.reflectData(batch.getTable(), operate,
                            batch.getData().get(i),
                            batch.getOld().get(i), clazz));
                    break;

                default:
                    log.error("BinlogList2SingleFlatMapV2 flatMap unknown operate: {}", JSON.toJSONString(batch));
                    break;
            }
        }
    }

    /**
     * 映射partner_goods表数据
     * @param table
     * @param dataMap
     * @param oldMap
     * @param clazz
     * @return
     */
    private BasicModel<Model> reflectGoods(String table,
                                           Map<String, Object> dataMap,
                                           Map<String, Object> oldMap,
                                           Class<? extends Model> clazz) {
        BasicModel<Model> basicMode = new BasicModel<>();
        Model oldRecord = JSON.parseObject(JSON.toJSONString(dataMap), clazz);
        Arrays.stream(TimeFields.fields)
                .filter(f -> oldMap.containsKey(f) && Objects.nonNull(oldMap.get(f))
                        && StringUtils.equals(TimeFields.ZERO_TIME, oldMap.get(f).toString()))
                .forEach(f -> oldMap.put(f, null));
        // 反射旧数据
        ReflectUtil.convert(oldRecord, oldMap, clazz);

        PartnerGoodsDao goodsDao = new PartnerGoodsDao(tool);
        PartnerGoods partnerGoods = goodsDao.querySingle(((PartnerGoods) oldRecord).getDbId(), ((PartnerGoods) oldRecord).getInternalId());
        // 查询db_id + internal_id 是否存在，如果存在，则刷新新goods，如果不存在，则删除旧goods
        if (Objects.nonNull(partnerGoods)) {
            basicMode.setOperate(CommonConstants.OPERATE_UPDATE_INSERT);
            basicMode.setData(partnerGoods);
        }
        else {
            basicMode.setOperate(OPERATE_UPDATE_DELETE);
            basicMode.setData(oldRecord);
        }

        if (Objects.nonNull(oldMap)) {
            basicMode.setModFieldList(new ArrayList<>(oldMap.keySet()));
        }
        basicMode.setTableName(table);

        return basicMode;

    }

}
