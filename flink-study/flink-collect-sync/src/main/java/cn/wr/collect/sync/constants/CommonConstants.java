package cn.wr.collect.sync.constants;

import cn.wr.collect.sync.model.annotations.Table;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class CommonConstants {
    // tidb 数据库
    public static final String SCHEMA_UNION_DRUG_PARTNER = "uniondrug_partner";
    public static final String SCHEMA_GOODS_CENTER_TIDB = "cn_uniondrug_middleend_goodscenter";
//    public static final String SCHEMA_UNION_DRUG_PARTNER = "uniondrug_partner_polardb";
//    public static final String SCHEMA_GOODS_CENTER_TIDB = "cn_uniondrug_middleend_goodscenter_polardb";
    // 中间库
    public static final String SCHEMA_SQOOP = "sqoop";
    // oto药联库
    public static final String SCHEMA_UNIONDRUG_SHOP  = "uniondrug_shop";

    // 库存中心
    public static final String SCHEMA_CN_UD_MID_STOCK = "cn_ud_mid_stock";

    // 价格中心
    public static final String SCHEMA_CN_UD_GSBP_PRICE = "cn_udc_gsbp_price";

    // 源数据表
    public static final String PARTNER_GOODS = "partner_goods";
    public static final String PARTNER_STORE_GOODS = "partner_store_goods";
    public static final String BASIC_TIME_GOODS = "basic_time_goods";
    public static final String BASIC_TIME_STORE_GOODS = "basic_time_store_goods";
    public static final String ORGANIZE_BASE = "organize_base";
    public static final String GC_STANDARD_GOODS_SYNCRDS = "gc_standard_goods_syncrds";

    // 商品上下架表
    public static final String PLATFORM_GOODS = "platform_goods";
    public static final String GC_GOODS_SPU_ATTR_SYNCRDS = "gc_goods_spu_attr_syncrds";
    public static final String GC_PARTNER_STORES_ALL = "gc_partner_stores_all";
    public static final String PGC_STORE_INFO_INCREMENT = "pgc_store_info_increment";
    public static final String GC_GOODS_OVERWEIGHT = "gc_goods_overweight";

    // 商品中心消息发送表
    public static final String GC_CONFIG_SKU = "gc_config_sku";
    public static final String PARTNERS = "partners";
    public static final String PARTNER_STORES = "partner_stores";

    // binlog 操作 增/删/改
    public static final String OPERATE_INSERT = "insert";
    public static final String OPERATE_DELETE = "delete";
    public static final String OPERATE_UPDATE = "update";
    public static final String OPERATE_UNKNOWN = "unknown";
    public static final String OPERATE_UPDATE_DELETE = "update_delete"; // 更新情况下，删除旧数据操作
    public static final String OPERATE_UPDATE_INSERT = "update_insert"; // 更新情况下，新增数据操作
    // binlog 操作 增/删/改
    public static final String OPERATE_SHORT_INSERT = "I";
    public static final String OPERATE_SHORT_DELETE = "D";
    public static final String OPERATE_SHORT_UPDATE = "U";
    // mbs 消息发送  增/删/改
    public static final Integer OPERATE_MBS_INSERT = 1;
    public static final Integer OPERATE_MBS_DELETE = 2;
    public static final Integer OPERATE_MBS_UPDATE = 3;

    /* HBase 存储表 */
    public static final String HBASE_PREFIX = "hbase_";
    // partner_store_goods
    public static final String HBASE_PARTNER_STORE_GOODS = "hbase_partner_store_goods";
    // gc_goods_sales_statistics_merchant
    public static final String HBASE_GC_GOODS_SALES_STATISTICS_MERCHANT = "hbase_gc_goods_sales_statistics_merchant";
    // 销量
    public static final String HBASE_GOODS_SALES = "hbase_goods_sales";
    // 全连锁销量
    public static final String HBASE_GOODS_FULL_SALES = "hbase_goods_full_sales";
    // partner_goods
    public static final String HBASE_PARTNER_GOODS = "hbase_partner_goods";
    // platform_goods
    public static final String HBASE_PLATFORM_GOODS = "hbase_platform_goods";
    // gc_config_sku
    public static final String HBASE_GC_CONFIG_SKU = "hbase_gc_config_sku";
    // stock_goods
    public static final String HBASE_STOCK_GOODS = "hbase_stock_goods";

    /**
     * jdbc 数据库异常
     * 数据库断开连接，尝试重新获取连接
     */
    public static final String JDBC_EXCEPTION_01 = "Communications link failure";
    public static final String JDBC_EXCEPTION_02 = "No operations allowed after connection closed";
    public static final String JDBC_EXCEPTION_SELF = "Connection can not be null";

    // true/false
    public static final String FLAG_FALSE = "false";
    public static final String FLAG_TRUE = "true";

    // 百度是否上线
    public static final Integer BAIDU_ONLINE = 1;

    // 商品名称包含酒精
    public static final String COMMON_NAME_CONTAINS_ALCOHOL = "酒精";

    // 连锁上下架状态字段： 0-上架 1-下架
    public static final Integer GOODS_STATUS_OFF = 0;
    public static final Integer GOODS_STATUS_ON = 1;

    // O2O运营配置的状态字段 状态: 0-上架 1-下架
    public static final Integer IS_WR_OFF_SHELF_ON = 0;
    public static final Integer IS_WR_OFF_SHELF_OFF = 1;

    // hbase数据比对分页条数
    public static final Integer HBASE_COMPARE_PAGE_SIZE = 50000;

    // 是否开启dtp商品1为开启0为未开启
    public static final Integer IS_DTP_TRUE = 1;
    public static final Integer IS_DTP_FALSE = 0;

    // 1为普药网络2为dtp网络
    public static final Integer NET_TYPE_1 = 1;
    public static final Integer NET_TYPE_2 = 2;

    // 门店开启
    public static final Integer STORE_O2O_OPEN = 1;

    // 超重商品标识
    public static final Integer GOODS_OVERWEIGHT_STATUS_TRUE = 1;
    public static final Integer GOODS_OVERWEIGHT_STATUS_FALSE = 0;

    // 商品有效
    public static final Integer GOODS_STATUS_TRUE = 1;
    // 商品无效
    public static final Integer GOODS_STATUS_FALSE = 0;

    public static final BigDecimal GOODS_PRICE_DIME = new BigDecimal(0.1);

    public static final String MIDDLE_LINE = "-";
    // 渠道  2-o2o 4-电商
    public static final String CHANNEL_O2O = "2";
    public static final String CHANNEL_DS = "4";


    //1	药店宝 2	药联到家 3 网上药店(老电商) 4	网上商城(新电商) 5 权益活动 6	药联分销 7 严选福利商城 8	增值服务商城
    //9	车险 10 分销-药店宝 11 O2O-百度 12 O2O-FESCO 13 增值服务开放平台 14 招标系统 15 顾问社群 0	未知类型
    public static final Integer WELFARE_CHANNEL_7 = 7;
    public static final Integer WELFARE_CHANNEL_8 = 8;

    // 发送钉钉消息参数
//    public static final String WELFARE_GOODS_BOT_URL = "welfare.goods.bot.url";
//    public static final String WELFARE_GOODS_BOT_TITLE = "welfare.goods.bot.title";

    // 序列化id
    // public static final String SERIAL_VERSION_UID = "serialVersionUID";

    // gc_sku_extend { spellWord:助记词,saleNum 中包数量 inPackage }
    public static final String SPELL_WORD = "spellWord";
    public static final String CHANNEL = "channel";

    // 是否麻黄碱 属性id集合
    public static final Long[] EPHEDRINE_ATTR_IDS = new Long[]{31002L, 31003L, 31004L};
    // 是否双跨 属性id集合
    public static final Long[] DOUBLE_ATTR_IDS = new Long[]{31016L};
    // DTP
    public static final Long[] DTP_ATTR_IDS = new Long[]{31055L};

    // 是否是处方药
    public static final Long[] IS_PRESCRIPTION_IDS = new Long[]{4L,31028L,31274L};

    // 0-无关字段变更 1-仅单字段变更 2-仅组合字段变更 3-单字段&组合字段变更 4-key变更
    public static final int MOD_FIELD_NONE = 0;
    public static final int MOD_FIELD_SINGLE = 1;
    public static final int MOD_FIELD_MULTI = 2;
    public static final int MOD_FIELD_BOTH = 3;
    public static final int MOD_FIELD_KEY = 4;

    // 状态 0:未审核 1:审核通过 2:审核不通过 3下市
    public static final Integer STANDARD_GOODS_STATUS_PASS = 1;

    // 删除标记 0 正常 1 停用
    public static final Integer CATE_DELETED_ENABLE = 0;
    public static final Integer CATE_DELETED_DISABLE = 1;

    // 删除标记 0 正常 1 停用
    public static final Integer ATTR_DELETED_ENABLE = 0;
    public static final Integer ATTR_DELETED_DISABLE = 1;

    // is_o2o = 1
    public static final Integer IS_O2O = 1;

    // 是否DTP门店 0-否 1-是
    public static final Integer IS_DTP_STORE_TRUE = 1;
    public static final Integer IS_DTP_STORE_FALSE = 0;


    // 上下架
    public static final Integer IS_OFF_SHELF = 1;
    // 商品中心上下架  0：上架 1：下架
    public static final Integer IS_STANDARD_OFF_SHELF = 1;

    // gc_standard_goods_syncrds 表特殊字段处理
    public static final String MOD_FIELD_SPU_ID = "spu_id";
    // partner_goods 表特殊处理
    public static final String MOD_FIELD_TRADE_CODE = "trade_code";
    // organize_base 表字段特殊处理
    public static final String MOD_FIELD_IS_O2O = "isO2O";
    // organize_base 表字段特殊处理
    public static final String MOD_FIELD_NET_TYPE = "netType";
    // gc_config_sku 表字段特殊处理
    public static final String MOD_FIELD_BARCODE = "barcode";

    // 连锁db库起始名
    public static final String CHAIN_DB_START = "partner_common_";

    // mbs2 访问地址
    public static final String MBS2_URL = "/topic/publish";


    // 来源: 0-连锁推送变更 1-商品中心变更
    public static final Integer SOURCE_PARTNERS = 0;
    public static final Integer SOURCE_GOODS_CENTER = 1;

    // 虚拟库存 0-关闭 1-开启
    public static final Integer STOCK_VIRTUAL_ON = 1;
    public static final Integer STOCK_VIRTUAL_OFF = 0;

    // 平台状态 1 正常 0停用
    public static final Integer CENTER_STATE_ON = 1;
    public static final Integer CENTER_STATE_OFF = 0;

    /**
     * 基础数据写es过滤表数据，以下表不处理
     */
    public static final List<String> BASIC2ESFILTER = Arrays.asList(
            Table.BaseDataTable.partner_goods.name(),
            Table.BaseDataTable.partner_store_goods.name(),
            Table.BaseDataTable.pgc_store_info.name(),
            Table.BaseDataTable.pgc_merchant_info.name(),
            Table.BaseDataTable.merchant_goods_category_mapping.name()/*,
            Table.BaseDataTable.gc_partner_stores_all.name()*/);

    /**
     * 商品数据实时写es过滤表数据，只处理以下表
     */
    public static final List<String> GOODSFILTER = Arrays.asList(
            Table.BaseDataTable.partner_goods.name(),
            Table.BaseDataTable.partner_store_goods.name());

    /**
     * 基础商品数据推kafka过滤表数据，只处理以下表
     */
    public static final List<String> BASIC2KAFKAFILTER = Arrays.asList(
            Table.BaseDataTable.organize_base.name(),
            Table.BaseDataTable.platform_goods.name(),
            Table.BaseDataTable.gc_goods_spu_attr_syncrds.name(),
            Table.BaseDataTable.gc_partner_stores_all.name(),
            Table.BaseDataTable.gc_standard_goods_syncrds.name(),
            Table.BaseDataTable.pgc_store_info_increment.name(),
            Table.BaseDataTable.gc_goods_overweight.name()
    );

    /**
     * 商品数据实时写es过滤表数据，只处理以下表
     */
    public static final List<String> GOODSSTOREFILTER = Arrays.asList(
            Table.BaseDataTable.partner_goods.name(),
            Table.BaseDataTable.partner_store_goods.name(),
            Table.BaseDataTable.gc_partner_stores_all.name(),
            BASIC_TIME_GOODS,
            BASIC_TIME_STORE_GOODS);


    /**
     * 商品数据实时写es过滤表数据，只处理以下表
     */
    public static final List<String> STANDARD_GOODS_FILTER = Arrays.asList(
            Table.BaseDataTable.gc_goods_overweight.name(),
            Table.BaseDataTable.gc_base_nootc.name(),
            Table.BaseDataTable.gc_goods_dosage.name(),
            Table.BaseDataTable.gc_goods_spu_attr_syncrds.name(),
            Table.BaseDataTable.gc_goods_spu.name()
            );

}
