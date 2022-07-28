package cn.wr.collect.sync.hbase;

import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.gc.*;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import cn.wr.collect.sync.model.stock.StockGoods;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import cn.wr.collect.sync.utils.HBaseUtils;
import cn.wr.collect.sync.utils.StoreUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.CommonConstants.*;
import static cn.wr.collect.sync.constants.RedisConstant.HBASE_FAMILY_NAME;
import static cn.wr.collect.sync.constants.RedisConstant.HBASE_QUALIFIER;

public class HBaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HBaseService.class);
    private Connection connection;

    public HBaseService(Connection connection) {
        this.connection = connection;
    }

    /**
     * 删除
     * @param tableName
     * @param key
     */
    public void delete(String tableName, String key) {
        long start = System.currentTimeMillis();
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            Delete delete = new Delete(key.getBytes());
            table.delete(delete);
        } catch (Exception e) {
            LOGGER.error("HBaseService delete error:{}", e);
        } finally {
            long end = System.currentTimeMillis();
            LOGGER.info("HBaseService delete tableName:{}, key:{}, time:{}", tableName, key, (end - start));
            HBaseUtils.close(table, null, null);
        }
    }

    /**
     * 新增/更新
     *
     * @param tableName
     * @param key
     * @param data
     */
    public void put(String tableName, String key, String data) {
        // long start = System.currentTimeMillis();
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            Put put = this.getPut(key, data);
            if (Objects.nonNull(put)) {
                table.put(put);
            }
        } catch (Exception e) {
            LOGGER.error("HBaseService put error:{}", e);
        } finally {
            // long end = System.currentTimeMillis();
            // LOGGER.info("HBaseService put tableName:{}, key:{}, time:{}", tableName, key, (end - start));
            HBaseUtils.close(table, null, null);
        }
    }

    /**
     * hbase 新增/更新
     * @param key
     * @param data
     */
    /*public void put(String key, Object data) {
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(HBASE_PLATFORM_GOODS));
            Put put = this.getPut(key, data);
            if (Objects.nonNull(put)) {
                table.put(put);
            }
        } catch (Exception e) {
            LOGGER.error("HBaseService put error:{}", e);
        } finally {
            HBaseUtils.close(table, null, null);
        }
    }*/

    /**
     * 获取put
     * @param key
     * @param data
     * @return
     */
    public Put getPut(String key, String data) {
        Put put = new Put(Bytes.toBytes(key));
        put.addColumn(Bytes.toBytes(HBASE_FAMILY_NAME), Bytes.toBytes(HBASE_QUALIFIER), Bytes.toBytes(data));
        return put;
    }

    /**
     * 获取put
     * @param key
     * @param data
     * @return
     */
    /*public Put getPut(String key, Object data) {
        Put put = new Put(Bytes.toBytes(key));
        try {
            Field[] declaredFields = data.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (StringUtils.equals(CommonConstants.SERIAL_VERSION_UID, field.getName())) {
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(data);
                if (Objects.isNull(value)) {
                    put.addColumn(Bytes.toBytes(HBASE_FAMILY_NAME), Bytes.toBytes(field.getName()), null);
                }
                else {
                    if (field.getType() == LocalDateTime.class) {
                        put.addColumn(Bytes.toBytes(HBASE_FAMILY_NAME), Bytes.toBytes(field.getName()), Bytes.toBytes(DateUtils.format((LocalDateTime) value)));
                    }
                    else if (field.getType() == Date.class) {
                        put.addColumn(Bytes.toBytes(HBASE_FAMILY_NAME), Bytes.toBytes(field.getName()), Bytes.toBytes(DateUtils.format((Date) value)));
                    }
                    else {
                        put.addColumn(Bytes.toBytes(HBASE_FAMILY_NAME), Bytes.toBytes(field.getName()), Bytes.toBytes(String.valueOf(value)));
                    }
                }
            }
            return put;
        }
        catch (Exception e) {
            LOGGER.error("key:{}, data:{}, Exception: {}", key, JSON.toJSONString(data), e);
        }
        return null;
    }*/

    /**
     * 批量写入
     * @param tableName
     * @param models
     * @param fieldList
     * @param methodList
     */
    public void batchPut(String tableName, List<Model> models, List<Field> fieldList, List<Method> methodList) {
        if (CollectionUtils.isEmpty(models)) {
            LOGGER.info("HBaseService batchPut models is empty, tableName:{}", tableName);
            return;
        }
        /*long start = System.currentTimeMillis();*/
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            List<Put> putList = models.stream()
                    .map(item -> this.getPut(StoreUtil.buildHBaseStoreKey(tableName, fieldList, methodList, item), JSON.toJSONString(item)))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(putList)) {
                table.put(putList);
            }
        } catch (Exception e) {
            LOGGER.error("HBaseService batch put models error:{}", e);
        } finally {
            /*long end = System.currentTimeMillis();
            LOGGER.info("HBaseService batchPut models tableName:{}, size:{}, time:{}(ms)", tableName, models.size(), end - start);*/
            HBaseUtils.close(table, null, null);
        }
    }

    /**
     * 批量写入
     * @param tableName
     * @param putList
     */
    public void batchPut(String tableName, List<Put> putList) {
        if (CollectionUtils.isEmpty(putList)) {
//            LOGGER.info("HBaseService batchPut putList is empty, tableName:{}", tableName);
            return;
        }
//        LOGGER.info("HBaseService batchPut putList tableName:{}, size:{}", tableName, putList.size());
        /*long start = System.currentTimeMillis();*/
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            table.put(putList);
        } catch (Exception e) {
            LOGGER.error("HBaseService batch put error:{}", e);
        } finally {
            /*long end = System.currentTimeMillis();
            LOGGER.info("HBaseService batchPut putList tableName:{}, size:{}, time:{}(ms)", tableName, putList.size(), end - start);*/
            HBaseUtils.close(table, null, null);
        }

    }

    /**
     * 查询 partner_goods
     *
     * @param dbId
     * @param internalId
     * @return
     */
    public PartnerGoods queryPartnerGoods(Integer dbId, String internalId) {
        PartnerGoods goods = null;
        String rowKey = dbId + "-" + internalId;
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(HBASE_PARTNER_GOODS));
            Get get = new Get(Bytes.toBytes(rowKey));
            Result rs = table.get(get);
            Cell[] cells = rs.rawCells();
            if (null != cells && cells.length > 0) {
                String value = Bytes.toString(cells[0].getValueArray(), cells[0].getValueOffset(), cells[0].getValueLength());
                goods = JSON.parseObject(value, PartnerGoods.class);
            }
        } catch (Exception e) {
            LOGGER.error("HBaseService query IOException error: {}", e);
        } finally {
            HBaseUtils.close(table, null, null);
        }
        return goods;
    }

    /**
     * 查询 stock_goods
     * @param merchantId
     * @param storeId
     * @param internalId
     * @return
     */
    public StockGoods queryStockGoods(Long merchantId, Long storeId, String internalId) {
        StockGoods stockGoods = null;
        String rowKey = merchantId + SymbolConstants.HOR_LINE + storeId + SymbolConstants.HOR_LINE + internalId;
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(HBASE_STOCK_GOODS));
            Get get = new Get(Bytes.toBytes(rowKey));
            Result rs = table.get(get);
            Cell[] cells = rs.rawCells();
            if (null != cells && cells.length > 0) {
                String value = Bytes.toString(cells[0].getValueArray(), cells[0].getValueOffset(), cells[0].getValueLength());
                stockGoods = JSON.parseObject(value, StockGoods.class);
            }
        } catch (Exception e) {
            LOGGER.error("HBaseService query IOException error: {}", e);
        } finally {
            HBaseUtils.close(table, null, null);
        }
        return stockGoods;
    }

    /**
     * 查询 partner_store_goods
     *
     * @param dbId
     * @param groupId
     * @param internalId
     * @return
     */
    public PartnerStoreGoods queryPartnerStoreGoods(Integer dbId, String groupId, String internalId) {
        PartnerStoreGoods storeGoods = null;
        String rowKey = dbId + "-" + groupId + "-" + internalId;
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(HBASE_PARTNER_STORE_GOODS));
            Get get = new Get(Bytes.toBytes(rowKey));
            Result rs = table.get(get);
            Cell[] cells = rs.rawCells();
            if (null != cells && cells.length > 0) {
                String value = Bytes.toString(cells[0].getValueArray(), cells[0].getValueOffset(), cells[0].getValueLength());
                storeGoods = JSON.parseObject(value, PartnerStoreGoods.class);
            }
        } catch (Exception e) {
            LOGGER.error("HBaseService query IOException error: {}", e);
        } finally {
            HBaseUtils.close(table, null, null);
        }
        return storeGoods;
    }

    /**
     * 批量查询
     * @param rowkeyList
     * @return
     */
    public List<PartnerStoreGoods> queryPartnerStoreGoods(List<String> rowkeyList) {
        if (CollectionUtils.isEmpty(rowkeyList)) {
            LOGGER.info("HBaseService queryPartnerStoreGoods rowkey is empty, return");
            return Collections.emptyList();
        }
        Table table = null;
        List<PartnerStoreGoods> storeGoodsList = new ArrayList<>();
        try {
            List<Get> getList = new ArrayList<>();
            table = connection.getTable(TableName.valueOf(HBASE_PARTNER_STORE_GOODS));
            // 把rowkey加到get里，再把get装到list中
            for (String rowkey : rowkeyList) {
                Get get = new Get(Bytes.toBytes(rowkey));
                getList.add(get);
            }
            Result[] results = table.get(getList);
            for (Result rs : results) {
                Cell[] cells = rs.rawCells();
                if (null != cells && cells.length > 0) {
                    String value = Bytes.toString(cells[0].getValueArray(), cells[0].getValueOffset(), cells[0].getValueLength());
                    storeGoodsList.add(JSON.parseObject(value, PartnerStoreGoods.class));
                }
            }
        } catch (Exception e) {
            LOGGER.error("HBaseService query IOException error: {}", e);
        } finally {
            HBaseUtils.close(table, null, null);
        }
        return storeGoodsList;
    }

    /**
     * 查询 gc_goods_sales_statistics_merchant
     *
     * @param merchantId
     * @param internalId
     * @return
     */
    public GoodsSalesStatisticsMerchant queryGcGoodsSalesStatisticsMerchant(Integer merchantId, String internalId) {
        GoodsSalesStatisticsMerchant statisticsMerchant = null;
        String rowKey = merchantId + "-" + internalId;
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(HBASE_GC_GOODS_SALES_STATISTICS_MERCHANT));
            Get get = new Get(Bytes.toBytes(rowKey));
            Result rs = table.get(get);
            Cell[] cells = rs.rawCells();
            if (null != cells && cells.length > 0) {
                String value = Bytes.toString(cells[0].getValueArray(), cells[0].getValueOffset(), cells[0].getValueLength());
                statisticsMerchant = JSON.parseObject(value, GoodsSalesStatisticsMerchant.class);
            }
        } catch (Exception e) {
            LOGGER.error("HBaseService query IOException error: {}", e);
        } finally {
            HBaseUtils.close(table, null, null);
        }
        return statisticsMerchant;
    }

    /**
     * 查询销量
     * @param merchantId
     * @param internalId
     * @return
     */
    public GoodsSales queryGoodsSales(Integer merchantId, String internalId) {
        if (Objects.isNull(merchantId) || StringUtils.isBlank(internalId)) {
            return null;
        }
        GoodsSales goodsSales = null;
        String rowKey = merchantId + "-" + internalId;
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(HBASE_GOODS_SALES));
            Get get = new Get(Bytes.toBytes(rowKey));
            Result rs = table.get(get);
            Cell[] cells = rs.rawCells();
            if (null != cells && cells.length > 0) {
                String value = Bytes.toString(cells[0].getValueArray(), cells[0].getValueOffset(), cells[0].getValueLength());
                if (StringUtils.isNotBlank(value) && !StringUtils.equals("null", value)) {
                    goodsSales = new GoodsSales(Double.valueOf(value));
                }
            }
        } catch (Exception e) {
            LOGGER.error("HBaseService queryGoodsSales IOException error: {}", e);
        } finally {
            HBaseUtils.close(table, null, null);
        }
        return goodsSales;
    }

    /**
     * 查询全连锁销量
     * @param tradeCode
     * @return
     */
    public GoodsFullSales queryGoodsFullSales(String tradeCode) {
        if (StringUtils.isBlank(tradeCode)) {
            return null;
        }
        GoodsFullSales goodsFullSales = null;
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(HBASE_GOODS_FULL_SALES));
            Get get = new Get(Bytes.toBytes(tradeCode));
            Result rs = table.get(get);
            Cell[] cells = rs.rawCells();
            if (null != cells && cells.length > 0) {
                String value = Bytes.toString(cells[0].getValueArray(), cells[0].getValueOffset(), cells[0].getValueLength());
                if (StringUtils.isNotBlank(value) && !StringUtils.equals("null", value)) {
                    goodsFullSales = new GoodsFullSales(Double.valueOf(value));
                }
            }
        } catch (Exception e) {
            LOGGER.error("HBaseService queryGoodsFullSales IOException error: {}", e);
        } finally {
            HBaseUtils.close(table, null, null);
        }
        return goodsFullSales;
    }


    /**
     * 根据skuCode查询商品上下架
     * @param skuCode
     * @return
     */
    public PlatformGoods queryPlatformGoods(String skuCode) {
        if (StringUtils.isBlank(skuCode)) {
            return null;
        }
        PlatformGoods platformGoods = null;
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(HBASE_PLATFORM_GOODS));
            Get get = new Get(Bytes.toBytes(skuCode));
            Result rs = table.get(get);
            Cell[] cells = rs.rawCells();

            if (null != cells && cells.length > 0) {
                String value = Bytes.toString(cells[0].getValueArray(), cells[0].getValueOffset(), cells[0].getValueLength());
                platformGoods = JSON.parseObject(value, PlatformGoods.class);
            }
        } catch (Exception e) {
            LOGGER.error("HBaseService query IOException error: {}", e);
        } finally {
            HBaseUtils.close(table, null, null);
        }
        return platformGoods;
    }

    /**
     * gc_config_sku
     * @param merchantId
     * @param goodsInternalId
     * @return
     */
    public ConfigSku queryConfigSku(Integer merchantId, String goodsInternalId) {
        if (Objects.isNull(merchantId) || StringUtils.isBlank(goodsInternalId)) {
            return null;
        }
        ConfigSku configSku = null;
        Table table = null;
        try {
            String rowKey = merchantId + "-" + goodsInternalId;
            table = connection.getTable(TableName.valueOf(HBASE_GC_CONFIG_SKU));
            Get get = new Get(Bytes.toBytes(rowKey));
            Result rs = table.get(get);
            Cell[] cells = rs.rawCells();

            if (null != cells && cells.length > 0) {
                String value = Bytes.toString(cells[0].getValueArray(), cells[0].getValueOffset(), cells[0].getValueLength());
                configSku = JSON.parseObject(value, ConfigSku.class);
            }
        } catch (Exception e) {
            LOGGER.error("HBaseService queryConfigSku IOException error: {}", e);
        } finally {
            HBaseUtils.close(table, null, null);
        }
        return configSku;
    }


    /**
     * 创建表
     */
    private void createTable(String tableName, String [] args) {
        Admin admin = null;
        try {
            // args数组保存的是列族
            admin = connection.getAdmin();
            // 创建表
            HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(tableName));
            for (String st : args) {
                htd.addFamily(new HColumnDescriptor(st));
            }
            admin.createTable(htd);
        }
        catch (Exception e) {
            LOGGER.info("HBaseService createTable tableName:{}, error:{}", tableName, e);
        }
        finally {
            if (null != admin) {
                try {
                    admin.close();
                } catch (IOException e) {
                    LOGGER.info("HBaseService close admin tableName:{}, error:{}", tableName, e);
                }
            }
            try {
                if (null != connection) {
                    connection.close();
                }
            } catch (IOException e) {
                LOGGER.info("HBaseService close connection tableName:{}, error:{}", tableName, e);
            }
        }
    }
    public void deleteTable(String tableName) {
        HBaseAdmin admin = null;
        try {
            admin = (HBaseAdmin) connection.getAdmin();
            if (!admin.tableExists(TableName.valueOf(tableName))) {
                LOGGER.warn("Table: {} is not exists!", tableName);
                return;
            }
            admin.disableTable(TableName.valueOf(tableName));
            admin.deleteTable(TableName.valueOf(tableName));
            LOGGER.info("Table: {} delete success!", tableName);
        }
        catch (Exception e) {
            LOGGER.info("HBaseService deleteTable tableName:{}, error:{}", tableName, e);
        }
        finally {
            if (null != admin) {
                try {
                    admin.close();
                } catch (IOException e) {
                    LOGGER.info("HBaseService close admin tableName:{}, error:{}", tableName, e);
                }
            }
            try {
                if (null != connection) {
                    connection.close();
                }
            } catch (IOException e) {
                LOGGER.info("HBaseService close connection tableName:{}, error:{}", tableName, e);
            }
        }
    }

    public void truncateTable(String tableName) {
        HBaseAdmin admin = null;
        try {
            admin = (HBaseAdmin) connection.getAdmin();
            if (!admin.tableExists(TableName.valueOf(tableName))) {
                LOGGER.warn("Table: {} is not exists!", tableName);
                return;
            }
            admin.disableTable(TableName.valueOf(tableName));
            admin.truncateTable(TableName.valueOf(tableName), true);
            admin.enableTables(tableName);
            LOGGER.info("Table: {} truncate success!", tableName);
        }
        catch (Exception e) {
            LOGGER.info("HBaseService truncateTable tableName:{}, error:{}", tableName, e);
        }
        finally {
            if (null != admin) {
                try {
                    admin.close();
                } catch (IOException e) {
                    LOGGER.info("HBaseService close admin tableName:{}, error:{}", tableName, e);
                }
            }
        }
    }

    /**
     * 模糊查询hbase_partner_goods
     * 全表扫描，速度非常慢
     * @param dbId
     * @return
     */
    public List<PartnerGoods> fuzzyQueryGoods(Integer dbId, String lastRowKey) {
        long start = System.currentTimeMillis();
        Scan scan = new Scan();
        String key = dbId + "-";
        ResultScanner scanner;
        Table table = null;
        List<PartnerGoods> list = new ArrayList<>();
        try {
            FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
            // 模糊匹配
            filterList.addFilter(new RowFilter(CompareFilter.CompareOp.EQUAL, new BinaryPrefixComparator(key.getBytes())));
            // 分页
            filterList.addFilter(new PageFilter(HBASE_COMPARE_PAGE_SIZE));
            // 上一页的最后rowkey
            if (StringUtils.isNotBlank(lastRowKey)) {
                filterList.addFilter(new RowFilter(CompareFilter.CompareOp.GREATER, new BinaryComparator(Bytes.toBytes(lastRowKey))));
            }
            scan.setFilter(filterList);
            table = connection.getTable(TableName.valueOf(HBASE_PARTNER_GOODS));
            scanner = table.getScanner(scan);

            Result result;
            int rowNum = 0;
            while ((result = scanner.next()) != null) {
                if (rowNum >= HBASE_COMPARE_PAGE_SIZE) {
                    break;
                }
                List<Cell> listCells = result.listCells();
                for (Cell cell : listCells) {
                    String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    list.add(JSON.parseObject(value, PartnerGoods.class));
                    rowNum++;
                }
            }
        } catch (IOException e) {
            LOGGER.error("fuzzyQueryGoods IOException:{}", e);
        } finally {
            long end = System.currentTimeMillis();
            LOGGER.info("fuzzyQueryGoods dbId:{}, lastRowKey:{}, size{}, time:{}",
                    dbId, lastRowKey, list.size(), (end - start) / 1000);
            HBaseUtils.close(table, null, null);
        }
        return list;
    }

    /**
     * 模糊查询 hbase_store_goods
     * 全表扫描，速度非常慢
     * @param dbId
     * @param groupId
     * @param lastRowKey
     * @return
     */
    public List<PartnerStoreGoods> fuzzyQueryStoreGoods(Integer dbId, String groupId, String lastRowKey) {
        LOGGER.info("fuzzyQueryStoreGoods dbId:{}, groupId:{}, lastRowKey:{}", dbId, groupId, lastRowKey);
        long start = System.currentTimeMillis();
        Scan scan = new Scan();
        String key = dbId + "-" + groupId + "-";
        ResultScanner scanner;
        Table table = null;
        List<PartnerStoreGoods> list = new ArrayList<>();
        try {
            FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
            // 模糊匹配
            filterList.addFilter(new RowFilter(CompareFilter.CompareOp.EQUAL, new BinaryPrefixComparator(key.getBytes())));
            // 分页
            filterList.addFilter(new PageFilter(HBASE_COMPARE_PAGE_SIZE));
            // 上一页的最后rowkey
            if (StringUtils.isNotBlank(lastRowKey)) {
                filterList.addFilter(new RowFilter(CompareFilter.CompareOp.GREATER, new BinaryComparator(Bytes.toBytes(lastRowKey))));
            }
            scan.setFilter(filterList);
            table = connection.getTable(TableName.valueOf(HBASE_PARTNER_STORE_GOODS));
            scanner = table.getScanner(scan);

            Result result;
            int rowNum = 0;
            while ((result = scanner.next()) != null) {
                if (rowNum >= HBASE_COMPARE_PAGE_SIZE) {
                    break;
                }
                List<Cell> listCells = result.listCells();
                for (Cell cell : listCells) {
                    String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    list.add(JSON.parseObject(value, PartnerStoreGoods.class));
                    rowNum++;
                }
            }
        } catch (IOException e) {
            LOGGER.error("fuzzyQueryStoreGoods IOException:{}", e);
        } finally {
            long end = System.currentTimeMillis();
            LOGGER.info("fuzzyQueryStoreGoods dbId:{}, groupId:{}, lastRowKey:{}, size:{} time:{}",
                    dbId, groupId, lastRowKey, list.size(), (end - start) / 1000);
            HBaseUtils.close(table, null, null);
        }
        return list;
    }

    /**
     * 关闭连接
     */
    public void closeConnection() {
        HBaseUtils.close(null, connection, null);
    }

    /**
     * 自定义hbase保存数据
     * @param tableName
     * @param rowKey
     * @param familyName
     * @param qualifier
     * @param data
     */
    public void selfPut(String tableName, String rowKey, String familyName, String qualifier, String data) {
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(qualifier), Bytes.toBytes(data));
            table.put(put);
        } catch (Exception e) {
            LOGGER.error("HBaseService put error:{}", e);
        } finally {
            HBaseUtils.close(table, null, null);
        }
    }


    public static void main(String[] args) {
        HBaseService hBaseService = new HBaseService(HBaseUtils.getConnection(ExecutionEnvUtil.PARAMETER_TOOL));

        /*System.out.println("start");
        long start = System.currentTimeMillis();
        List<PartnerStoreGoods> partnerStoreGoods = hBaseService.queryPartnerStoreGoods(Arrays.asList("320-1170303-101140"));
        long end = System.currentTimeMillis();
        System.out.println((end - start) + JSON.toJSONString(partnerStoreGoods));*/

        // 创建表
        String [] family = {HBASE_FAMILY_NAME};
         hBaseService.createTable(HBASE_GC_CONFIG_SKU, family);

//        hBaseService.selfPut(HBASE_GOODS_SALES, "1-1",
//                "cnt", "quantity", "10.01");
//        hBaseService.closeConnection();
        System.out.println("complete");

//        PlatformGoods goods = new PlatformGoods();
//        goods.setId(1L);
//        goods.setChannel("2");
//        goods.setGmtCreated(LocalDateTime.now());
//        goods.setGmtUpdated(LocalDateTime.now());
//        goods.setGoodsInternalId("1");
//        goods.setMerchantId(1L);
//        goods.setStatus(1);
//        goods.setStoreId(1L);
//        hBaseService.put("2-1-1-1", goods);
//        hBaseService.closeConnection();

//        PlatformGoods platformGoods = hBaseService.queryPlatformGoods("635-18307-2-2115203");
//        LOGGER.info("platformGoods:{}", JSON.toJSONString(platformGoods));
//        GoodsSales goodsSales = hBaseService.queryGoodsSales(104334, "02270");
//        LOGGER.info("goodsSales:{}", JSON.toJSONString(goodsSales));
//        hBaseService.delete(HBASE_PLATFORM_GOODS, "null-1-1-1");

        // 删除表
        // hBaseService.deleteTable(HBASE_GC_GOODS_SALES_STATISTICS_MERCHANT);
        // hBaseService.deleteTable(HBASE_PARTNER_GOODS);
        // hBaseService.deleteTable(HBASE_PARTNER_STORE_GOODS);

        // 清空表
        // hBaseService.truncateTable(HBASE_GC_GOODS_SALES_STATISTICS_MERCHANT);
        // hBaseService.truncateTable(HBASE_PARTNER_GOODS);
        // hBaseService.truncateTable(HBASE_PARTNER_STORE_GOODS);

        // 模糊查询
        /*List<PartnerGoods> partnerGoods = hBaseService.fuzzyQueryGoods(2, null);
        int size = 0;
        while (CollectionUtils.isNotEmpty(partnerGoods)) {
            PartnerGoods goods = partnerGoods.get(partnerGoods.size() - 1);
            size += partnerGoods.size();
            partnerGoods = hBaseService.fuzzyQueryGoods(2, goods.getDbId() + "-" + goods.getInternalId());
        }
        System.out.println("总数量：" + size);*/
    }


}
