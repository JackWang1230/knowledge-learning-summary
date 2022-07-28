package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.RedisConstant;
import cn.wr.collect.sync.hbase.HBaseService;
import cn.wr.collect.sync.model.BaseDataInitEvent;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.QueryField;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.gc.OrganizeBase;
import cn.wr.collect.sync.model.partner.PartnerStores;
import cn.wr.collect.sync.model.stock.StockMerchant;
import cn.wr.collect.sync.utils.HBaseUtils;
import cn.wr.collect.sync.utils.RedisPoolUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.wr.collect.sync.constants.CommonConstants.*;
import static cn.wr.collect.sync.constants.RedisConstant.*;

public class BasicInitSink extends RichSinkFunction<BaseDataInitEvent> {
    private static final long serialVersionUID = -168955840664973228L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicInitSink.class);

    private HBaseService hBaseService;
    private Jedis jedis;

    @Override
    public void open(Configuration parameters) throws Exception {
        LOGGER.info("BasicInitSink open");
        super.open(parameters);
        final ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        hBaseService = new HBaseService(HBaseUtils.getConnection(parameterTool));
        jedis = RedisPoolUtil.getInstance(parameterTool);
    }

    @Override
    public void invoke(BaseDataInitEvent baseDataInitEvent, Context context) {
        long start = System.currentTimeMillis();
        Pipeline pipeline = jedis.pipelined();
        Class modelClass = baseDataInitEvent.getModelClass();
        List<Model> models = baseDataInitEvent.getModels();
        // 获取实体类所有字段
        Field[] fields = modelClass.getDeclaredFields();
        // 过滤 留下QueryField注解标注的字段,按order排序
        List<Field> fieldList = Stream.of(fields).filter(e -> e.isAnnotationPresent(QueryField.class)).sorted(Comparator.comparingInt(e -> {
            QueryField queryField = e.getAnnotation(QueryField.class);
            return queryField.order();
        })).collect(Collectors.toList());
        // 获取字段对应的getter方法
        List<Method> methodList = new ArrayList<>();
        fieldList.stream().forEach(e -> {
            try {
                methodList.add(new PropertyDescriptor(e.getName(), baseDataInitEvent.getModelClass()).getReadMethod());
            } catch (IntrospectionException ex) {
                LOGGER.error("字段没有getter方法!field: {}", e.getName());
            }
        });

        // 需要存储到Hbase中的表
        Table.ToHbaseTable[] toHbaseTables = Table.ToHbaseTable.values();
        List<String> toHbaseTableNames = Stream.of(toHbaseTables).parallel().map(Table.ToHbaseTable::name).collect(Collectors.toList());
        // 存hbase
        if (toHbaseTableNames.contains(baseDataInitEvent.getTableName())) {
            if (Table.ToHbaseTable.partner_store_goods.name().equals(baseDataInitEvent.getTableName())) {
                // 索引字段和表记录一对一,直接存
                hBaseService.batchPut(HBASE_PARTNER_STORE_GOODS, models, fieldList, methodList);
            } /*else if (Table.ToHbaseTable.gc_goods_sales_statistics_merchant.name().equals(baseDataInitEvent.getTableName())) {
                hBaseService.batchPut(HBASE_GC_GOODS_SALES_STATISTICS_MERCHANT, models, fieldList, methodList);
                // 索引字段和表记录可能一对多,quantity累加
                // 分组
            }*/ else if (Table.ToHbaseTable.partner_goods.name().equals(baseDataInitEvent.getTableName())) {
                // 索引字段和表记录一对一,直接存
                hBaseService.batchPut(HBASE_PARTNER_GOODS, models, fieldList, methodList);
            }
            else if (Table.ToHbaseTable.platform_goods.name().equals(baseDataInitEvent.getTableName())) {
                hBaseService.batchPut(HBASE_PLATFORM_GOODS, models, fieldList, methodList);
            }
            else if (Table.ToHbaseTable.gc_config_sku.name().equals(baseDataInitEvent.getTableName())) {
                hBaseService.batchPut(HBASE_GC_CONFIG_SKU, models, fieldList, methodList);
            }
            else if (Table.ToHbaseTable.stock_goods.name().equals(baseDataInitEvent.getTableName())) {
                hBaseService.batchPut(HBASE_STOCK_GOODS, models, fieldList, methodList);
            }
        } else {
            if (Table.BaseDataTable.partner_stores.name().equals(baseDataInitEvent.getTableName())) {
                dealPartnerStores(baseDataInitEvent, pipeline);
                long end = System.currentTimeMillis();
                LOGGER.info("BasicInitSink invoke out.... time:{} (ms)", end - start);
                return;
            }

            if (Table.BaseDataTable.stock_merchant.name().equals(baseDataInitEvent.getTableName())) {
                for (Model model : baseDataInitEvent.getModels()) {
                     StockMerchant stockMerchant = (StockMerchant) model;
                     pipeline.set(RedisConstant.COLLECT_CACHE_STOCK_MERCHANT + "rootId" + stockMerchant.getRootId()+"organizationId" + stockMerchant.getOrganizationId(), JSON.toJSONString(stockMerchant));
                }

                pipeline.sync();
                long end = System.currentTimeMillis();
                LOGGER.info("BasicInitSink invoke out.... time:{} (ms)", end - start);
                return;
            }

            // 特殊处理 parnter_goods_search_priority 多个字段合并作为key
            // 处理要作为查询条件的字段
            /*else */
            if (fieldList.size() == 1) {
                // 按字段1分组
                Map<String, List<String>> field1ModelMap = models.stream().filter(e -> {
                    try {
                        return methodList.get(0).invoke(e) != null;
                    } catch (Exception ex) {
                        LOGGER.error("执行getter方法失败!field: {}", fieldList.get(0).getName());
                    }
                    return false;
                }).collect(Collectors.groupingBy(e -> {
                    try {
                        return methodList.get(0).invoke(e).toString();
                    } catch (Exception ex) {
                        LOGGER.error("执行getter方法失败!field: {}", fieldList.get(0).getName());
                    }
                    return null;
                }, Collectors.mapping(e -> JSON.toJSONString(e), Collectors.toList())));

                field1ModelMap.entrySet().stream().forEach(e -> {
                    pipeline.set(getKey(baseDataInitEvent.getTableName(), fieldList.get(0).getName(), e.getKey()), e.getValue().get(0));
                });

            } else if (fieldList.size() >= 2) {
                // 按字段1 2两层分组
                Map<String, Map<String, String>> fieldsModelMap = models.stream().filter(e -> {
                    try {
                        return methodList.get(0).invoke(e) != null && methodList.get(1).invoke(e) != null;
                    } catch (Exception ex) {
                        LOGGER.error("过滤 error:{}", fieldList.get(0).getName());
                    }
                    return false;
                }).collect(Collectors.groupingBy(e -> {
                    try {
                        return methodList.get(0).invoke(e).toString();
                    } catch (Exception ex) {
                        LOGGER.error("执行getter方法失败!field: {}", fieldList.get(0).getName());
                    }
                    return null;
                }, Collectors.groupingBy(e -> {
                    try {
                        return fieldList.get(1).getName() + methodList.get(1).invoke(e).toString();
                    } catch (Exception ex) {
                        LOGGER.error("执行getter方法失败!field: {}", fieldList.get(1).getName());
                    }
                    return null;
                }, java.util.stream.Collector.of(
                        () -> new ArrayList<String>(),          // supplier
                        (j, p) -> j.add(JSON.toJSONString(p)),  // accumulator
                        (j1, j2) -> {
                            j1.addAll(j2);
                            return j1;
                        },               // combiner
                        ArrayList::toString
                ))));

                try {
                    if (Table.BaseDataTable.gc_partner_stores_all.name().equals(baseDataInitEvent.getTableName())) {
                        // 处理成hash结构存储到redis中
                        fieldsModelMap.entrySet().stream().forEach(e -> {
                            pipeline.hmset(getKey(baseDataInitEvent.getTableName(), fieldList.get(0).getName(), e.getKey()),
                                    e.getValue());
                        });
                    }
                    else if (Table.BaseDataTable.pgc_store_info.name().equals(baseDataInitEvent.getTableName())) {
                        fieldsModelMap.entrySet().stream().forEach(e -> {
                            e.getValue().entrySet().stream().forEach(i -> {
                                List<String> l = JSON.parseArray(i.getValue(), String.class);
                                if (CollectionUtils.isNotEmpty(l)) {
                                    pipeline.set(getKey(baseDataInitEvent.getTableName(), fieldList.get(0).getName(), e.getKey())
                                                    + REDIS_TABLE_SEPARATOR + i.getKey(),
                                            l.get(0), "NX", "EX", 60 * 60 * 24 * 2);
                                }
                            });
                        });
                    }
                    else {
                        // 处理成hash结构存储到redis中
                        fieldsModelMap.entrySet().stream().forEach(e -> {
                            e.getValue().entrySet().stream().forEach(i -> {
                                List<String> l = JSON.parseArray(i.getValue(), String.class);
                                i.setValue(l.get(0));
                            });
                            pipeline.hmset(getKey(baseDataInitEvent.getTableName(), fieldList.get(0).getName(), e.getKey()),
                                    e.getValue());

                        });
                    }
                } catch (Exception e) {
                    LOGGER.error("数据缓存redis异常， error: {}", e);
                }
            }
        }

        String tableName = baseDataInitEvent.getTableName();
        if (StringUtils.equals(tableName, Table.BaseDataTable.organize_base.name())) {
            for (Model model : baseDataInitEvent.getModels()) {
                OrganizeBase organizeBase = (OrganizeBase) model;
                pipeline.set(RedisConstant.COLLECT_CACHE_ORGANIZE_BASE_STORE + organizeBase.getOrganizationId(), JSON.toJSONString(organizeBase));
            }
        }

        pipeline.sync();
        long end = System.currentTimeMillis();
        LOGGER.info("BasicInitSink invoke out.... time:{} (ms)", end - start);
    }


    @Override
    public void close() throws Exception {
        super.close();
        if (null != hBaseService) {
            hBaseService.closeConnection();
        }
        RedisPoolUtil.closeConn(jedis);
        RedisPoolUtil.closePool();
        LOGGER.info("BasicInitSink close");
    }

    /**
     * 获取key
     *
     * @param tableName
     * @param fieldName
     * @param value
     * @return
     */
    private String getKey(String tableName, String fieldName, String value) {
        return RedisConstant.REDIS_TABLE_PREFIX + tableName + REDIS_TABLE_SEPARATOR + fieldName + value;
    }

    /**
     * 初始化 wr_partner.partner_stores 缓存redis
     * @param init
     * @param pipeline
     */
    private void dealPartnerStores(BaseDataInitEvent init, Pipeline pipeline) {
        String key = COLLECT_CACHE_PARTNER_STORES + "dbId%sgroupId%s";
        String field = "internalId%s";
        init.getModels().forEach(model -> {
            PartnerStores data = (PartnerStores) model;
            pipeline.hset(String.format(key, data.getDbId(), data.getGroupId()),
                    String.format(field, data.getInternalId()), JSON.toJSONString(data));
        });
        pipeline.sync();
    }
}
