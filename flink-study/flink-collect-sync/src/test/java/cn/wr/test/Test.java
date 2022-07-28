package cn.wr.test;

import cn.wr.collect.sync.model.annotations.Table;

import static cn.wr.collect.sync.constants.RedisConstant.REDIS_REFRESH_DATA_PREFIX;
import static cn.wr.collect.sync.constants.RedisConstant.REDIS_TABLE_PREFIX;

public class Test {
    private static final String REFRESH_KEY =  REDIS_TABLE_PREFIX + REDIS_REFRESH_DATA_PREFIX
            + Table.BasicTimeSyncTable.refresh_partner_goods_v10.name();


    public static class PriceList {
        String pids;

        public PriceList(String pids) {
            this.pids = pids;
        }

        public String getPids() {
            return pids;
        }

        public void setPids(String pids) {
            this.pids = pids;
        }
    }

    public static void main(String[] args) {
        System.out.println(REFRESH_KEY);

        String s = "123,";
        System.out.println(s.substring(0, s.length() - 1));

       /* PgConcatParams params = new PgConcatParams();
        params.setDbId("210");
//        params.setGroupId("063");
//        params.setInternalId("02450");
        params.setMerchantId("71202");
        params.setStoreId("71203");
        System.out.println(JSON.toJSONString(params));*/

        /*String nu = null;
        String nonull = "时刻记得给";
        String s = nu + nonull;
        System.out.println(s);*/

        /*String dbId = "partner_common_12223".substring(CHAIN_DB_START.length());
        System.out.println(dbId);

        String skuNo = "123-jekjt-34";
        String merchantId = skuNo.substring(0, skuNo.indexOf(SymbolConstants.HOR_LINE));
        String goodsInternal_id = skuNo.substring(skuNo.indexOf(SymbolConstants.HOR_LINE) + 1);
        System.out.println(merchantId);
        System.out.println(goodsInternal_id);

        GoodsCenterTime time = new GoodsCenterTime();
        time.setDbId(1);
        time.setMerchantId(2);
        time.setGoodsInternalId("3");
        System.out.println(JSON.toJSONString(time));*/


        /*String str = "test:%s%s";
        Integer[] arr = {1, 2, 3, 4};
        String format = String.format(str, arr);
        System.out.println(format);*/


        // 排序 list_id 最低 -> 最高
        // List<PriceList> list = new ArrayList<>();
        // list.add(new PriceList("0,1"));
        // list.add(new PriceList("0"));
        // list.add(new PriceList("0,1,2"));
        // list.add(new PriceList("0,1,3"));
        // list.sort((l, m) -> m.getPids().length() - l.getPids().length());
        // list.forEach(l -> System.out.println(l.getPids()));
    }
}
