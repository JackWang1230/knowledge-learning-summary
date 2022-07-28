package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.constants.ElasticEnum;
import cn.wr.collect.sync.constants.PropertiesConstants;
import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.dao.time.QueryPartnerStoresAllDao;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.gc.OrganizeBase;
import cn.wr.collect.sync.model.gc.PartnerStoresAll;
import cn.wr.collect.sync.redis.RedisService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.CommonConstants.*;
import static cn.wr.collect.sync.constants.CommonConstants.BASIC_TIME_GOODS;
import static cn.wr.collect.sync.constants.CommonConstants.BASIC_TIME_STORE_GOODS;
import static cn.wr.collect.sync.constants.CommonConstants.IS_O2O;
import static cn.wr.collect.sync.constants.CommonConstants.OPERATE_INSERT;
import static cn.wr.collect.sync.constants.PropertiesConstants.*;
import static cn.wr.collect.sync.constants.PropertiesConstants.INIT_ES_DB_ID;
import static cn.wr.collect.sync.constants.PropertiesConstants.INIT_ES_END_ID;
import static cn.wr.collect.sync.constants.PropertiesConstants.INIT_ES_START_ID;

public class BasicPartnerStoresAllSource03 extends RichSourceFunction<BasicModel<PgConcatParams>> {
    private static final long serialVersionUID = 3138985609168664595L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicPartnerStoresAllSource03.class);
    private ParameterTool parameterTool;
    private List<Integer> filterDbIdList;
    private RedisService redisService;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        redisService = new RedisService(parameterTool);

        String filterDbIdStr = parameterTool.get(PropertiesConstants.FILTER_DBID);
        if (StringUtils.isNotBlank(filterDbIdStr)) {
            filterDbIdList = Arrays.stream(filterDbIdStr.split(SymbolConstants.COMMA_EN))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        }
        else {
            filterDbIdList = Collections.emptyList();
        }
    }

    @Override
    public void cancel() {
    }

    @Override
    public void run(SourceContext<BasicModel<PgConcatParams>> ctx) {
        try {
            String startId = parameterTool.get(INIT_ES_START_ID);
            String endId = parameterTool.get(INIT_ES_END_ID);
            String dbId = parameterTool.get(INIT_ES_DB_ID);


            this.collectStores(startId, endId, dbId, ctx);
        }
        catch (Exception e) {
            LOGGER.error("collect Exception:{}", e);
        }

    }

    private void collectStores(String startId, String endId, String dbId, SourceContext<BasicModel<PgConcatParams>> ctx) {
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(startId) && StringUtils.isNotBlank(endId)) {
            params.put("startId",  Long.parseLong(startId));
            params.put("endId", Long.parseLong(endId));
        }
        else if (StringUtils.isNotBlank(dbId)) {
            params.put("dbId",  Integer.parseInt(dbId));
        }
        else {
            LOGGER.info("collect params not valid");
        }

        QueryPartnerStoresAllDao queryDao = new QueryPartnerStoresAllDao(parameterTool);
        List<PartnerStoresAll> storeList = queryDao.query(params);
        LOGGER.info("collect params:{},size:{}", params, storeList.size());
        if (CollectionUtils.isEmpty(storeList)) {
            LOGGER.info("collect storeList is empty params:{}", params);
            return;
        }

        int index = 0;
        for (PartnerStoresAll ps : storeList) {
            Map<String, Object> psParams = new HashMap<>();
            psParams.put("id", ps.getId());
            PartnerStoresAll store = queryDao.queryById(psParams);
            LOGGER.info("collect total:{}, sync:{}, curId:{}", storeList.size(), index++, ps.getId());
            if (Objects.isNull(store) || Objects.isNull(store.getDbId()) || Objects.isNull(store.getMerchantId())
                    || Objects.isNull(store.getStoreId()) || StringUtils.isBlank(store.getGroupId())) {
                LOGGER.info("collect store is not valid, stores:{}",
                        JSON.toJSONString(ps));
                continue;
            }
            // 过滤配置dbId
            if (CollectionUtils.isNotEmpty(filterDbIdList) && filterDbIdList.contains(store.getDbId())) {
                continue;
            }
            // 过滤未开通o2o
            /*if (!this.checkO2O(store.getMerchantId(), store.getStoreId())) {
                continue;
            }*/
            if (StringUtils.equals(ElasticEnum.O2O.getChannel(), store.getChannel())) {
                ctx.collect(
                        new BasicModel<>(BASIC_TIME_STORE_GOODS,
                                OPERATE_INSERT, PgConcatParams.builder()
                                .dbId(String.valueOf(store.getDbId()))
                                .merchantId(String.valueOf(store.getMerchantId()))
                                .storeId(String.valueOf(store.getStoreId()))
                                .groupId(store.getGroupId())
                                .build()));
            }
            else {
                ctx.collect(
                        new BasicModel<>(BASIC_TIME_GOODS,
                                OPERATE_INSERT, PgConcatParams.builder()
                                .dbId(String.valueOf(store.getDbId()))
                                .merchantId(String.valueOf(store.getMerchantId()))
                                .storeId(String.valueOf(store.getStoreId()))
                                .build()));
            }

        }
    }

    /**
     * 校验O2O/DTP
     * @param merchantId
     * @param storeId
     * @return
     */
    public boolean checkO2O(Integer merchantId, Integer storeId) {
        OrganizeBase organizeBase = redisService.queryOrganizeBase(merchantId, storeId);
        return Objects.nonNull(organizeBase)
                && IS_O2O.equals(organizeBase.getIsO2O());
    }
}
