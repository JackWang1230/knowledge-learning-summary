package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.RedisConstant;
import cn.wr.collect.sync.hbase.HBaseService;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.gc.PartnerStoresAll;
import cn.wr.collect.sync.model.partner.PartnerStores;
import cn.wr.collect.sync.model.stock.StockMerchant;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.HBaseUtils;
import cn.wr.collect.sync.utils.StoreUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.wr.collect.sync.constants.CommonConstants.*;
import static cn.wr.collect.sync.constants.RedisConstant.*;

public class BasicSyncSinkV2 extends RichSinkFunction<BasicModel<Model>>  {
    private static final long serialVersionUID = 2056362378994425505L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicSyncSinkV2.class);
    private RedisService redisService;
    private HBaseService hBaseService;

    @Override
    public void open(Configuration parameters) throws Exception {
        LOGGER.info("BasicSyncSinkV2 open");
        super.open(parameters);
        final ParameterTool parameterTool = (ParameterTool) getRuntimeContext()
                .getExecutionConfig().getGlobalJobParameters();
        redisService = new RedisService(parameterTool);
        hBaseService = new HBaseService(HBaseUtils.getConnection(parameterTool));

    }

    @Override
    public void close() throws Exception {
        super.close();
        LOGGER.info("BasicSyncSinkV2 close");
        if (null != hBaseService) {
            hBaseService.closeConnection();
        }
    }

    @Override
    public void invoke(BasicModel<Model> model, Context context) throws Exception {
        if (Objects.isNull(model) || StringUtils.isBlank(model.getTableName()) ||
                StringUtils.isBlank(model.getOperate()) || Objects.isNull(model.getData())) {
            return;
        }
        LOGGER.info("BasicSyncSinkV2 operate:{}, table:{}, data: {}", model.getOperate(), model.getTableName(),
                JSON.toJSONString(model));

        // 获取实体类所有字段
        Field[] fields = model.getData().getClass().getDeclaredFields();

        // 过滤 留下QueryField注解标注的字段,按order排序
        List<Field> fieldList = Stream.of(fields).filter(e -> e.isAnnotationPresent(QueryField.class)).sorted(Comparator.comparingInt(e -> {
            QueryField queryField = e.getAnnotation(QueryField.class);
            return queryField.order();
        })).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(fieldList)) {
            return;
        }

        // 获取字段对应的getter方法
        List<Method> methodList = fieldList.stream().map(e -> {
            try {
                return new PropertyDescriptor(e.getName(), model.getData().getClass()).getReadMethod();
            } catch (IntrospectionException ex) {
                LOGGER.error("BasicSyncSinkV2 method IntrospectionException, field: {}, error:{}", e.getName(), e);
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(methodList)) {
            return;
        }

        Table.ToHbaseTable[] toHbaseTables = Table.ToHbaseTable.values();

        List<String> toHbaseTableNames = Stream.of(toHbaseTables).parallel().map(Table.ToHbaseTable::name).collect(Collectors.toList());

        if (Table.BaseDataTable.gc_partner_stores_all.name().equals(model.getTableName())) {
            this.dealGcPartnerStoresAll(model, fieldList.get(0), methodList.get(0), fieldList.get(1), methodList.get(1));
        }
        else if (Table.BaseDataTable.partner_stores.name().equals(model.getTableName())) {
            this.dealPartnerStores(model);
        }
        else if (Table.BaseDataTable.pgc_store_info.name().equals(model.getTableName())) {
            this.dealPgcStoreInfo(model, fieldList, methodList);
        }
        else if (Table.BaseDataTable.stock_merchant.name().equals(model.getTableName())) {
            this.dealStockMerchant(model);
        }
        else if (toHbaseTableNames.contains(model.getTableName())) {
            this.dealHBase(model, fieldList, methodList);
        }
        else {
            this.dealRedis(model, fieldList, methodList);
        }
    }

    /**
     * 处理redis数据
     * @param model
     * @param fieldList
     * @param methodList
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void dealRedis(BasicModel<Model> model, List<Field> fieldList, List<Method> methodList) throws InvocationTargetException, IllegalAccessException {
        switch (model.getOperate()) {
            case OPERATE_INSERT:
            case OPERATE_UPDATE:
                if (fieldList.size() == 1) {
                    // 添加新纪录
                    addRecord(model.getTableName(), model.getData(), fieldList.get(0), methodList.get(0));
                } else if (fieldList.size() >= 2) {
                    // 添加新纪录
                    addHashRecord(model.getTableName(), model.getData(), fieldList.get(0), methodList.get(0), fieldList.get(1), methodList.get(1));
                }
                break;

            case OPERATE_DELETE:
            case OPERATE_UPDATE_DELETE:
                // 删除操作
                if (fieldList.size() == 1) {
                    // 移除旧记录
                    deleteRecord(model.getTableName(), model.getData(), fieldList.get(0), methodList.get(0));
                } else if (fieldList.size() >= 2) {
                    // 删除旧记录
                    deleteHashRecord(model.getTableName(), model.getData(), fieldList.get(0), methodList.get(0), fieldList.get(1), methodList.get(1));
                }
                break;
        }

    }

    /**
     * 处理hbase数据
     *
     * @param model
     * @param fieldList
     * @param methodList
     */
    private void dealHBase(BasicModel<Model> model, List<Field> fieldList, List<Method> methodList) {

        switch (model.getOperate()) {
            case OPERATE_DELETE:
            case OPERATE_UPDATE_DELETE:
                LOGGER.info("[DELETE] delete old record, table:{}, record:{}", model.getTableName(), JSON.toJSONString(model));
                hBaseService.delete(HBASE_PREFIX + model.getTableName(),
                        StoreUtil.buildHBaseStoreKey(model.getTableName(), fieldList, methodList, model.getData()));
                break;

            case OPERATE_INSERT:
            case OPERATE_UPDATE:
                hBaseService.put(HBASE_PREFIX + model.getTableName(),
                        StoreUtil.buildHBaseStoreKey(model.getTableName(), fieldList, methodList, model.getData()),
                        JSON.toJSONString(model.getData()));
                break;
            default:
                break;
        }
    }

    /**
     * 处理特殊表数据
     *
     * @param model
     * @param field1
     * @param method1
     * @param field2
     * @param method2
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void dealGcPartnerStoresAll(BasicModel<Model> model, Field field1, Method method1, Field field2, Method method2)
            throws InvocationTargetException, IllegalAccessException {

        switch (model.getOperate()) {
            case CommonConstants.OPERATE_DELETE:
            case CommonConstants.OPERATE_UPDATE_DELETE:
                PartnerStoresAll psa01 = (PartnerStoresAll) model.getData();
                List<PartnerStoresAll> list01 = redisService.queryPartnerStoresAll(psa01.getDbId(), psa01.getGroupId());
                List<PartnerStoresAll> collect01 = list01.stream()
                        .filter(item -> !psa01.getId().equals(item.getId()))
                        .collect(Collectors.toList());
                // 删除后判断是否还有门店，如果没有，则删除key，如果有，则重新写入redis
                if (CollectionUtils.isNotEmpty(collect01)) {
                    redisService.addHash(REDIS_TABLE_PREFIX + model.getTableName() +
                                    RedisConstant.REDIS_TABLE_SEPARATOR + field1.getName() + method1.invoke(psa01),
                            field2.getName() + method2.invoke(psa01),
                            JSON.toJSONString(collect01));
                } else {
                    redisService.deleteHash(REDIS_TABLE_PREFIX + model.getTableName() +
                                    RedisConstant.REDIS_TABLE_SEPARATOR + field1.getName() + method1.invoke(psa01),
                            field2.getName() + method2.invoke(psa01));
                }
                break;

            case OPERATE_INSERT:
                PartnerStoresAll psa02 = (PartnerStoresAll) model.getData();
                List<PartnerStoresAll> list02 = redisService.queryPartnerStoresAll(psa02.getDbId(), psa02.getGroupId());
                if (CollectionUtils.isEmpty(list02)) {
                    list02 = new ArrayList<>(1);
                }
                list02.add(psa02);
                redisService.addHash(REDIS_TABLE_PREFIX + model.getTableName() +
                                RedisConstant.REDIS_TABLE_SEPARATOR + field1.getName() + method1.invoke(psa02),
                        field2.getName() + method2.invoke(psa02),
                        JSON.toJSONString(list02));
                break;

            case OPERATE_UPDATE:
                PartnerStoresAll newRecord = (PartnerStoresAll) model.getData();
                List<PartnerStoresAll> newList = redisService.queryPartnerStoresAll(newRecord.getDbId(), newRecord.getGroupId());
                List<PartnerStoresAll> collect02 = newList.stream()
                        .filter(item -> !newRecord.getId().equals(item.getId()))
                        .collect(Collectors.toList());
                collect02.add(newRecord);
                // 新数据写入redis
                redisService.addHash(REDIS_TABLE_PREFIX + model.getTableName() +
                                RedisConstant.REDIS_TABLE_SEPARATOR + field1.getName() + method1.invoke(newRecord),
                        field2.getName() + method2.invoke(newRecord),
                        JSON.toJSONString(collect02));
                break;
        }
    }

    /**
     * 处理 wr_partners.partner_stores 写入redis
     * @param model
     */
    private void dealPartnerStores(BasicModel<Model> model) {
        String key = COLLECT_CACHE_PARTNER_STORES + "dbId%sgroupId%s";
        String field = "internalId%s";
        PartnerStores data = (PartnerStores) model.getData();
        switch (model.getOperate()) {
            case OPERATE_UPDATE_DELETE:
            case OPERATE_DELETE:
                redisService.deleteHash(String.format(key, data.getDbId(), data.getGroupId()),
                        String.format(field, data.getInternalId()));
                break;

            case OPERATE_UPDATE:
            case OPERATE_INSERT:
                redisService.addHash(String.format(key, data.getDbId(), data.getGroupId()),
                        String.format(field, data.getInternalId()), JSON.toJSONString(data));
                break;
            default:
                break;
        }
    }

    /**
     * 处理 stock_merchant 写入缓存
     * @param model
     */
    private void dealStockMerchant(BasicModel<Model> model) {
        StockMerchant stockMerchant = (StockMerchant) model.getData();

        String key = COLLECT_CACHE_STOCK_MERCHANT + "rootId" + stockMerchant.getRootId() + "organizationId" + String.valueOf(stockMerchant.getOrganizationId());

        switch (model.getOperate()) {
            case OPERATE_UPDATE_DELETE:
            case OPERATE_DELETE:
                redisService.delete(key);
                break;

            case OPERATE_UPDATE:
            case OPERATE_INSERT:
                redisService.set(key, JSON.toJSONString(model.getData()));
                break;
            default:
                break;
        }
    }

    /**
     * 处理pgcStoreInfo写入redis
     * @param model
     * @param fieldList
     * @param methodList
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void dealPgcStoreInfo(BasicModel<Model> model, List<Field> fieldList, List<Method> methodList)
            throws InvocationTargetException, IllegalAccessException {
        switch (model.getOperate()) {
            case OPERATE_UPDATE_DELETE:
            case OPERATE_DELETE:
                redisService.delete(this.getKey(model.getTableName(), model.getData(), fieldList, methodList));
                break;

            case OPERATE_UPDATE:
            case OPERATE_INSERT:
                redisService.set(this.getKey(model.getTableName(), model.getData(), fieldList, methodList),
                        JSON.toJSONString(model.getData()), "EX", 60 * 60 * 24 * 2);
                break;
            default:
                break;
        }
    }

    /**
     * 删除记录,set结构
     * @param tableName
     * @param model
     * @param field
     * @param method
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void deleteRecord(String tableName, Model model, Field field, Method method) throws InvocationTargetException, IllegalAccessException {
        if (null == model) {
            LOGGER.info("BasicSyncSink deleteRecord model is null");
            return;
        }
        redisService.delete(REDIS_TABLE_PREFIX + tableName
                + RedisConstant.REDIS_TABLE_SEPARATOR + field.getName()
                + method.invoke(model));
    }

    /**
     * 新增记录
     *
     * @param tableName
     * @param model
     * @param field
     * @param method
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void addRecord(String tableName, Model model, Field field, Method method) throws InvocationTargetException, IllegalAccessException {
        if (null != model && null != method.invoke(model)) {
            redisService.set(REDIS_TABLE_PREFIX + tableName +
                            RedisConstant.REDIS_TABLE_SEPARATOR + field.getName() + method.invoke(model),
                    JSON.toJSONString(model));
        }
    }

    /**
     * 删除记录 hash结构
     * @param tableName
     * @param model
     * @param field1
     * @param method1
     * @param field2
     * @param method2
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void deleteHashRecord(String tableName, Model model, Field field1, Method method1, Field field2, Method method2) throws InvocationTargetException, IllegalAccessException {
        if (Objects.isNull(model)) {
            LOGGER.info("BasicSyncSinkV2.deleteHashRecord model is null ");
            return;
        }

        redisService.deleteHash(REDIS_TABLE_PREFIX + tableName + RedisConstant.REDIS_TABLE_SEPARATOR +
                        field1.getName() + method1.invoke(model),
                field2.getName() + method2.invoke(model));
    }

    /**
     * 新增记录 hash结构
     * @param tableName
     * @param model
     * @param field1
     * @param method1
     * @param field2
     * @param method2
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void addHashRecord(String tableName, Model model, Field field1, Method method1, Field field2, Method method2)
            throws InvocationTargetException, IllegalAccessException {
        if (null != method1.invoke(model) && null != method2.invoke(model)) {
            redisService.addHash(REDIS_TABLE_PREFIX + tableName +
                            RedisConstant.REDIS_TABLE_SEPARATOR + field1.getName() + method1.invoke(model),
                    field2.getName() + method2.invoke(model),
                    JSON.toJSONString(model));
        }
    }

    /**
     * 获取  key 多个字段拼接
     * @param tableName
     * @param model
     * @param fieldList
     * @param methodList
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private String getKey(String tableName, Model model, List<Field> fieldList, List<Method> methodList)
            throws InvocationTargetException, IllegalAccessException {
        String key = REDIS_TABLE_PREFIX + tableName;
        for (int i = 0; i < fieldList.size(); i++) {
            key += (REDIS_TABLE_SEPARATOR + fieldList.get(i).getName() + methodList.get(i).invoke(model));
        }
        return key;
    }
}
