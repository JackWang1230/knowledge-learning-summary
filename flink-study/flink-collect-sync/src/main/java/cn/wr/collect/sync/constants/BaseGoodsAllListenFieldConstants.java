package cn.wr.collect.sync.constants;


public interface BaseGoodsAllListenFieldConstants {


    enum OrganizeBase {

        IS_O2O("isO2O"),
        NET_TYPE("netType");

        private final String fieldEN;

        OrganizeBase(String fieldEN) {
            this.fieldEN = fieldEN;
        }

        public String getFieldEN() {
            return fieldEN;
        }
    }

    enum PlatformGoods {

        STATUS("status");

        private final String fieldEN;

        PlatformGoods(String fieldEN) {
            this.fieldEN = fieldEN;
        }

        public String getFieldEN() {
            return fieldEN;
        }

    }

    enum GcGoodsSpuAttrSyncrds {
        barCode("bar_code"),
        attrId("attr_id");

        private final String fieldEN;

        GcGoodsSpuAttrSyncrds(String fieldEN) {
            this.fieldEN = fieldEN;
        }

        public String getFieldEN() {
            return fieldEN;
        }
    }

    enum GcStandardGoodsSyncrds {
        tradeCode("trade_code"),
        spuId("spu_id");

        private final String fieldEN;

        GcStandardGoodsSyncrds(String fieldEN) {
            this.fieldEN = fieldEN;
        }

        public String getFieldEN() {
            return fieldEN;
        }
    }

    enum GcPartnerStoresAll {
        dbId("db_id"),
        groupId("group_id"),
        merchantId("merchant_id"),
        storeId("store_id"),
        channel("channel");

        private final String fieldEN;

        GcPartnerStoresAll(String fieldEN) {
            this.fieldEN = fieldEN;
        }

        public String getFieldEN() {
            return fieldEN;
        }
    }

    enum PgcStoreInfoIncrement {
        organization_id("organization_id"),
        store_id("store_id"),
        longitude("longitude"),
        latitude("latitude");

        private final String fieldEN;

        PgcStoreInfoIncrement(String fieldEN) {
            this.fieldEN = fieldEN;
        }

        public String getFieldEN() {
            return fieldEN;
        }
    }
}
