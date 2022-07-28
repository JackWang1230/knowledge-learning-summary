package cn.wr.collect.sync.utils;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;

public class HBaseUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(HBaseUtils.class);

    public static Connection connection = null;

    /**
     * 获取连接
     * @param parameterTool
     * @return
     */
    public static Connection getConnection(ParameterTool parameterTool) {
        try {
            if (null == connection) {
                synchronized (HBaseUtils.class) {
                    if (null == connection) {
                        LOGGER.info("hbase create connection ......");
                        ExecutorService pool = Executors.newFixedThreadPool(parameterTool.getInt(HBASE_CLIENT_THREAD_NUM));
                        connection = ConnectionFactory.createConnection(hbaseConfig(parameterTool), pool);
                        LOGGER.info("hbase create connection success ......");
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("HBaseUtils get connection error:{}", e);
        }
        return connection;
    }

    /**
     * 关闭连接
     * @param table
     * @param connection
     * @param rs
     */
    public static void close(Table table, Connection connection, ResultScanner rs) {
        if (null != rs) {
            rs.close();
        }
        try {
            if (null != table) {
                table.close();
            }
        } catch (IOException e) {
            LOGGER.error("HBaseUtils close table error:{}", e);
        }
        /*try {
            if (null != connection) {
                LOGGER.info("关闭 hbase connection ...");
                connection.close();
            }
        } catch (IOException e) {
            LOGGER.error("HBaseUtils close connection error:{}", e);
        }*/
    }

    /**
     * hbase 配置
     * @param parameterTool
     * @return
     */
    private static Configuration hbaseConfig(ParameterTool parameterTool) {
        Configuration conf = HBaseConfiguration.create();
        conf.set(HBASE_ZOOKEEPER_QUORUM, parameterTool.get(HBASE_ZOOKEEPER_QUORUM));
        conf.set(HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT, parameterTool.get(HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT));
        conf.set(HBASE_RPC_TIMEOUT, parameterTool.get(HBASE_RPC_TIMEOUT));
        conf.set(HBASE_CLIENT_OPERATION_TIMEOUT, parameterTool.get(HBASE_CLIENT_OPERATION_TIMEOUT));
        conf.set(HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, parameterTool.get(HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD));
        conf.set(ZOOKEEPER_ZNODE_PARENT, parameterTool.get(ZOOKEEPER_ZNODE_PARENT));
        conf.set(HBASE_CLIENT_SCANNER_CACHING, parameterTool.get(HBASE_CLIENT_SCANNER_CACHING));
        // 配置解决发布log报错
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        return conf;
    }
}
