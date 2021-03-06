package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.EsFieldConst;
import cn.wr.collect.sync.constants.SyncTypeEnum;
import cn.wr.collect.sync.function.Compute;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Correspond;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.basic.BasicModelV1;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class EsFieldFullOrPartFlatMap extends RichFlatMapFunction<BasicModel<ElasticO2O>, BasicModel<ElasticO2O>> {
    private static final long serialVersionUID = -5130305133180462638L;
    private static final Logger log = LoggerFactory.getLogger(EsFieldFullOrPartFlatMap.class);

    @Override
    public void flatMap(BasicModel<ElasticO2O> model, Collector<BasicModel<ElasticO2O>> out) throws Exception {

        if (Objects.isNull(model) || Objects.isNull(model.getData())) {
            return;
        }
        ElasticO2O data = model.getData();
        if (StringUtils.isBlank(data.getSkuCode())) {
             log.info("EsFieldFullOrPartFlatMap skuCode is null: {}", JSON.toJSONString(model));
            return;
        }
        try {
            SyncTypeEnum syncType = SyncTypeEnum.get(model.getSyncType());
            if (Objects.isNull(syncType)) {
                 log.info("EsFieldFullOrPartFlatMap syncType is null: {}", JSON.toJSONString(model));
                return;
            }
            // ??????????????????????????????????????????????????? ????????????????????????????????? isDtp + ??????????????? ??????ES???????????????????????????
            switch (syncType) {
                case FULL:
                    BasicModelV1<ElasticO2O> modelV1 = new BasicModelV1<>();
                    modelV1.setTableName(model.getTableName());
                    modelV1.setData(model.getData());
                    modelV1.setOld(model.getOld());
                    modelV1.setOperate(model.getOperate());
                    modelV1.setSyncType(model.getSyncType());
                    modelV1.setModFieldList(model.getModFieldList());
                    out.collect(modelV1);
                    break;

                case PART:
                    // ????????????????????????????????????
                    Class<? extends Model> clazz = Table.BaseDataTable.getClazz(model.getTableName());
                    if (Objects.isNull(clazz)) {
                         log.info("EsFieldFullOrPartFlatMap clazz is empty:{}", JSON.toJSONString(model));
                        return;
                    }

                    int modCondition = Compute.computeCondition(model.getOperate(), model.getTableName(), model.getModFieldList());
                    // ??????/??????/????????????/key?????? ???????????????????????????table??????es???????????????
                    // ???????????????????????????????????????
                    Set<String> esFieldName = Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
                        // colums && correspond && field
                        if (!field.isAnnotationPresent(Column.class) || !field.isAnnotationPresent(Correspond.class)
                                || (Correspond.Type.Field != field.getDeclaredAnnotation(Correspond.class).type()
                                && Correspond.Type.Both != field.getDeclaredAnnotation(Correspond.class).type())) {
                            return false;
                        }
                        // key ???????????? ??????field????????????
                        if (CommonConstants.MOD_FIELD_KEY == modCondition) {
                            return true;
                        }
                        // ??????????????????????????????
                        return CollectionUtils.isNotEmpty(model.getModFieldList())
                                && model.getModFieldList().contains(field.getDeclaredAnnotation(Column.class).name());
                    }).flatMap(f -> Stream.of(f.getDeclaredAnnotation(Correspond.class).field())).collect(Collectors.toSet());

                    // ???????????????
                    // gc_goods_attr_info_syncrds id ?????????id????????????????????????????????????
                    if (StringUtils.equals(Table.BaseDataTable.gc_goods_attr_info_syncrds.name(), model.getTableName())
                            && CommonConstants.MOD_FIELD_KEY == modCondition) {
                        esFieldName.add(EsFieldConst.is_double);
                        esFieldName.add(EsFieldConst.is_ephedrine);
                    }
                    // gc_partner_goods_gift ??????????????????????????????
                    else if (StringUtils.equals(Table.BaseDataTable.gc_partner_goods_gift.name(), model.getTableName())) {
                        esFieldName.addAll(EsFieldConst.getAllField());
                    }
                    else if (StringUtils.equals(Table.BaseDataTable.stock_goods.name(), model.getTableName())) {
                        if (Objects.isNull(data.getIsOffShelf())) {
                            esFieldName.remove(EsFieldConst.is_off_shelf);
                        }
                    }
                    else if (StringUtils.equals(Table.BaseDataTable.price_store.name(), model.getTableName())) {
                        esFieldName.add(EsFieldConst.sale_price);
                        esFieldName.add(EsFieldConst.base_price);
                    }

                     log.info("EsFieldFullOrPartFlatMap process esFieldName: {}, data: {}", JSON.toJSONString(esFieldName), JSON.toJSONString(model));
                    if (CollectionUtils.isEmpty(esFieldName)) {
                         log.info("EsFieldFullOrPartFlatMap esfieldName is empty:{}", JSON.toJSONString(model));
                        return;
                    }

                    // ??????????????????????????????????????????
                    esFieldName.add(EsFieldConst.sync_date);

                    // ?????? ElasticO2O ??????field
                    List<Field> esFieldList = Arrays.stream(ElasticO2O.class.getDeclaredFields())
                            .filter(field -> field.isAnnotationPresent(JSONField.class) && esFieldName.contains(field.getDeclaredAnnotation(JSONField.class).name()))
                            .collect(Collectors.toList());
                    List<String> esModFields = esFieldList.stream().map(Field::getName).collect(Collectors.toList());

                    if (CollectionUtils.isEmpty(esFieldList)) {
                          log.info("EsFieldFullOrPartFlatMap esFieldList is empty:{}", JSON.toJSONString(model));
                        return;
                    }
                    BasicModelV1<ElasticO2O> modelV2 = new BasicModelV1<>();
                    modelV2.setTableName(model.getTableName());
                    modelV2.setData(model.getData());
                    modelV2.setOld(model.getOld());
                    modelV2.setOperate(model.getOperate());
                    modelV2.setSyncType(model.getSyncType());
                    modelV2.setModFieldList(model.getModFieldList());
                    modelV2.setEsModFields(esModFields);
                    out.collect(modelV2);
                    // this.update(requestIndexer, data, json);
                    break;

                default:
                    break;
            }
        }
        catch (Exception e) {
             log.error("EsFieldFullOrPartFlatMap Exception:{}", e);
        }


    }

}
