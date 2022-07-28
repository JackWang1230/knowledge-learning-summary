package cn.wr.collect.sync.utils;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.FieldRefreshEnum;
import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.constants.TimeFields;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.MutationResult;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.basic.BasicModel;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReflectUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectUtil.class);

    /**
     * 校验字段是否发生变更
     *
     * @param result
     * @return
     */
    public static boolean checkFieldsIsDiff(MutationResult result) {
        FieldRefreshEnum refresh = FieldRefreshEnum.getEnum(result.getTableName());
        if (null == refresh) {
            return false;
        }
        if (StringUtils.equals(CommonConstants.OPERATE_DELETE, result.getMutationType())
                || StringUtils.equals(CommonConstants.OPERATE_INSERT, result.getMutationType())) {
            return true;
        }

        Model oldRecord = result.getOldRecord();
        Model newRecord = result.getNewRecord();
        for (String field : refresh.getFields()) {
            String oldfd = ReflectUtil.getFieldValueByFieldName(field, oldRecord);
            String newfd = ReflectUtil.getFieldValueByFieldName(field, newRecord);
            if (!StringUtils.equals(oldfd, newfd)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 赋值
     *
     * @param oldRecord
     * @param json
     */
    public static void convert(Model oldRecord, Map<String, Object> json, Class<? extends Model> clazz) {
        try {
            Field[] declaredFields = oldRecord.getClass().getDeclaredFields();
            Model o = JSON.parseObject(JSON.toJSONString(json), clazz);
            for (Field field : declaredFields) {
                if (Objects.isNull(field.getAnnotation(Column.class))) {
                    continue;
                }
                String name = field.getAnnotation(Column.class).name();
                if (json.containsKey(name)) {
                    field.setAccessible(true);
                    Field oldField = o.getClass().getDeclaredField(field.getName());
                    oldField.setAccessible(true);
                    field.set(oldRecord, oldField.get(o));
                }
            }
        } catch (Exception e) {
            LOGGER.error("ReflectUtil model:{}, map:{} Exception:{}", oldRecord, json, e);
        }
    }

    /*public static void main(String[] args) {
        GoodsManual goodsManual1 = new GoodsManual();
        goodsManual1.setApprovalNumber("1");
        GoodsManual goodsManual2 = new GoodsManual();
        goodsManual2.setApprovalNumber("1");
        MutationResult result = new MutationResult();
        result.setMutationType(MutationResult.MutationType.UPDATE.getValue());
        result.setTableName(FieldRefreshEnum.gc_goods_manual.getTableName());
        result.setOldRecord(goodsManual1);
        result.setNewRecord(goodsManual2);

        System.out.println(ReflectUtil.checkFieldsIsDiff(result));
    }*/

    public static String getFieldValueByFieldName(String fieldName, Object object) {
        if (StringUtils.isBlank(fieldName) || Objects.isNull(object)) {
            return null;
        }
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            //对private的属性的访问
            field.setAccessible(true);
            return null == field.get(object) ? null : field.get(object).toString();
        } catch (Exception e) {
            LOGGER.error("ReflectUtil getFieldValueByFieldName error:{}", e);
            return null;
        }
    }

    /**
     * 公用方法 返回对象
     *
     * @param tableName
     * @param operate
     * @param dataMap
     * @param oldMap
     * @param clazz
     * @return
     */
    public static BasicModel<Model> reflectData(String tableName,
                                                String operate,
                                                Map<String, Object> dataMap,
                                                Map<String, Object> oldMap,
                                                Class<? extends Model> clazz) {
        BasicModel<Model> basicMode = new BasicModel<>();

        for (String f : TimeFields.fields) {
            if (Objects.nonNull(dataMap) && dataMap.containsKey(f) && Objects.nonNull(dataMap.get(f))
                    && (StringUtils.equals(TimeFields.ZERO_TIME, dataMap.get(f).toString())
                    || StringUtils.equals(TimeFields.ZERO_DATE, dataMap.get(f).toString()))) {
                dataMap.put(f, null);
            }
            if (Objects.nonNull(oldMap) && oldMap.containsKey(f) && Objects.nonNull(oldMap.get(f))
                    && (StringUtils.equals(TimeFields.ZERO_TIME, oldMap.get(f).toString())
                    || StringUtils.equals(TimeFields.ZERO_DATE, oldMap.get(f).toString()))) {
                oldMap.put(f, null);
            }
        }

        basicMode.setData(JSON.parseObject(JSON.toJSONString(dataMap), clazz));
        if (Objects.nonNull(oldMap)) {
            basicMode.setModFieldList(new ArrayList<>(oldMap.keySet()));

            Map<String, Object> map = new HashMap<>();
            map.putAll(dataMap);
            map.putAll(oldMap);
            basicMode.setOld(JSON.parseObject(JSON.toJSONString(map), clazz));
        }
        basicMode.setTableName(tableName);
        basicMode.setOperate(operate);
        return basicMode;
    }

    /**
     * 公用方法 返回对象
     *
     * @param dataMap
     * @param oldMap
     * @param clazz
     * @return
     */
    public static BasicModel<Model> reflectOld(String tableName,
                                               String operate,
                                               Map<String, Object> dataMap,
                                               Map<String, Object> oldMap,
                                               Class<? extends Model> clazz) {

        Map<String, Object> map = new HashMap<>();
        if (Objects.nonNull(dataMap)) {
            map.putAll(dataMap);
        }
        if (Objects.nonNull(oldMap)) {
            map.putAll(oldMap);
        }
        for (String f : TimeFields.fields) {
            if (Objects.nonNull(map) && map.containsKey(f) && Objects.nonNull(map.get(f))
                    && StringUtils.equals(TimeFields.ZERO_TIME, map.get(f).toString())) {
                map.put(f, null);
            }
        }
        Model oldRecord = JSON.parseObject(JSON.toJSONString(map), clazz);

        // 切换上面的逻辑简化旧数据获取
        /*Arrays.stream(TimeFields.fields)
                .filter(f -> oldMap.containsKey(f) && Objects.nonNull(oldMap.get(f))
                        && StringUtils.equals(TimeFields.ZERO_TIME, oldMap.get(f).toString()))
                .forEach(f -> oldMap.put(f, null));

        ReflectUtil.convert(oldRecord, oldMap, clazz);*/
        return new BasicModel<>(tableName, operate, oldRecord, null);
    }

    public static void main(String[] args) {
        /*String oldStr = "{\"gmtCreated\":\"2020-02-19T19:30:00\",\"gmtUpdated\":\"2020-02-19T19:30:00\",\"goodsName\":\"卡托普利片 25毫克*100片 常州制药厂有限公司\",\"id\":21760,\"spuId\":173766,\"status\":0,\"tradeCode\":\"69\n" +
                "42817000283\",\"urls\":\"http://wr-release.oss-cn-shanghai.aliyuncs.com/oss/upload/file/goods/1574994877110.jpg,http://wr-release.oss-cn-shanghai.aliyuncs.com/oss/upload/file/goods/\n" +
                "1574994879563.jpg\"}";
        String newStr = "{\"categoryId\":23,\"gmtCreated\":\"2020-02-19T19:30:00\",\"gmtUpdated\":\"2020-06-08T17:38:47\",\"goodsName\":\"卡托普利片 25毫克*100片 常州制药厂有限公司\",\"id\":21760,\"spuId\":173766,\"status\":0\n" +
                ",\"tradeCode\":\"6942817000283\",\"urls\":\"http://wr-release.oss-cn-shanghai.aliyuncs.com/oss/upload/file/goods/1574994877110.jpg,http://wr-release.oss-cn-shanghai.aliyuncs.com/oss/up\n" +
                "load/file/goods/1574994879563.jpg,http://wr-release.oss-cn-shanghai.aliyuncs.com/oss/upload/file/goods/1591609112003.jpg,http://wr-release.oss-cn-shanghai.aliyuncs.com/oss/uploa\n" +
                "d/file/goods/1591609117694.jpg,http://wr-release.oss-cn-shanghai.aliyuncs.com/oss/upload/file/goods/1591609134579.jpg\"}";
        StandardGoodsSyncrds o = JSON.parseObject(oldStr, StandardGoodsSyncrds.class);
        StandardGoodsSyncrds n = JSON.parseObject(newStr, StandardGoodsSyncrds.class);
        MutationResult result = new MutationResult();
        result.setTableName("gc_standard_goods_syncrds");
        result.setOldRecord(o);
        result.setNewRecord(n);
        result.setMutationType(CommonConstants.OPERATE_UPDATE);
        result.setModelClass(StandardGoodsSyncrds.class);
        boolean b = ReflectUtil.checkFieldsIsDiff(result);
        System.out.println(b);*/
        String str = "";
        System.out.println(str.substring(0, str.indexOf(SymbolConstants.HOR_LINE)));
        System.out.println(str.substring(str.indexOf(SymbolConstants.HOR_LINE) + 1));
    }
}
