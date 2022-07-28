package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.constants.ElasticEnum;
import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.function.Compute;
import cn.wr.collect.sync.hbase.HBaseService;
import cn.wr.collect.sync.model.BasicTimeGoods;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.gc.*;
import cn.wr.collect.sync.model.kafka.ElasticGoodsDTO;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.HBaseUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.CommonConstants.*;


public class GoodsStoreSplitFlatMap extends RichFlatMapFunction<BasicModel<Model>, BasicModel<ElasticGoodsDTO>> {
    private static final long serialVersionUID = -8963452641085490877L;
    private static final Logger log = LoggerFactory.getLogger(GoodsStoreSplitFlatMap.class);
    private RedisService redisService;
    private HBaseService hBaseService;
    private ParameterTool tool;

    @Override
    public void open(Configuration parameters) throws Exception {
        log.info("GoodsStoreSplitFlatMap open");
        super.open(parameters);
        tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        this.redisService = new RedisService(tool);
        this.hBaseService = new HBaseService(HBaseUtils.getConnection(tool));
    }

    @Override
    public void close() throws Exception {
        super.close();
        // 关闭hbase连接
        if (null != hBaseService) {
            hBaseService.closeConnection();
        }
        log.info("GoodsStoreSplitFlatMap close");
    }

    @Override
    public void flatMap(BasicModel<Model> model, Collector<BasicModel<ElasticGoodsDTO>> collector) {
        if (Objects.isNull(model)
                || StringUtils.isBlank(model.getTableName())
                || Objects.isNull(model.getData())
                || StringUtils.isBlank(((BasicTimeGoods) model.getData()).getTradeCode())) {
            return;
        }

        BasicTimeGoods goods = (BasicTimeGoods) model.getData();

        if (this.checkIfTradeCodeModify(model.getTableName(), model.getOperate(), goods)) {
            this.collect2TradeCode(model.getTableName(), model.getOperate(), goods, collector);
        }
        else if (this.checkIfStoreModify(model.getTableName(), model.getOperate(), goods)) {
            this.collect2Store(model.getTableName(), model.getOperate(), goods, collector);
        }
        else {
            this.splitByBasicTimeGoods(model.getTableName(), model.getOperate(), goods, collector);
        }

    }

    /**
     * gc_standard_goods_syncrds 删除trade_code / gc_goods_spu_attr_syncrds 删除trade_code
     * @param tableName
     * @param operate
     * @param goods
     * @return
     */
    private boolean checkIfTradeCodeModify(String tableName, String operate, BasicTimeGoods goods) {
        return (StringUtils.equals(OPERATE_UPDATE_DELETE, operate)
                || StringUtils.equals(OPERATE_DELETE, operate)) &&
                (StringUtils.equals(Table.BaseDataTable.gc_standard_goods_syncrds.name(), tableName)
                        || StringUtils.equals(Table.BaseDataTable.gc_goods_spu_attr_syncrds.name(), tableName))
                && StringUtils.isNotBlank(goods.getTradeCode());
    }

    /**
     * 校验是否是门店相关表变更
     * @param tableName
     * @param operate
     * @param goods
     * @return
     */
    private boolean checkIfStoreModify(String tableName, String operate, BasicTimeGoods goods) {
        // 如果是pgc_store_info_increment表变更，且是删除/更删操作，location字段直接取删除的字段
        return (StringUtils.equals(OPERATE_UPDATE_DELETE, operate)
                || StringUtils.equals(OPERATE_DELETE, operate)) &&
                (StringUtils.equals(Table.BaseDataTable.gc_partner_stores_all.name(), tableName)
                        || StringUtils.equals(Table.BaseDataTable.pgc_store_info_increment.name(), tableName));
    }

    /**
     * trade_code 下发下个算子
     * @param tableName
     * @param operate
     * @param goods
     * @param collector
     */
    private void collect2TradeCode(String tableName, String operate, BasicTimeGoods goods, Collector<BasicModel<ElasticGoodsDTO>> collector) {
        collector.collect(new BasicModel<>(tableName, operate,
                        ElasticGoodsDTO.builder().tradeCode(goods.getTradeCode()).build()));
    }

    /**
     * store 下发下个算子
     * @param tableName
     * @param operate
     * @param goods
     * @param collector
     */
    private void collect2Store(String tableName, String operate, BasicTimeGoods goods, Collector<BasicModel<ElasticGoodsDTO>> collector) {
        String location;
        // pgc_store_info_increment 表如果发生
        if (StringUtils.equals(Table.BaseDataTable.pgc_store_info_increment.name(), tableName)) {
            location = goods.getLocation();
        }
        else {
            PgcStoreInfoIncrement info = redisService.queryPgcStoreInfoIncrement(goods.getMerchantId(), goods.getStoreId());
            location = Compute.location(info);
        }
        collector.collect(new BasicModel<>(tableName, operate,
                ElasticGoodsDTO.builder()
                        .tradeCode(goods.getTradeCode())
                        .merchantId(goods.getMerchantId())
                        .storeId(goods.getStoreId())
                        .location(location)
                        .build()));
    }


    /**
     * 根据partner_goods表数据变更拆分数据
     *
     * @param tableName
     * @param operate
     * @param goods
     * @param collector
     */
    private void splitByBasicTimeGoods(String tableName, String operate, BasicTimeGoods goods,
                                       Collector<BasicModel<ElasticGoodsDTO>> collector) {
        if (null == goods.getDbId() || StringUtils.isBlank(goods.getInternalId())) {
            return;
        }

        List<PartnerStoresAll> list = redisService.queryPartnerStoresAll(goods.getDbId());
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        if (Objects.nonNull(goods.getMerchantId())) {
            list = list.stream().filter(item -> goods.getMerchantId().equals(item.getMerchantId()))
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        if (Objects.nonNull(goods.getStoreId())) {
            list = list.stream().filter(item -> goods.getStoreId().equals(item.getStoreId()))
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<String> rowKeyList = list.stream().map(e -> (e.getDbId() + "-" + e.getGroupId() + "-" + goods.getInternalId()))
                .collect(Collectors.toList());
        List<PartnerStoreGoods> storeGoodsList = hBaseService.queryPartnerStoreGoods(rowKeyList);
//        log.info("dbId:{}, internalId:{}, rsize:{}, size:{}",
//                goods.getDbId(), goods.getInternalId(), rowKeyList.size(), storeGoodsList.size());

        this.transferFromGoods2Collect(tableName, operate, goods, list, storeGoodsList, collector);
    }

    /**
     * partner_store_goods 转 es 对象
     *
     * @param tableName
     * @param operate
     * @param goods
     * @param list
     * @param storeGoodsList
     */
    private void transferFromGoods2Collect(String tableName, String operate, BasicTimeGoods goods,
                                           List<PartnerStoresAll> list,
                                           List<PartnerStoreGoods> storeGoodsList,
                                           Collector<BasicModel<ElasticGoodsDTO>> collector) {
        ElasticO2O o2o = this.transferElasticO2OByGoods(operate, goods);
        if (Objects.isNull(o2o)) {
            return;
        }

        for (PartnerStoresAll psa : list) {
            ElasticO2O clone = null;
            if (StringUtils.equals(ElasticEnum.O2O.getChannel(), psa.getChannel())) {
                if (CollectionUtils.isEmpty(storeGoodsList)) {
                    continue;
                }
                // o2o 商品超重返回
                if (StringUtils.equals(OPERATE_INSERT, operate)
                        && GOODS_OVERWEIGHT_STATUS_TRUE.equals(o2o.getIsOverweight())) {
                    continue;
                }
                PartnerStoreGoods storeGoods = storeGoodsList.stream()
                        .filter(e -> psa.getDbId().equals(e.getDbId()) && StringUtils.equals(psa.getGroupId(), e.getGroupId())
                                && StringUtils.equals(goods.getInternalId(), e.getGoodsInternalId()))
                        .findFirst().orElse(null);
                if (null == storeGoods) {
                    continue;
                }
                clone = (ElasticO2O) o2o.clone();
                this.completeElasticO2O(operate, clone, psa, goods, storeGoods);

            } else if (StringUtils.equals(ElasticEnum.B2C.getChannel(), psa.getChannel())) {
                clone = (ElasticO2O) o2o.clone();
                this.completeElasticO2O(operate, clone, psa, goods, null);
            }

            if (Objects.isNull(clone)) {
                continue;
            }

            collector.collect(new BasicModel<>(tableName, operate, new ElasticGoodsDTO().transfer(clone)));

        }

    }

    /**
     * 参数转换 goods -> elastic
     *
     * @param operate
     * @param goods
     * @return
     */
    public ElasticO2O transferElasticO2OByGoods(String operate, PartnerGoods goods) {
        ElasticO2O o2o = new ElasticO2O();
        o2o.setRealTradeCode(Compute.realTradeCode(goods));
        if (StringUtils.equals(OPERATE_UPDATE_DELETE, operate) || StringUtils.equals(OPERATE_DELETE, operate)) {
            return o2o;
        }
        o2o.setDbId(Compute.dbId(goods));
        o2o.setGoodsInternalId(Compute.goodsInternalId(goods));
        o2o.setApprovalNumber(Compute.approvalNumber(goods, null));

        // is_off_shelf
        /*PartnerGoodsGift goodsGift = redisService.queryGcPartnerGoodsGift(o2o.getDbId(), o2o.getGoodsInternalId());
        o2o.setIsOffShelf(Compute.isOffShelf(goodsGift, o2o.getIsOffShelf()));*/

        StandardGoodsSyncrds standard = redisService.queryGcStandardGoodsSyncrds(o2o.getRealTradeCode());
        o2o.setIsStandard(Compute.isStandard(standard));

        o2o.setIsEphedrine(false);
        Set<String> attrIdList = new HashSet<>();
        if (Objects.nonNull(standard) && Objects.nonNull(standard.getSpuId())) {
            // is_double / is_ephedrine
            List<GoodsSpuAttrSyncrds> spuAttrList = redisService.queryGcGoodsSpuAttrSyncrds(standard.getTradeCode());
            if (CollectionUtils.isNotEmpty(spuAttrList)) {
                for (GoodsSpuAttrSyncrds spuAttr : spuAttrList) {
                    if (Objects.isNull(spuAttr.getAttrId())) {
                        continue;
                    }
                    GoodsAttrInfoSyncrds attr = redisService.queryGcGoodsAttrInfoSyncrds(spuAttr.getAttrId());
                    if (Objects.isNull(attr)) {
                        continue;
                    }
                    attrIdList.addAll(Arrays.asList(attr.getPids().split(SymbolConstants.COMMA_EN)));
                    attrIdList.add(String.valueOf(attr.getId()));
                    if (!o2o.getIsEphedrine()) {
                        o2o.setIsEphedrine(Compute.isEphedrine(attr));
                    }
                    if (o2o.getIsEphedrine()) {
                        break;
                    }
                }
            }
        }
        o2o.setIsDtp(Compute.isDtp(attrIdList));

        // 麻黄碱返回
        if (StringUtils.equals(OPERATE_INSERT, operate) && o2o.getIsEphedrine()) {
            return null;
        }

        // is_overweight: 默认0 不超重
        GoodsOverweight goodsOverweight = redisService.queryGoodsOverweight(o2o.getRealTradeCode());
        o2o.setIsOverweight(Compute.isOverweight(goodsOverweight));

        return o2o;
    }


    /**
     * 补充es写入数据
     *
     * @param operate
     * @param o2o
     * @param stores
     * @param goods
     * @param storeGoods
     * @return
     */
    public boolean completeElasticO2O(String operate, ElasticO2O o2o, PartnerStoresAll stores,
                                      BasicTimeGoods goods, PartnerStoreGoods storeGoods) {
        // channel
        o2o.setChannel(Compute.channel(stores));
        // merchant_id
        o2o.setMerchantId(Compute.merchantId(stores));
        // store_id
        o2o.setStoreId(Compute.storeId(stores));

        // location: 经纬度
        PgcStoreInfoIncrement pgcStoreInfoIncrement = redisService.queryPgcStoreInfoIncrement(stores.getMerchantId(), stores.getStoreId());
        o2o.setLocation(Compute.location(pgcStoreInfoIncrement));

        if (StringUtils.equals(OPERATE_DELETE, operate)
                || StringUtils.equals(OPERATE_UPDATE_DELETE, operate)) {
            return true;
        }

        // is_off_shelf
        if (StringUtils.equals(ElasticEnum.B2C.getChannel(), stores.getChannel())) {
            o2o.setSalePrice(Compute.salePrice(goods));
            o2o.setIsOffShelf(Compute.isOffShelf(goods, o2o));
        } else if (StringUtils.equals(ElasticEnum.O2O.getChannel(), stores.getChannel())) {
            o2o.setIsOffShelf(Compute.isOffShelf(goods, storeGoods, o2o));
            o2o.setSalePrice(Compute.salePrice(storeGoods));
        }
        if (Objects.isNull(o2o.getSalePrice()) || o2o.getSalePrice().compareTo(GOODS_PRICE_DIME) < 0) {
            return false;
        }

        // is_wr_off_shelf: 连锁上下架
        PlatformGoods platformGoods = hBaseService.queryPlatformGoods(o2o.getSkuCode());
        o2o.setIswrOffShelf(Compute.iswrOffShelf(platformGoods));

        // o2o下架返回
        if (Objects.nonNull(o2o.getIswrOffShelf()) && o2o.getIswrOffShelf()) {
            return false;
        }

        // store_status: 门店状态
        // isDtp代表是否开启DTP
        OrganizeBase base = redisService.queryOrganizeBase(stores.getMerchantId(), stores.getStoreId());
        o2o.setStoreStatus(Compute.storeStatus(base));
        o2o.setIsDtpStore(Compute.isDtpStore(base));

        // storeStatus门店未开启o2o返回
        return STORE_O2O_OPEN.equals(o2o.getStoreStatus());
    }
}
