package cn.wr.collect.sync.model.annotations;

import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.Model;

import cn.wr.collect.sync.model.gc.*;
import cn.wr.collect.sync.model.partner.*;
import cn.wr.collect.sync.model.price.PriceList;
import cn.wr.collect.sync.model.price.PriceListDetails;
import cn.wr.collect.sync.model.price.PriceStore;
import cn.wr.collect.sync.model.stock.StockGoods;
import cn.wr.collect.sync.model.stock.StockMerchant;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Objects;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {

    String name();

    enum BaseDataTable {
        gc_goods_manual(new GoodsManual()),
        // 2020-11-16销量表由数仓离线统计直接写入hbase
        /*gc_goods_sales_statistics_merchant(new GoodsSalesStatisticsMerchant()),*/
        gc_partner_goods_gift(new PartnerGoodsGift()),
        gc_base_spu_img(new BaseSpuImg()),
        partner_goods_img(new PartnerGoodsImg()),
        /*parnter_goods_search_priority(new ParnterGoodsSearchPriority()),*/
        gc_standard_goods_syncrds(new StandardGoodsSyncrds()),
        gc_goods_spu_attr_syncrds(new GoodsSpuAttrSyncrds()),
        gc_goods_attr_info_syncrds(new GoodsAttrInfoSyncrds()),
        gc_goods_dosage(new GoodsDosage()),
        gc_partner_stores_all(new PartnerStoresAll()),
        /*gc_base_sku_goods(new BaseSkuGoods()),*/
        merchant_goods_category_mapping(new MerchantGoodsCategoryMapping()),
        base_goods(new BaseGoods()),
        gc_base_nootc(new BaseNootc()),
        partner_goods_info(new PartnerGoodsInfo()),
        partner_store_goods(new PartnerStoreGoods()),
        partner_goods(new PartnerGoods()),
        pgc_store_info(new PgcStoreInfo()),
        platform_goods(new PlatformGoods()),
        organize_base(new OrganizeBase()),
        pgc_store_info_increment(new PgcStoreInfoIncrement()),
        pgc_merchant_info(new PgcMerchantInfo()),
        gc_goods_overweight(new GoodsOverweight()),
        gc_sku_extend(new SkuExtend()),
        gc_goods_spu(new GoodsSpu()),
        gc_category_info(new CategoryInfo()),
        gc_goods_cate_spu(new GoodsCateSpu()),
        gc_config_sku(new ConfigSku()),
        partners(new Partners()),

        stock_goods(new StockGoods()),

        partner_stores(new PartnerStores()),

        stock_merchant(new StockMerchant()),

        price_list(new PriceList()),
        price_list_details(new PriceListDetails()),
        price_store(new PriceStore())
        ;

        private Model model;

        BaseDataTable(Model model) {
            this.model = model;
        }

        public Model getModel() {
            return model;
        }

        public void setModel(Model model) {
            this.model = model;
        }

        /**
         * 获取class
         * @param name
         * @return
         */
        public static Class<? extends Model> getClazz(String name) {
            Model model = getModel(name);
            if (Objects.isNull(model)) {
                return null;
            }
            return model.getClass();
        }

        /**
         * 获取model
         * @param name
         * @return
         */
        public static Model getModel(String name) {
            BaseDataTable baseDataTable = getEnum(name);
            if (Objects.isNull(baseDataTable)) {
                return null;
            }
            return baseDataTable.getModel();
        }

        /**
         * 获取enum
         * @param name
         * @return
         */
        public static BaseDataTable getEnum(String name) {
            if (StringUtils.isBlank(name)) {
                return null;
            }
            BaseDataTable baseDataTable = Arrays.stream(BaseDataTable.values()).filter(val -> StringUtils.equals(val.name(), name))
                    .findFirst()
                    .orElse(null);
            if (Objects.isNull(baseDataTable)) {
                return null;
            }
            return baseDataTable;
        }
    }

    enum ToHbaseTable{
        partner_store_goods,
        /*gc_goods_sales_statistics_merchant,*/
        partner_goods,
        platform_goods,
        gc_config_sku,
        stock_goods
        ;
    }

    enum ElasticTable {
        elastic_o2o(new ElasticO2O()),
        elastic_b2c(new ElasticO2O());
        private Model model;

        ElasticTable(Model model) {
            this.model = model;
        }

        public Model getModel() {
            return model;
        }

        public void setModel(Model model) {
            this.model = model;
        }

    }

    enum BasicTimeSyncTable {
        refresh_partner_goods_v10(new PartnerGoods()),
        refresh_partner_goods_v9(new PartnerGoods()),

        gc_goods_manual(new PartnerGoods()),
        gc_base_nootc(new PartnerGoods()),
        gc_standard_goods_syncrds(new PartnerGoods()),
        gc_base_spu_img(new PartnerGoods()),
        partner_goods_img(new PartnerGoods()),
        partner_goods_info(new PartnerGoods()),
        gc_goods_dosage(new PartnerGoods()),
        gc_partner_goods_gift(new PartnerGoods()),
        base_goods(new PartnerGoods()),
        merchant_goods_category_mapping(new PartnerGoods()),
        gc_goods_spu_attr_syncrds(new PartnerGoods()),
        gc_goods_attr_info_syncrds(new PartnerGoods()),
        pgc_store_info(new PgcStoreInfo()),
        organize_base(new OrganizeBase()),
        pgc_store_info_increment(new PgcStoreInfoIncrement()),
        platform_goods(new PlatformGoods()),
        pgc_merchant_info(new PgcMerchantInfo()),
        gc_goods_overweight(new PartnerGoods()),
        gc_sku_extend(new SkuExtend()),

        ;

        private Model model;

        BasicTimeSyncTable(Model model) {
            this.model = model;
        }

        public Model getModel() {
            return model;
        }

        public void setModel(Model model) {
            this.model = model;
        }

    }
}
