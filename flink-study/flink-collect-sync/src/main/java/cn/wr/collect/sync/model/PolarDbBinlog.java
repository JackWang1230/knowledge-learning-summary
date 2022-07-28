package cn.wr.collect.sync.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/*
{
  "data": [
    {
      "id": "1",
      "approval_number": "国药准字Z14020687",
      "product_name": "龟龄集",
      "spec": "每粒装0.3g",
      "product_unit": "山西广誉远国药有限公司",
      "product_addr": "山西省晋中市太谷县广誉远路1号",
      "approve_date": "2015-09-28",
      "end_date": "2015-09-28",
      "goods_type": "11",
      "domestic_foreign": "1",
      "relation_id": "1",
      "cate_one": "Z",
      "cate_two": "ZA",
      "cate_three": "ZA09",
      "cate_four": "ZA09D",
      "cate_five": "ZA09DC"
    }
  ],
  "database": "cn_uniondrug_middleend_goodscenter",
  "es": 1603957876000,
  "id": 4,
  "isDdl": false,
  "mysqlType": {
    "id": "bigint(20)",
    "approval_number": "varchar(255)",
    "product_name": "varchar(512)",
    "spec": "text",
    "product_unit": "varchar(512)",
    "product_addr": "text",
    "approve_date": "varchar(255)",
    "end_date": "varchar(255)",
    "goods_type": "int(20)",
    "domestic_foreign": "int(20)",
    "relation_id": "bigint(20)",
    "cate_one": "varchar(255)",
    "cate_two": "varchar(255)",
    "cate_three": "varchar(255)",
    "cate_four": "varchar(255)",
    "cate_five": "varchar(255)"
  },
  "old": [
    {
      "product_name": "龟龄集1",
      "end_date": null
    }
  ],
  "pkNames": [
    "id"
  ],
  "sql": "",
  "sqlType": {
    "id": -5,
    "approval_number": 12,
    "product_name": 12,
    "spec": 2005,
    "product_unit": 12,
    "product_addr": 2005,
    "approve_date": 12,
    "end_date": 12,
    "goods_type": 4,
    "domestic_foreign": 4,
    "relation_id": -5,
    "cate_one": 12,
    "cate_two": 12,
    "cate_three": 12,
    "cate_four": 12,
    "cate_five": 12
  },
  "table": "base_goods",
  "ts": 1603957876798,
  "type": "UPDATE"
}
 */
@Data
public class PolarDbBinlog implements Serializable {
    private static final long serialVersionUID = 8917910102609866973L;
    /**
     * binlog序号
     */
    protected long id;
    /**
     * 库名
     */
    protected String database;
    /**
     * 表名
     */
    protected String table;
    /**
     * 主键
     */
    protected List<String> pkNames;
    /**
     * 是否ddl
     */
    protected Boolean isDdl;
    /**
     * 操作类型
     * INSERT/DELETE/UPDATE
     */
    protected String type;
    /**
     * 事件时间戳
     */
    protected Long es;
    /**
     * 操作时间戳
     */
    protected Long ts;
    /**
     * sql
     */
    protected String sql;
    /**
     * sql 类型
     */
    protected Map<String, Integer> sqlType;
    /**
     * mysql 类型
     */
    protected Map<String, String> mysqlType;
    /**
     * 更新后数据
     */
//    private List<Map<String, Object>> data;
    /**
     * 更新前数据
     * 只包含更新字段，未更新字段不在集合中
     */
//    private List<Map<String, Object>> old;

}
