package cn.wr.collect.sync.utils;

import cn.wr.collect.sync.constants.FieldRefreshEnum;
import cn.wr.collect.sync.model.MetricItem;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.SplitMiddleData;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.gc.PartnerStoresAll;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.wr.collect.sync.constants.CommonConstants.*;
import static cn.wr.collect.sync.constants.CommonConstants.OPERATE_UPDATE;

public class SelfTestUtils {

    public static void main(String[] args) {
        FieldRefreshEnum refresh = FieldRefreshEnum.gc_base_nootc;
        switch (refresh) {
            case gc_base_spu_img:
            case base_goods:
                System.out.println("11");
                break;
            case gc_base_nootc:
                System.out.println("2222");
                break;
        }
    }


    private static void test(List<Model> models, PartnerStoresAll partnerStores, List<PartnerStoreGoods> storeGoodsList) {
        // List<MetricItem<SplitMiddleData>> list = new ArrayList<>();
        List<MetricItem<SplitMiddleData>> list =
                models.stream()
                        .flatMap(model -> Stream.of(convert01(partnerStores, storeGoodsList, (PartnerGoods) model))
                                ).filter(e -> null != e)
                        .collect(Collectors.toList());
        System.out.println("size:" + list.size());
    }

    private static MetricItem<SplitMiddleData> convert01(PartnerStoresAll partnerStores, List<PartnerStoreGoods> storeGoodsList, PartnerGoods model) {
        PartnerGoods goods = model;
        if (CollectionUtils.isEmpty(storeGoodsList)) {
            return null;
        }
        PartnerStoreGoods storeGoods = storeGoodsList.stream()
                .filter(e -> partnerStores.getDbId().equals(e.getDbId()) && partnerStores.getGroupId().equals(e.getGroupId())
                        && goods.getInternalId().equals(e.getGoodsInternalId()))
                .findFirst().orElse(null);

        if (null != storeGoods) {
            // list.add(item);
            return new MetricItem<>(Table.BaseDataTable.gc_partner_stores_all.name(),
                    OPERATE_UPDATE, new SplitMiddleData(partnerStores, goods, storeGoods));
        }
        return null;
    }

    public static void testOld(List<Model> models, PartnerStoresAll partnerStores, List<PartnerStoreGoods> storeGoodsList) {
        List<MetricItem<SplitMiddleData>> list = new ArrayList<>();
        models.stream().forEach(model -> {
            PartnerGoods goods = (PartnerGoods) model;
            if (CollectionUtils.isNotEmpty(storeGoodsList)) {
                PartnerStoreGoods storeGoods = storeGoodsList.stream()
                        .filter(e -> partnerStores.getDbId().equals(e.getDbId()) && partnerStores.getGroupId().equals(e.getGroupId())
                                && goods.getInternalId().equals(e.getGoodsInternalId()))
                        .findFirst().orElse(null);
                if (null != storeGoods) {
                    SplitMiddleData data = new SplitMiddleData(partnerStores, goods, storeGoods);
                    MetricItem<SplitMiddleData> item = new MetricItem<>();
                    item.setItem(data);
                    item.setTableName(Table.BaseDataTable.gc_partner_stores_all.name());
                    item.setOperate(OPERATE_UPDATE);
                    list.add(item);
                }
            }
        });
    }



    /*public static void main(String[] args) {
        MetricEvent event = new MetricEvent();
        List<MetricItem> itemList = new ArrayList<>();
        MetricItem item1 = new MetricItem(PARTNER_GOODS, "b", new PartnerGoods());
        MetricItem item2 = new MetricItem(PARTNER_STORE_GOODS, "b", new PartnerStoreGoods());
        itemList.add(item1);
        itemList.add(item2);
        event.setFields(itemList);

        List<MetricItem> fields = event.getFields();
        fields.stream().forEach(e -> {
            if (StringUtils.equals(PARTNER_GOODS, e.getTableName())) {
                new SelfTestUtils().convertPG(e.getItem());
            }
            else if (StringUtils.equals(PARTNER_STORE_GOODS, e.getTableName())) {
                new SelfTestUtils().convertPSG(e.getItem());
            }
        });
    }*/

    private <T> void convertPG(T t) {
        PartnerGoods pg = (PartnerGoods)t;
        System.out.println(JSON.toJSONString(pg));
    }
    private <T> void convertPSG(T t) {
        PartnerStoreGoods psg = (PartnerStoreGoods) t;
        System.out.println(JSON.toJSONString(psg));
    }
}
