package cn.wr.collect.sync.constants;

import java.util.Arrays;
import java.util.List;


public class EsFieldConst {

    public static final String goods_internal_id = "goods_internal_id";
    public static final String drug_name = "drug_name";
    public static final String indications = "indications";
    public static final String trade_code = "trade_code";
    public static final String sync_date = "sync_date";
    public static final String sku_code = "sku_code";
    public static final String approval_number = "approval_number";
    public static final String sale_price = "sale_price";
    public static final String base_price = "base_price";
    public static final String real_trade_code = "real_trade_code";
    public static final String common_name = "common_name";
    public static final String form = "form";
    public static final String channel = "channel";
    public static final String is_off_shelf = "is_off_shelf";
    public static final String merchant_id = "merchant_id";
    public static final String store_id = "store_id";
    public static final String location = "location";
    public static final String manufacturer = "manufacturer";
    public static final String is_standard = "is_standard";
    public static final String relative_sickness = "relative_sickness";
    public static final String is_prescription = "is_prescription";
    public static final String is_ephedrine = "is_ephedrine";
    public static final String is_double = "is_double";
    public static final String img = "img";
    public static final String sales_volume = "sales_volume";
    public static final String pack = "pack";
    public static final String brand = "brand";
    public static final String is_wr_off_shelf = "is_wr_off_shelf";
    public static final String province_code = "province_code";
    public static final String province_name = "province_name";
    public static final String city_code = "city_code";
    public static final String city_name = "city_name";
    public static final String area_code = "area_code";
    public static final String area_name = "area_name";
    public static final String real_common_name = "real_common_name";
    public static final String store_status = "store_status";
    public static final String is_dtp = "is_dtp";
    public static final String is_overweight = "is_overweight";
    public static final String search_keywords = "search_keywords";
    public static final String full_sales_volume = "full_sales_volume";
    public static final String spell_word = "spell_word";
    public static final String standard_goods_status = "standard_goods_status";

    public static final String cate_ids = "cate_ids";
    public static final String attr_ids = "attr_ids";

    public static final String goods_type = "goods_type";
    public static final String goods_sub_type = "goods_sub_type";

    // 标准商品库上下架
    public static final String is_standard_off_shelf = "is_standard_off_shelf";
    // sku审核状态
    public static final String sku_audit_status = "sku_audit_status";
    // 是否dtp门店 0-普通门店 1-dtp门店
    public static final String is_dtp_store = "is_dtp_store";
    // 是否虚拟库存
    public static final String is_virtual_stock = "is_virtual_stock";
    // 库存数量
    public static final String stock_quantity = "stock_quantity";
    // 连锁总部上下架
    public static final String control_status = "control_status";



    public static List<String> getAllField() {
        return Arrays.asList(drug_name, indications, trade_code, sync_date, sku_code, approval_number, sale_price, base_price,
                goods_internal_id, real_trade_code, common_name, form, channel, is_off_shelf, merchant_id, store_id,
                location, manufacturer, is_standard, relative_sickness, is_prescription, is_ephedrine, is_double, img,
                sales_volume, pack, brand, is_wr_off_shelf, province_code, province_name, city_code, city_name,
                area_code, area_name, real_common_name, store_status, is_dtp, is_overweight, search_keywords,
                full_sales_volume, spell_word, standard_goods_status, cate_ids, attr_ids, goods_type, goods_sub_type,
                is_standard_off_shelf, sku_audit_status, is_dtp_store, is_virtual_stock, stock_quantity, control_status);
    }

}
