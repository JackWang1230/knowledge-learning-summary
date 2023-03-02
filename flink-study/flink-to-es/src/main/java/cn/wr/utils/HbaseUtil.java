package cn.wr.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cn.wr.constants.PropertiesConstants.*;

/**
 * @author RWang
 * @Date 2022/5/25
 */
@Slf4j
public class HbaseUtil {

    /**
     * 此处必须要加上volatile 关键字判断 禁止jvm指令重排优化 ，保证顺序
     * <p>
     * 指令1：获取singleton对象的内存地址
     * 指令2：初始化singleton对象
     * 指令3：将内存地址指向引用变量singleton
     * <p>
     * <p>
     * 单线程没问题，
     * 多线程场景下，线程A正常创建一个实例，执行了1-3，此时线程B调用getInstance()后发现instance不为空，
     * 因此会直接返回instance，但此时instance并未被初始化 导致直接为空
     */
    private volatile static Connection conn;

    private static Table table = null;

    /**
     * 创建连接
     *
     * @param parameterTool
     * @return
     */
    public static Connection getConnection(ParameterTool parameterTool) {
        // 双重检验锁 保证单例情况
        // 1.第一层判断null 若存在直接跳过竞争锁，提高效率
        // 2.第二层判断null 若线程1 通过了第一层的时候,线程2也通过了第一层，加上锁以后 只保证一个实例进入第二层创建
        //   从而保证数据是单例
        try {
            if (null == conn) {
                synchronized (HbaseUtil.class) {
                    if (null == conn) {
                        log.info("hbase create connection");
                        ExecutorService pool = Executors.newFixedThreadPool(parameterTool.getInt(HBASE_CLIENT_THREAD_NUM));
                        conn = ConnectionFactory.createConnection(hbaseConfig(parameterTool), pool);
                    }
                }
            }
        } catch (IOException ioException) {
            log.error("HbaseUtil get connection error:{} ", ioException);
        }
        return conn;

    }

    /**
     * 关闭连接
     *
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
            log.error("HBaseUtils close table error:{}", e);
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
     * 加载配置
     *
     * @param parameterTool
     * @return
     */
    public static Configuration hbaseConfig(ParameterTool parameterTool) {
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
