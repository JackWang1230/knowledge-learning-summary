package cn.wr.hbase;

import cn.wr.model.StockGoods;
import cn.wr.utils.HbaseUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static cn.wr.constants.PropertiesConstants.HBASE_STOCK_GOODS;
import static cn.wr.constants.RedisHbaseConst.HBASE_FAMILY_NAME;
import static cn.wr.constants.RedisHbaseConst.HBASE_QUALIFIER;

/**
 * @author RWang
 * @Date 2022/7/20
 */

public class HbaseService {


    private static final Logger logger = LoggerFactory.getLogger(HbaseService.class);
    private Connection connection;

    public HbaseService(Connection connection) {
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
            logger.error("HBaseService delete error:{}", e);
        } finally {
            long end = System.currentTimeMillis();
            logger.info("HBaseService delete tableName:{}, key:{}, time:{}", tableName, key, (end - start));
            HbaseUtil.close(table, null, null);
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
            logger.error("HBaseService put error:{}", e);
        } finally {
            // long end = System.currentTimeMillis();
            // LOGGER.info("HBaseService put tableName:{}, key:{}, time:{}", tableName, key, (end - start));
            HbaseUtil.close(table, null, null);
        }
    }


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
            logger.error("HBaseService batch put error:{}", e);
        } finally {
            /*long end = System.currentTimeMillis();
            LOGGER.info("HBaseService batchPut putList tableName:{}, size:{}, time:{}(ms)", tableName, putList.size(), end - start);*/
            HbaseUtil.close(table, null, null);
        }

    }


    /**
     * 查询stock_goods
     * @param stockNo
     * @return
     */
    public StockGoods queryStockGoods(String stockNo){

        StockGoods stockGoods = null;
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(HBASE_STOCK_GOODS));
            Get get = new Get(Bytes.toBytes(stockNo));
            Result rs = table.get(get);
            Cell[] cells = rs.rawCells();
            if (null != cells && cells.length > 0) {
                String value = Bytes.toString(cells[0].getValueArray(), cells[0].getValueOffset(), cells[0].getValueLength());
                stockGoods = JSON.parseObject(value, StockGoods.class);
            }
        } catch (Exception e) {
            logger.error("HBaseService query IOException error: {}", e);
        } finally {
            HbaseUtil.close(table, null, null);
        }
        return stockGoods;
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
            logger.info("HBaseService createTable tableName:{}, error:{}", tableName, e);
        }
        finally {
            if (null != admin) {
                try {
                    admin.close();
                } catch (IOException e) {
                    logger.info("HBaseService close admin tableName:{}, error:{}", tableName, e);
                }
            }
            try {
                if (null != connection) {
                    connection.close();
                }
            } catch (IOException e) {
                logger.info("HBaseService close connection tableName:{}, error:{}", tableName, e);
            }
        }
    }
    public void deleteTable(String tableName) {
        HBaseAdmin admin = null;
        try {
            admin = (HBaseAdmin) connection.getAdmin();
            if (!admin.tableExists(TableName.valueOf(tableName))) {
                logger.warn("Table: {} is not exists!", tableName);
                return;
            }
            admin.disableTable(TableName.valueOf(tableName));
            admin.deleteTable(TableName.valueOf(tableName));
            logger.info("Table: {} delete success!", tableName);
        }
        catch (Exception e) {
            logger.info("HBaseService deleteTable tableName:{}, error:{}", tableName, e);
        }
        finally {
            if (null != admin) {
                try {
                    admin.close();
                } catch (IOException e) {
                    logger.info("HBaseService close admin tableName:{}, error:{}", tableName, e);
                }
            }
            try {
                if (null != connection) {
                    connection.close();
                }
            } catch (IOException e) {
                logger.info("HBaseService close connection tableName:{}, error:{}", tableName, e);
            }
        }
    }

    public void truncateTable(String tableName) {
        HBaseAdmin admin = null;
        try {
            admin = (HBaseAdmin) connection.getAdmin();
            if (!admin.tableExists(TableName.valueOf(tableName))) {
                logger.warn("Table: {} is not exists!", tableName);
                return;
            }
            admin.disableTable(TableName.valueOf(tableName));
            admin.truncateTable(TableName.valueOf(tableName), true);
            admin.enableTables(tableName);
            logger.info("Table: {} truncate success!", tableName);
        }
        catch (Exception e) {
            logger.info("HBaseService truncateTable tableName:{}, error:{}", tableName, e);
        }
        finally {
            if (null != admin) {
                try {
                    admin.close();
                } catch (IOException e) {
                    logger.info("HBaseService close admin tableName:{}, error:{}", tableName, e);
                }
            }
        }
    }

    /**
     * 关闭连接
     */
    public void closeConnection() {
        HbaseUtil.close(null, connection, null);
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
            logger.error("HBaseService put error:{}", e);
        } finally {
            HbaseUtil.close(table, null, null);
        }
    }
}
