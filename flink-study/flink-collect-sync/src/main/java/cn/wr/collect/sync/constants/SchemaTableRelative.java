package cn.wr.collect.sync.constants;

import cn.wr.collect.sync.model.annotations.Table;
import org.apache.commons.lang3.StringUtils;

import static cn.wr.collect.sync.constants.CommonConstants.*;

public enum SchemaTableRelative {
    goods_center(SCHEMA_GOODS_CENTER_TIDB, new String[]{
            Table.BaseDataTable.gc_goods_manual.name(),
            Table.BaseDataTable.gc_partner_goods_gift.name(),
            Table.BaseDataTable.gc_base_spu_img.name(),
            Table.BaseDataTable.partner_goods_img.name(),
            Table.BaseDataTable.gc_standard_goods_syncrds.name(),
            Table.BaseDataTable.gc_goods_spu_attr_syncrds.name(),
            Table.BaseDataTable.gc_goods_attr_info_syncrds.name(),
            Table.BaseDataTable.gc_goods_dosage.name(),
            Table.BaseDataTable.gc_base_nootc.name(),
            Table.BaseDataTable.gc_partner_stores_all.name(),
            Table.BaseDataTable.base_goods.name(),
            Table.BaseDataTable.merchant_goods_category_mapping.name(),
            Table.BaseDataTable.pgc_store_info.name(),
            Table.BaseDataTable.platform_goods.name(),
            Table.BaseDataTable.organize_base.name(),
            Table.BaseDataTable.pgc_store_info_increment.name(),
            Table.BaseDataTable.pgc_merchant_info.name(),
            Table.BaseDataTable.gc_goods_overweight.name(),
            Table.BaseDataTable.gc_sku_extend.name(),
            Table.BaseDataTable.gc_goods_spu.name(),
            Table.BaseDataTable.gc_goods_cate_spu.name(),
            Table.BaseDataTable.gc_category_info.name(),
            Table.BaseDataTable.gc_config_sku.name(),

    }),
    partner(SCHEMA_UNION_DRUG_PARTNER, new String[]{
            Table.BaseDataTable.partner_goods.name(),
            Table.BaseDataTable.partner_store_goods.name(),
            Table.BaseDataTable.partner_goods_info.name(),
            Table.BaseDataTable.partners.name(),
            Table.BaseDataTable.partner_stores.name(),
    });

    /**
     * schema
     */
    private final String schema;
    /**
     * table list
     */
    private final String[] table;

    SchemaTableRelative(String schema, String[] table) {
        this.schema = schema;
        this.table = table;
    }



    /**
     * 校验 schema + table 是否符合条件
     *
     * @param schema
     * @param tableName
     * @return
     */
    public static boolean checkBasicValid(String schema, String tableName) {
        if (StringUtils.isBlank(schema) || StringUtils.isBlank(tableName)) {
            return false;
        }
        for (SchemaTableRelative relative : SchemaTableRelative.values()) {
            if (StringUtils.equals(relative.getSchema(), schema)) {
                for (String name : relative.table) {
                    if (StringUtils.equals(tableName, name)) {
                        return !GOODSFILTER.contains(name);
                    }
                }
            }
        }
        return false;
    }


    /**
     * 校验 schema + table 是否符合条件
     * 商品数据实时同步 只处理部分表
     *
     * @param schema
     * @param tableName
     * @return
     */
    public static boolean checkGoods2EsValid(String schema, String tableName) {
        if (StringUtils.isBlank(schema) || StringUtils.isBlank(tableName)) {
            return false;
        }

        for (SchemaTableRelative relative : SchemaTableRelative.values()) {
            if (!StringUtils.equals(relative.getSchema(), schema)) {
                continue;
            }
            for (String name : relative.table) {
                if (StringUtils.equals(tableName, name)) {
                    return GOODSFILTER.contains(name);
                }
            }
        }
        return false;
    }


    /**
     * 校验 schema + table 是否符合条件
     * 基础商品数据实时同步 需排除部分表
     *
     * @param schema
     * @param tableName
     * @return
     */
    public static boolean checkBasic2EsValid(String schema, String tableName) {
        if (StringUtils.isBlank(schema) || StringUtils.isBlank(tableName)) {
            return false;
        }

        for (SchemaTableRelative relative : SchemaTableRelative.values()) {
            if (!StringUtils.equals(relative.getSchema(), schema)) {
                continue;
            }
            for (String name : relative.table) {
                if (StringUtils.equals(tableName, name)) {
                    return !BASIC2ESFILTER.contains(name);
                }
            }
        }
        return false;
    }

    /**
     * 校验 table 是否符合条件
     * 基础商品数据发送kafka
     * @param tableName
     * @return
     */
    public static boolean checkBasicGoods2KafkaValid(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            return false;
        }
        return BASIC2KAFKAFILTER.contains(tableName);
    }

    /**
     * 校验标准商品是否符合标准
     * @param schema
     * @param tableName
     * @return
     */
    public static boolean checkStandardGoods2EsValid(String schema, String tableName) {
        if (StringUtils.isBlank(schema) || StringUtils.isBlank(tableName)) {
            return false;
        }

        for (SchemaTableRelative relative : SchemaTableRelative.values()) {
            if (!StringUtils.equals(relative.getSchema(), schema)) {
                continue;
            }
            for (String name : relative.table) {
                if (StringUtils.equals(tableName, name)) {
                    return STANDARD_GOODS_FILTER.contains(name);
                }
            }
        }
        return false;
    }


    public String getSchema() {
        return schema;
    }

    public String[] getTable() {
        return table;
    }
}
