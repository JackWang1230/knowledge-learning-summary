package cn.wr.constants;

/**
 * @author RWang
 * @Date 2022/5/12
 */

public class SqlConstants {

    /** 连表查询生产厂商值 */
    public static final String MANUFACTURER_SQL = "select b.manufacturer as  manufacturer from gc_config_sku a left join gc_source_sku b " +
            " on a.sku_no=b.sku_no " +
            " where a.sku_no= ? ";

    /** upsert 更新连锁商品星级标签 */
    public static final String UPSERT_GOODS_SKU_STAR_SQL="insert into gc_merchant_goods_infos (sku_no,merchant_id," +
            "is_goods_name,is_approval_number,is_tradecode,is_spec_name,is_manufacturer) values (?,?,?,?,?,?,?) " +
            "on duplicate key update merchant_id=?, is_goods_name=?,is_approval_number=?,is_tradecode=?," +
            "is_spec_name=?,is_manufacturer=?";

    public static final String UPSERT_GOODS_SKU_STAR_SQL1="insert into gc_merchant_goods_infos (sku_no,merchant_id," +
            "is_goods_name,is_approval_number,is_tradecode,is_spec_name,is_manufacturer) values (?,?,?,?,?,?,?) ";
}
