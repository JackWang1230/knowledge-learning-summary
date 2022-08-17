package cn.wr.utils;

import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Objects;

import static cn.wr.constants.PropertiesConstants.*;

/**
 * 数据库连接工具类
 * @author RWang
 * @Date 2022/5/12
 */

public class DataBasesUtil {

    private static final Logger logger = LoggerFactory.getLogger(DataBasesUtil.class);

    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";


    /**
     *  get mysql database connection
     * @param tool default config
     * @return Connection
     */
    public static Connection getMysqlConnection(ParameterTool tool){
        return DataBasesUtil.getConnection(tool.get(MYSQL_DATABASE_URL),
                tool.get(MYSQL_DATABASE_USER),
                tool.get(MYSQL_DATABASE_PASSWORD));
    }

    /**
     * get goodscenter polardb connection
     * @param tool default config
     * @return connection
     */
    public static Connection getGoodsCenterPolarConnection(ParameterTool tool){
        return DataBasesUtil.getConnection(tool.get(POLAR_GOODS_CENTER_DATABASE_URL),
                tool.get(POLAR_GOODS_CENTER_DATABASE_USER),
                tool.get(POLAR_GOODS_CENTER_DATABASE_PASSWORD));
    }


    /**
     * get datacenter polardb connection
     * @param tool default config
     * @return connection
     */
    public static Connection getDataCenterPolarConnection(ParameterTool tool){
        return DataBasesUtil.getConnection(tool.get(POLAR_DATACENTER_DATABASE_URL),
                tool.get(POLAR_DATACENTER_DATABASE_USER),
                tool.get(POLAR_DATACENTER_DATABASE_PASSWORD));
    }


    /**
     * 重试获取数据库链接，失败情况下重试
     * @return
     */
    public static Connection retryPolarConnection(ParameterTool parameterTool) {
        synchronized (DataBasesUtil.class) {
            int i = 1;
            Connection connection;
            while (i <= 100) {
                try {
                    connection = DataBasesUtil.getGoodsCenterPolarConnection(parameterTool);
                    if (Objects.nonNull(connection)) {
                        // LOGGER.info("MysqlUtils retryConnection success");
                        return connection;
                    }
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    logger.error("MysqlUtil retryConnection url: {} InterruptedException: {}",e);
                }
                catch (Exception e) {
                    logger.error("MysqlUtil retryConnection url: {} Exception: {}", e);
                }
                logger.info("MysqlUtil retryConnection retry times:{}", i++);
            }
            return null;
        }
    }

    /**
     * get connection based on  basic args
     * @param url url
     * @param user user
     * @param passWord passwd
     * @return Connection
     */
    public static Connection getConnection (String url, String user, String passWord){
        try {
            Class.forName(MYSQL_DRIVER);
            Connection connection = DriverManager.getConnection(url,user,passWord);
            return connection;
        } catch (Exception e){
            logger.error("Mysqlutil get connection error:{}",e);
        }
        return null;
    }

    /**
     * close connection
     * @param connection connection session
     * @param ps PreparedStatement
     */
    public static void close(Connection connection, PreparedStatement ps){

        if (null != connection){
            try {
                connection.close();
            } catch (SQLException e){
                logger.error("DataBasesUtil close connection error:{}",e);
            }
        }
        if (null != ps){
            try {
                ps.close();
            } catch (SQLException e){
                logger.error("DataBasesUtil close ps error:{}",e);
            }
        }
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
                logger.error("DataBasesUtil close rs error:{}", e);
            }
        }
        if (null != ps) {
            try {
                ps.close();
            } catch (SQLException e) {
                logger.error("DataBasesUtil close ps error:{}", e);
            }
        }
        if (null != connection) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("DataBasesUtil close connection error:{}", e);
            }
        }
    }
}
