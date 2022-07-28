package cn.wr.collect.sync.constants;

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_GOODS_CENTER_TIDB;
import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_UNION_DRUG_PARTNER;

public class SqlConstants {
    // partner_goods
    public static final String SQL_PARTNER_GOODS = "select pg.`id`, pg.`db_id`, pg.`table_id`, pg.`internal_id`, " +
            " pg.`common_name`, pg.`trade_code`, " +
            " pg.`approval_number`, pg.`form`, pg.`pack`, pg.`price`, pg.`manufacturer`, pg.`status`, " +
            " (CASE pg.`goods_create_time` WHEN '0000-00-00 00:00:00' THEN NULL ELSE pg.`goods_create_time` END) AS `goods_create_time`, " +
            " (CASE pg.`goods_update_time` WHEN '0000-00-00 00:00:00' THEN NULL ELSE pg.`goods_update_time` END) AS `goods_update_time`, " +
            " (CASE pg.`created_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE pg.`created_at` END) AS `created_at`, " +
            " (CASE pg.`updated_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE pg.`updated_at` END) AS `updated_at`, " +
            " (CASE pg.`gmtcreated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE pg.`gmtcreated` END) AS `gmtcreated`, " +
            " (CASE pg.`gmtupdated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE pg.`gmtupdated` END) AS `gmtupdated` " +
            " from " + SCHEMA_UNION_DRUG_PARTNER + ".partner_goods pg ";
    public static final String SQL_GOODS_JOIN01 = " left join " + SCHEMA_GOODS_CENTER_TIDB + ".gc_standard_goods_syncrds sgs on pg.trade_code = sgs.trade_code ";
    public static final String SQL_GOODS_JOIN02 = " left join " + SCHEMA_GOODS_CENTER_TIDB + ".gc_standard_goods_syncrds sgs on pg.trade_code = sgs.trade_code " +
            " inner join " + SCHEMA_GOODS_CENTER_TIDB + ".gc_goods_spu gs on sgs.spu_id = gs.id ";
    public static final String SQL_GOODS_WHERE_01 = " where pg.id > ? ";
    // 如果spuId未传入，则是approval_number相关表发生变更，需要处理goods表+关联表 approval_number相关数据
    public static final String SQL_GOODS_WHERE_02 = " where pg.id > ? and ((pg.approval_number = ? and (gs.approval_number is null or gs.approval_number = '')) or gs.approval_number = ?) ";
    // 如果传入参数spuId，则一定是表gc_goods_spu发生变更，只需处理关联到的approval_number即可
    public static final String SQL_GOODS_WHERE_03 = " where pg.id > ? and sgs.spu_id = ? ";
    public static final String SQL_GOODS_ORDER = " order by pg.id asc ";
    public static final String SQL_GOODS_LIMIT = " limit ?; ";


    // partner_store_goods
    public static final String SQL_STORE_GOODS = "select `id`, `db_id`, `table_id`, `goods_internal_id`, `group_id`, " +
            " `price`, `status`, " +
            " (CASE `created_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `created_at` END) AS `created_at`, " +
            " (CASE `updated_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `updated_at` END) AS `updated_at`, " +
            " `gmtcreated`, `gmtupdated`, `member_price` " +
            " from " + SCHEMA_UNION_DRUG_PARTNER + ".`partner_store_goods` ";
    public static final String SQL_STORE_GOODS_WHERE_01 = " where id > ? and db_id = ? and group_id = ? ";
    public static final String SQL_STORE_GOODS_WHERE_02 = " and goods_internal_id = ? ";
    public static final String SQL_STORE_GOODS_ORDER = " order by id asc ";
    public static final String SQL_STORE_GOODS_LIMIT = " limit ? ; ";


    public static final String SQL_PARTNER_GOODS_APPROVAL_NUM = "select t.* from ( " +
            "select " +
            "pg.`id`, pg.`db_id`, pg.`table_id`, pg.`internal_id`, " +
            "pg.`common_name`, pg.`trade_code`,  " +
            "pg.`approval_number`, pg.`form`, pg.`pack`, pg.`price`, pg.`manufacturer`, pg.`status`, " +
            "null AS `goods_create_time`,null AS `goods_update_time`, " +
            "null AS `created_at`,null AS `updated_at`, " +
            "null AS `gmtcreated`,null AS `gmtupdated` " +
            "from " + SCHEMA_UNION_DRUG_PARTNER + ".partner_goods pg " +
            "left join " + SCHEMA_GOODS_CENTER_TIDB + ".gc_standard_goods_syncrds sgs on pg.trade_code = sgs.trade_code " +
            "left join " + SCHEMA_GOODS_CENTER_TIDB + ".gc_goods_spu gs on sgs.spu_id = gs.id  " +
            "where pg.approval_number = ? and (gs.approval_number is null or gs.approval_number = '')  " +
            "union all " +
            "select " +
            "pg.`id`, pg.`db_id`, pg.`table_id`, pg.`internal_id`, " +
            "pg.`common_name`, pg.`trade_code`,  " +
            "pg.`approval_number`, pg.`form`, pg.`pack`, pg.`price`, pg.`manufacturer`, pg.`status`, " +
            "null AS `goods_create_time`,null AS `goods_update_time`, " +
            "null AS `created_at`,null AS `updated_at`, " +
            "null AS `gmtcreated`,null AS `gmtupdated` " +
            "from " + SCHEMA_UNION_DRUG_PARTNER + ".partner_goods pg " +
            "left join " + SCHEMA_GOODS_CENTER_TIDB + ".gc_standard_goods_syncrds sgs on pg.trade_code = sgs.trade_code " +
            "inner join " + SCHEMA_GOODS_CENTER_TIDB + ".gc_goods_spu gs on sgs.spu_id = gs.id  " +
            "where gs.approval_number = ? " +
            ") t " +
            "where t.id > ? order by t.id asc limit ? ;";
}
