package cn.wr.collect.sync.constants;

import cn.wr.collect.sync.model.annotations.Table;
import org.apache.commons.lang3.StringUtils;

import static cn.wr.collect.sync.constants.FieldRefreshConstants.*;

public enum FieldRefreshEnum {
//    refresh_partner_goods(Table.BasicTimeSyncTable.refresh_partner_goods.name(), refresh_partner_goods_fields),
    gc_goods_manual(Table.BasicTimeSyncTable.gc_goods_manual.name(), gc_goods_manual_fields),
    gc_base_nootc(Table.BasicTimeSyncTable.gc_base_nootc.name(), gc_base_nootc_fields),
    gc_standard_goods_syncrds(Table.BasicTimeSyncTable.gc_standard_goods_syncrds.name(), gc_standard_goods_syncrds_fields),
    gc_base_spu_img(Table.BasicTimeSyncTable.gc_base_spu_img.name(), gc_base_spu_img_fields),
    partner_goods_img(Table.BasicTimeSyncTable.partner_goods_img.name(), partner_goods_img_fields),
    partner_goods_info(Table.BasicTimeSyncTable.partner_goods_info.name(), partner_goods_info_fields),
    gc_goods_dosage(Table.BasicTimeSyncTable.gc_goods_dosage.name(), gc_goods_dosage_fields),
    gc_partner_goods_gift(Table.BasicTimeSyncTable.gc_partner_goods_gift.name(), gc_partner_goods_gift_fields),
    base_goods(Table.BasicTimeSyncTable.base_goods.name(), base_goods_fields),
    merchant_goods_category_mapping(Table.BasicTimeSyncTable.merchant_goods_category_mapping.name(), merchant_goods_category_mapping_fields),
    gc_goods_spu_attr_syncrds(Table.BasicTimeSyncTable.gc_goods_spu_attr_syncrds.name(), gc_goods_spu_attr_syncrds_fields),
    gc_goods_attr_info_syncrds(Table.BasicTimeSyncTable.gc_goods_attr_info_syncrds.name(), gc_goods_attr_info_syncrds_fields),
    organize_base(Table.BasicTimeSyncTable.organize_base.name(), organize_base_fields),
    pgc_store_info_increment(Table.BasicTimeSyncTable.pgc_store_info_increment.name(), pgc_store_info_increment_fields),
    platform_goods(Table.BasicTimeSyncTable.platform_goods.name(), platform_goods_fields),
    gc_goods_overweight(Table.BasicTimeSyncTable.gc_goods_overweight.name(), gc_goods_overweight_fields),
    gc_sku_extend(Table.BasicTimeSyncTable.gc_sku_extend.name(), gc_sku_extend_fields),
//    pgc_merchant_info(Table.BasicTimeSyncTable.pgc_merchant_info.name(), pgc_merchant_info_fields),

    ;

    /**
     * 定时同步基础表表名
     */
    private String tableName;
    /**
     * 定时同步基础表字段
     */
    private String[] fields;

    public static FieldRefreshEnum getEnum(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            return null;
        }
        for (FieldRefreshEnum e : FieldRefreshEnum.values()) {
            if (StringUtils.equals(e.getTableName(), tableName)) {
                return e;
            }
        }
        return null;
    }

    FieldRefreshEnum(String tableName, String[] fields) {
        this.tableName = tableName;
        this.fields = fields;
    }

    public String getTableName() {
        return tableName;
    }

    public String[] getFields() {
        return fields;
    }
}
