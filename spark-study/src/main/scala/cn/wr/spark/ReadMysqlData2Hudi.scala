package cn.wr.spark

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

import java.util.Properties

object ReadMysqlData2Hudi {

  def main(args: Array[String]): Unit = {
//    val conf = new SparkConf().setAppName("items_ods")
val conf = new SparkConf().setAppName("users_ods")
    val sparkSession = SparkSession.builder().master("local[*]").enableHiveSupport().config(conf).getOrCreate()
    val url = "jdbc:mysql://localhost/cstor_store?useSSL=false"
    val prop = new Properties()
    prop.put("user", "root")
    prop.put("password", "12345678")
    prop.put("driver", "com.mysql.cj.jdbc.Driver")

    //spark读取Mysql
    val dataFrame = sparkSession.read.jdbc(url, "users", prop)
    dataFrame.show()

    //关闭spark
    sparkSession.stop()

  }

}
