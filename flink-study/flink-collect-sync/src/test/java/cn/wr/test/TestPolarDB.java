package cn.wr.test;

import java.sql.*;
import java.util.Properties;

public class TestPolarDB {
    /**
     * Replace the following information.
     */
    private final String host = "***.o.polardb.rds.aliyuncs.com";
    private final String user = "***";
    private final String password = "***";
    private final String port = "1921";
    private final String database = "db_name";

    public void run() throws Exception {
        Connection connect = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("com.aliyun.polardb.Driver");

            Properties props = new Properties();
            props.put("user", user);
            props.put("password", password);
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            connect = DriverManager.getConnection(url, props);

            /**
             * create table foo(id int, name varchar(20));
             */
            String sql = "select id, name from foo";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                System.out.println("id:" + resultSet.getInt(1));
                System.out.println("name:" + resultSet.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (statement != null)
                    statement.close();
                if (connect != null)
                    connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        TestPolarDB demo = new TestPolarDB();
        demo.run();
    }
}
