package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.ElasticEnum;
import cn.wr.collect.sync.constants.StandardEnum;
import cn.wr.collect.sync.function.FieldCheck;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.standard.StandardElastic;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.CommonConstants.*;

public class Standard2EsSink implements ElasticsearchSinkFunction<BasicModel<StandardElastic>> {
    private static final long serialVersionUID = 8915491246596370643L;
    private static final Logger log = LoggerFactory.getLogger(Standard2EsSink.class);

    @Override
    public void process(BasicModel<StandardElastic> model, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
        if (Objects.isNull(model) || Objects.isNull(model.getData())) {
            return;
        }
        StandardElastic data = model.getData();
        if (Objects.isNull(data.getId())) {
            log.info("Standard2EsSink id is null {}", JSON.toJSONString(model));
            return;
        }
        try {
            Set<String> esFieldName = null;
            switch (model.getOperate()) {
                case OPERATE_DELETE:
                case OPERATE_INSERT:
                case OPERATE_UPDATE_DELETE:
                    List<String> list = StandardEnum.getMap(model.getTableName());
                    if (CollectionUtils.isNotEmpty(list)) {
                        esFieldName = new HashSet<>(list);
                    }
                    break;
                case OPERATE_UPDATE:
                    if (FieldCheck.standardKeyCheck(model.getModFieldList(), model.getTableName())) {
                        List<String> allList = StandardEnum.getMap(model.getTableName());
                        if (CollectionUtils.isNotEmpty(allList)) {
                            esFieldName = new HashSet<>(allList);
                        }
                    }
                    else {
                        esFieldName = model.getModFieldList().stream().flatMap(field -> {
                            String[] map = StandardEnum.getMap(model.getTableName(), field);
                            if (Objects.nonNull(map)) {
                                return Arrays.stream(map);
                            }
                            return null;
                        }).filter(Objects::nonNull).collect(Collectors.toSet());
                    }
                    break;
                default:
                    break;
            }

            if (CollectionUtils.isEmpty(esFieldName)) {
                log.info("Standard2EsSink esfieldName is empty:{}", JSON.toJSONString(model));
                return;
            }

            List<String> esFieldNameList = new ArrayList<>(esFieldName);
            // 获取 StandardElastic 所有field
            List<Field> esFieldList = Arrays.stream(StandardElastic.class.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(JSONField.class)
                            && esFieldNameList.contains(field.getDeclaredAnnotation(JSONField.class).name()))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(esFieldList)) {
                log.info("Standard2EsSink esFieldList is empty:{}", JSON.toJSONString(model));
                return;
            }

            // 组装es更新数据doc
            JSONObject json = new JSONObject();
            esFieldList.forEach(esfield -> {
                try {
                    esfield.setAccessible(true);
                    json.put(esfield.getDeclaredAnnotation(JSONField.class).name(), esfield.get(data));
                } catch (IllegalAccessException e) {
                    log.error("Standard2EsSink IllegalAccessException:{}", e);
                }
            });

            this.update(requestIndexer, data, json);
        }
        catch (Exception e) {
            log.error("Standard2EsSink Exception:{}", e);
        }
    }

    /**
     * 更新es
     * @param requestIndexer
     * @param data
     * @param json
     */
    private void update(RequestIndexer requestIndexer, StandardElastic data, JSONObject json) {
        log.info("{}, id:{}, val:{}", ElasticEnum.STANDARD.getIndex(), data.getId(), json);
        // 更新
        UpdateRequest request = new UpdateRequest();
        request.index(ElasticEnum.STANDARD.getIndex())
                .type(ElasticEnum.STANDARD.getType())
                .doc(JSON.toJSONBytes(json, SerializerFeature.WriteMapNullValue), XContentType.JSON)
                .id(String.valueOf(data.getId()));
        requestIndexer.add(request);
    }

}
