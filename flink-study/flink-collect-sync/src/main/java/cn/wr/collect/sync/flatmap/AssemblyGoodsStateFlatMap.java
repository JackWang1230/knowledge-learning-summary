package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.dao.time.QueryStoreGoodsDao;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.gc.OrganizeBase;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import cn.wr.collect.sync.model.partner.PartnerStores;
import cn.wr.collect.sync.model.stock.StockGoods;
import cn.wr.collect.sync.model.stock.StockGoodsSideOut;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.RedisPoolUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static cn.wr.collect.sync.constants.SqlConstants.SQL_STORE_GOODS;

public class AssemblyGoodsStateFlatMap extends RichFlatMapFunction<BasicModel<Model>, StockGoodsSideOut> {
    private static final long serialVersionUID = -4560552705276099681L;
    private static final Logger log = LoggerFactory.getLogger(AssemblyGoodsStateFlatMap.class);

    private static boolean RUNNING = true;
    private static final String[] STORE_GOODS_FIELDS = {"status"};
    private static final String[] STORES_FIELDS = {"group_id", "out_id"};
    private RedisService redisService;
    private static ParameterTool tool;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        redisService = new RedisService(tool);
    }

    @Override
    public void close() throws Exception {
        super.close();
        RUNNING = false;
        RedisPoolUtil.closePool();
    }

    @Override
    public void flatMap(BasicModel<Model> model, Collector<StockGoodsSideOut> collector) throws Exception {
        if (!this.checkIfNeedPush(model)) {
            return;
        }

        switch (model.getTableName()) {
            case CommonConstants.PARTNER_STORE_GOODS:
                PartnerStoreGoods storeGoods = (PartnerStoreGoods) model.getData();
                this.assemblyByStoreGoods(storeGoods, collector);
                break;

            case CommonConstants.PARTNER_STORES:
                PartnerStores stores = (PartnerStores) model.getData();
                this.assemblyByStores(stores, collector);
                break;

            default:
                log.error("AssemblyGoodsStateFlatMap unknown table: {}, json: {}", model.getTableName(),
                        JSON.toJSONString(model));
                break;
        }
    }


    /**
     * 校验是否需要推送
     * @param model
     */
    private boolean checkIfNeedPush(BasicModel<Model> model) {
        if (StringUtils.isBlank(model.getOperate()) || StringUtils.isBlank(model.getTableName())) {
            return false;
        }
        // 操作类型 默认大写
        String operate = model.getOperate();
        switch (operate) {
            case CommonConstants.OPERATE_DELETE:
            case CommonConstants.OPERATE_UPDATE_DELETE:
                // 删除操作不处理
                return false;

            case CommonConstants.OPERATE_INSERT:
                return true;

            case CommonConstants.OPERATE_UPDATE:
                switch (model.getTableName()) {
                    case CommonConstants.PARTNER_STORE_GOODS:
                        return Arrays.stream(STORE_GOODS_FIELDS).anyMatch(f -> model.getModFieldList().contains(f));

                    case CommonConstants.PARTNER_STORES:
                        return Arrays.stream(STORES_FIELDS).anyMatch(f -> model.getModFieldList().contains(f));

                    default:
                        log.error("AssemblyGoodsStateFlatMap unknown table: {}, json: {}", model.getTableName(),
                                JSON.toJSONString(model));
                        break;
                }
                break;

            default:
                log.error("AssemblyGoodsStateFlatMap unknown operate: {}, json: {}", model.getOperate(),
                        JSON.toJSONString(model));
                break;
        }

        return false;
    }

    /**
     * store_goods 组装上下架状态
     * @param storeGoods
     * @param collector
     */
    private void assemblyByStoreGoods(PartnerStoreGoods storeGoods, Collector<StockGoodsSideOut> collector) {
        Integer dbId = storeGoods.getDbId();
        String groupId = storeGoods.getGroupId();
        // 获取门店
        List<PartnerStores> storesList = redisService.queryPartnerStores(dbId, groupId);
        if (CollectionUtils.isEmpty(storesList)) {
            return;
        }
        storesList.stream()
                .filter(store -> Objects.nonNull(store) && Objects.nonNull(store.getOutId()) && store.getOutId() != 0L)
                .forEach(store -> {
                    // 根据门店获取连锁id
                    OrganizeBase organizeBase = redisService.queryOrganizeBaseByStoreId(store.getOutId());
                    if (Objects.isNull(organizeBase)) {
                        return;
                    }
                    collector.collect(this.transfer(organizeBase, storeGoods));
                });
    }

    /**
     * stores 组装上下级状态
     * @param stores
     * @param collector
     */
    private void assemblyByStores(PartnerStores stores, Collector<StockGoodsSideOut> collector) {
        if (Objects.isNull(stores)
                || Objects.isNull(stores.getDbId()) || StringUtils.isBlank(stores.getGroupId())
                || Objects.isNull(stores.getOutId()) || stores.getOutId() == 0) {
            log.info("AssemblyGoodsStateFlatMap assemblyByStores stores is not valid: {}", JSON.toJSONString(stores));
            return;
        }
        OrganizeBase organizeBase = redisService.queryOrganizeBaseByStoreId(stores.getOutId());
        if (Objects.isNull(organizeBase)) {
            return;
        }

        PgConcatParams params = new PgConcatParams();
        params.setDbId(String.valueOf(stores.getDbId()));
        params.setGroupId(stores.getGroupId());
        QueryStoreGoodsDao queryDAO = new QueryStoreGoodsDao(SQL_STORE_GOODS, params, tool);
        long id = 0L;
        int pageSize = 1000;
        Map<String, Object> map = new HashMap<>();
        while (RUNNING) {
            map.put("id", id);
            List<PartnerStoreGoods> storeGoodsList = queryDAO.findLimit(id, pageSize, map, null);
            if (CollectionUtils.isEmpty(storeGoodsList)) {
                break;
            }
            storeGoodsList.forEach(storeGoods -> collector.collect(this.transfer(organizeBase, storeGoods)));
            id = storeGoodsList.get(storeGoodsList.size() - 1).getId();
            if (storeGoodsList.size() < pageSize) {
                break;
            }
        }
    }

    /**
     * 参数转换 => StockGoods
     * @param organizeBase
     * @param storeGoods
     * @return
     */
    private StockGoodsSideOut transfer(OrganizeBase organizeBase, PartnerStoreGoods storeGoods) {
        if (Objects.isNull(organizeBase.getRootId()) || Objects.isNull(organizeBase.getOrganizationId())
            || StringUtils.isBlank(storeGoods.getGoodsInternalId()) || Objects.isNull(storeGoods.getStatus())) {
            return null;
        }
        StockGoodsSideOut stockGoodsSideOut = new StockGoodsSideOut();
        StockGoods stockGoods = new StockGoods();
        stockGoods.setStoreId(organizeBase.getOrganizationId());
        stockGoods.setMerchantId(organizeBase.getRootId());
        stockGoods.setInternalId(storeGoods.getGoodsInternalId());
        stockGoods.setSaleState(storeGoods.getStatus());
        // 数据来源 0 商品中心创建 1连锁ERP同步 2商品中心更新  3商家服务平台更新 4云联ERP  5实时更新
        stockGoods.setSource(1);
        stockGoodsSideOut.setNetType(organizeBase.getNetType());
        stockGoodsSideOut.setStockGoods(stockGoods);
        return stockGoodsSideOut;
    }
}
