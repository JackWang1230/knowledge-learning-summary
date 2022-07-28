package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.ElasticEnum;
import cn.wr.collect.sync.constants.EsFieldConst;
import cn.wr.collect.sync.constants.SyncTypeEnum;
import cn.wr.collect.sync.function.Compute;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.ParamsConcat;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Correspond;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.wr.collect.sync.constants.CommonConstants.IS_DTP_TRUE;

public class Basic2EsSink02 implements ElasticsearchSinkFunction<BasicModel<ElasticO2O>> {
    private static final long serialVersionUID = 8915491246596370643L;
    private static final Logger log = LoggerFactory.getLogger(Basic2EsSink02.class);

    @Override
    public void process(BasicModel<ElasticO2O> model, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
        if (Objects.isNull(model) || Objects.isNull(model.getData())) {
            return;
        }
        ElasticO2O data = model.getData();
        if (StringUtils.isBlank(data.getSkuCode())) {
            log.info("Basic2EsSink02 skuCode is null: {}", JSON.toJSONString(model));
            return;
        }
        try {
            SyncTypeEnum syncType = SyncTypeEnum.get(model.getSyncType());
            if (Objects.isNull(syncType)) {
                log.info("Basic2EsSink02 syncType is null: {}", JSON.toJSONString(model));
                return;
            }
            // 此处如果基础商品删除数据的情况下， 同样还是更新数据，根据 isDtp + 上下架字段 判断ES是更新还是删除数据
            switch (syncType) {
                case FULL:

                    // (下架) && 非DTP商品
                    if ((Objects.isNull(data.getIsOffShelf()) || data.getIsOffShelf())
                            && !CommonConstants.IS_DTP_TRUE.equals(data.getIsDtp())) {
                        this.delete(data, requestIndexer);
                    } else {
                        this.insertOrUpdate(data, requestIndexer);
                    }
                    break;

                case PART:
                    // 获取当前变更表对象实体类
                    Class<? extends Model> clazz = Table.BaseDataTable.getClazz(model.getTableName());
                    if (Objects.isNull(clazz)) {
                        log.info("Basic2EsSink02 clazz is empty:{}", JSON.toJSONString(model));
                        return;
                    }

                    int modCondition = Compute.computeCondition(model.getOperate(), model.getTableName(), model.getModFieldList());
                    // 新增/删除/更新删除/key变更 情况下需要处理当前table对应es中所有字段
                    // 获取所有需要更新字段字段名
                    Set<String> esFieldName = Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
                        // colums && correspond && field
                        if (!field.isAnnotationPresent(Column.class) || !field.isAnnotationPresent(Correspond.class)
                                || (Correspond.Type.Field != field.getDeclaredAnnotation(Correspond.class).type()
                                && Correspond.Type.Both != field.getDeclaredAnnotation(Correspond.class).type())) {
                            return false;
                        }
                        // key 发生变更 所有field重刷数据
                        if (CommonConstants.MOD_FIELD_KEY == modCondition) {
                            return true;
                        }
                        // 字段发生变更重刷数据
                        return CollectionUtils.isNotEmpty(model.getModFieldList())
                                && model.getModFieldList().contains(field.getDeclaredAnnotation(Column.class).name());
                    }).flatMap(f -> Stream.of(f.getDeclaredAnnotation(Correspond.class).field())).collect(Collectors.toSet());

                    // 特殊表处理
                    // gc_goods_attr_info_syncrds id 只监控id字段，未配置其他关联字段
                    if (StringUtils.equals(Table.BaseDataTable.gc_goods_attr_info_syncrds.name(), model.getTableName())
                            && CommonConstants.MOD_FIELD_KEY == modCondition) {
                        esFieldName.add(EsFieldConst.is_double);
                        esFieldName.add(EsFieldConst.is_ephedrine);
                    }
                    // gc_partner_goods_gift 增删变更触发全量更新
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

                    // log.info("Basic2EsSink02 process esFieldName: {}, data: {}", JSON.toJSONString(esFieldName), JSON.toJSONString(model));
                    if (CollectionUtils.isEmpty(esFieldName)) {
                        log.info("Basic2EsSink02 esfieldName is empty:{}", JSON.toJSONString(model));
                        return;
                    }

                    // 手动插入更新时间字段触发更新
                    esFieldName.add(EsFieldConst.sync_date);
                    log.info("Basic2EsSink02: {}, {}, {}, esfield:{}, data:{} old:{}", model.getTableName(),
                            model.getOperate(), syncType, esFieldName, JSON.toJSONString(data),
                            JSON.toJSONString(model.getModFieldList()));

                    // 获取 ElasticO2O 所有field
                    List<Field> esFieldList = Arrays.stream(ElasticO2O.class.getDeclaredFields())
                            .filter(field -> field.isAnnotationPresent(JSONField.class) && esFieldName.contains(field.getDeclaredAnnotation(JSONField.class).name()))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(esFieldList)) {
                        log.info("Basic2EsSink02 esFieldList is empty:{}", JSON.toJSONString(model));
                        return;
                    }

                    // 组装es更新数据doc
                    JSONObject json = new JSONObject();
                    esFieldList.forEach(esfield -> {
                        try {
                            esfield.setAccessible(true);
                            json.put(esfield.getDeclaredAnnotation(JSONField.class).name(), esfield.get(data));
                        } catch (IllegalAccessException e) {
                            log.error("Basic2EsSink IllegalAccessException:{}", e);
                        }
                    });

                    this.update(requestIndexer, data, json);
                    break;

                default:
                    break;
            }
        }
        catch (Exception e) {
            log.error("Basic2EsSink02 Exception:{}", e);
        }
    }

    /**
     * 更新es
     * @param requestIndexer
     * @param data
     * @param json
     */
    private void update(RequestIndexer requestIndexer, ElasticO2O data, JSONObject json) {
        // log.info("Basic2EsSink02 update skuCode:{}", data.getSkuCode());
        // 更新
        UpdateRequest request = new UpdateRequest();
        request.index(ElasticEnum.O2O.getIndex())
                .type(ElasticEnum.O2O.getType())
                .doc(JSON.toJSONBytes(json, SerializerFeature.WriteMapNullValue), XContentType.JSON)
                .id(data.getSkuCode());
        requestIndexer.add(request);
    }

    /**
     * 覆盖es
     * @param data
     * @param requestIndexer
     */
    private void insertOrUpdate(ElasticO2O data, RequestIndexer requestIndexer) {
        ParamsConcat param = new ParamsConcat(data.getSkuCode(),
                JSON.toJSONBytes(data, SerializerFeature.WriteMapNullValue));
        /*log.info("Basic2EsSink02 insertOrUpdate skuCode:{}, isOffShelf:{}, isDtp:{}, isDtpStore:{}",
                data.getSkuCode(), data.getIsOffShelf(), data.getIsDtp(), data.getIsDtpStore());*/
        // 写入es
        requestIndexer.add(Requests.indexRequest()
                .index(ElasticEnum.O2O.getIndex())
                .type(ElasticEnum.O2O.getType())
                .source(param.getContent(), XContentType.JSON)
                .id(param.getSkuCode()));
    }

    /**
     * 删除es
     * @param data
     * @param requestIndexer
     */
    private void delete(ElasticO2O data, RequestIndexer requestIndexer) {
        String skuCode = data.getSkuCode();
        log.info("Basic2EsSink02 delete skuCode:{}, isOffShelf:{}, isDtp:{}, isDtpStore:{}",
                skuCode, data.getIsOffShelf(), data.getIsDtp(), data.getIsDtpStore());
        // 删除es
        requestIndexer.add(Requests.deleteRequest(ElasticEnum.O2O.getIndex())
                .type(ElasticEnum.O2O.getType())
                .id(skuCode));
    }
}
