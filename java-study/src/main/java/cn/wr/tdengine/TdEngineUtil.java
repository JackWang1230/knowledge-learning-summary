package cn.wr.tdengine;

import com.taosdata.jdbc.TSDBDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;

/**
 * @author : WangRui
 * @date : 2023/1/31
 */

public class TdEngineUtil {


    private static final Logger logger = LoggerFactory.getLogger(TdEngineUtil.class);

    private static final String TAOSDB_DRIVER = "com.taosdata.jdbc.rs.RestfulDriver";

    public static Connection getCon(String url, String userName, String password) throws Exception {

        try {

            Class.forName(TAOSDB_DRIVER);
            Properties connProps = new Properties();
//            connProps.setProperty(TSDBDriver.PROPERTY_KEY_BATCH_LOAD, "true");
//            connProps.setProperty(TSDBDriver.PROPERTY_KEY_USER, userName);
//            connProps.setProperty(TSDBDriver.PROPERTY_KEY_PASSWORD, password);
            return DriverManager.getConnection(url, connProps);
        } catch (Exception e) {
            logger.error("TdEngineUtil get connection error:{}", e);
        }
        return null;
    }

    public static Connection getRestConn() throws Exception {
        Class.forName("com.taosdata.jdbc.rs.RestfulDriver");
        String jdbcUrl = "jdbc:TAOS-RS://master:6041/test?user=root&password=taosdata";
        Properties connProps = new Properties();
        connProps.setProperty(TSDBDriver.PROPERTY_KEY_BATCH_LOAD, "true");
        Connection conn = DriverManager.getConnection(jdbcUrl, connProps);
        return conn;
    }

    public static void main(String[] args) throws Exception {

        String jdbcUrl = "jdbc:TAOS-RS://master:6041/test?";
        String userName = "root";
        String password = "taosdata";
        Connection con = TdEngineUtil.getRestConn();

//        try {
//            // RestFul 不支持预加载数据
//            PreparedStatement ps = con.prepareStatement("SELECT count(1) FROM d3740");
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()){
//                int anInt = rs.getInt(0);
//                System.out.println(anInt);
//            }
//
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }

        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT count(1) FROM d3740 ");
            while (rs.next()) {
                int anInt = rs.getInt(1);
                System.out.println(anInt);
            }

        }

    }

}
