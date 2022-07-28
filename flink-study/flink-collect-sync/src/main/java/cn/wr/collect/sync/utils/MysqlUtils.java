package cn.wr.collect.sync.utils;

import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Objects;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;

/**
 * mysql 工具类
 */
public class MysqlUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlUtils.class);
    // mysql driver
    private static final String MYSQL_DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    // flink配置
    private static ParameterTool parameterTool;

    /**
     * 获取数据库连接
     * @param tool
     * @return
     */
    public static Connection getConnection(ParameterTool tool) {
        parameterTool = tool;
        return MysqlUtils.getConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));
    }

    /**
     * 重试获取数据库链接，失败情况下重试
     * @param url
     * @param userName
     * @param pwd
     * @return
     */
    public static Connection retryConnection(String url, String userName, String pwd) {
        synchronized (MysqlUtils.class) {
            int i = 1;
            Connection connection;
            while (i <= 100) {
                try {
                    connection = MysqlUtils.getConnection(url, userName, pwd);
                    if (Objects.nonNull(connection)) {
                        // LOGGER.info("MysqlUtils retryConnection success");
                        return connection;
                    }
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    LOGGER.error("MysqlUtils retryConnection url: {} InterruptedException: {}", url, e);
                }
                catch (Exception e) {
                    LOGGER.error("MysqlUtils retryConnection url: {} Exception: {}", url, e);
                }
                LOGGER.info("MysqlUtils retryConnection retry times:{}", i++);
            }
            return null;
        }
    }

    /**
     * 重置数据库连接
     * @return
     */
    public static Connection resetConnection() {
        LOGGER.info("MysqlUtils reset connect ...");
        synchronized (MysqlUtils.class) {
            int i = 1;
            Connection connection;
            while (i <= 100) {
                try {
                    connection = MysqlUtils.getConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                            parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                            parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));
                    if (null != connection) {
                        LOGGER.info("MysqlUtils reset connect success");
                        return connection;
                    }

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOGGER.error("MysqlUtils reset connect InterruptedException:{}", e);
                } catch (Exception e) {
                    LOGGER.error("MysqlUtils reset connect Exception:{}", e);
                }
                LOGGER.info("MysqlUtils reset connect retry times:{}", i++);
            }
            return null;
        }
    }

    /**
     * 获取mysql连接
     * @param url
     * @param username
     * @param password
     * @return
     */
    public static Connection getConnection(String url, String username, String password) {
        Connection connection = null;
        //加载JDBC驱动
        try {
            Class.forName(MYSQL_DATABASE_DRIVER);
            //创建连接
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            LOGGER.error("MysqlUtils get connection error:{}", e);
        }
        return connection;
    }

    /**
     * 关闭连接
     * @param connection
     * @param ps
     * @param rs
     */
    public static void close(Connection connection, PreparedStatement ps, ResultSet rs) {
        if (null != rs) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.error("MysqlUtils close rs error:{}", e);
            }
        }
        if (null != ps) {
            try {
                ps.close();
            } catch (SQLException e) {
                LOGGER.error("MysqlUtils close ps error:{}", e);
            }
        }
        if (null != connection) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.error("MysqlUtils close connection error:{}", e);
            }
        }
    }
}
