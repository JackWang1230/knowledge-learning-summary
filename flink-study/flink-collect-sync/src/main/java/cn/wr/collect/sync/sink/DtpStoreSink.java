package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.function.Compute;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.gc.OrganizeBase;
import cn.wr.collect.sync.model.partner.Partners;
import cn.wr.collect.sync.redis.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.RedisConstant.REDIS_KEY_DTP_STORE;

public class DtpStoreSink extends RichSinkFunction<BasicModel<Model>> {
    private static final long serialVersionUID = 6342457884235471690L;
    private static final Logger log = LoggerFactory.getLogger(DtpStoreSink.class);

    private RedisService redisService;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool parameterTool = (ParameterTool) getRuntimeContext()
                .getExecutionConfig().getGlobalJobParameters();

        redisService = new RedisService(parameterTool);
    }

    @Override
    public void invoke(BasicModel<Model> basic, Context context) {
        Table.BaseDataTable table = Table.BaseDataTable.getEnum(basic.getTableName());

        if (Objects.isNull(table)) {
            return;
        }

        String operate = basic.getOperate();
        int storeId;
        Integer dtpStore;
        switch (table) {
            case organize_base:
                OrganizeBase organizeBase = (OrganizeBase) basic.getData();
                storeId = organizeBase.getOrganizationId().intValue();
                List<Partners> partnersList = redisService.queryPartners(organizeBase.getRootId().intValue());
                if (CollectionUtils.isEmpty(partnersList)) {
                    log.info("DtpStoreSink partnersList is null");
                    return;
                }
                List<Integer> dbIdList = partnersList.stream()
                        .map(Compute::dbId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(dbIdList)) {
                    log.info("DtpStoreSink dbIdList is null");
                    return;
                }
                dtpStore = Compute.isDtpStore(organizeBase);
                dbIdList.forEach(dbId -> this.sink(operate, basic.getTableName(), dbId, storeId, dtpStore));
                break;

            case partners:
                Partners partners = (Partners) basic.getData();
                Integer dbId = Compute.dbId(partners);
                if (Objects.isNull(dbId)) {
                    log.info("DtpStoreSink dbid is null");
                    return;
                }
                Integer merchantId = partners.getOrganizationId();
                List<OrganizeBase> organizeBaseList = redisService.queryOrganizeBase(merchantId);
                if (Objects.isNull(organizeBaseList)) {
                    log.info("DtpStoreSink organizeBaseList is null");
                    return;
                }
                organizeBaseList.forEach(ob -> {
                    Integer dtpStoreOb = Compute.isDtpStore(ob);
                    this.sink(operate, basic.getTableName(), dbId, ob.getOrganizationId().intValue(), dtpStoreOb);
                });
                break;

            default:
                break;
        }
    }

    /**
     * sink
     * @param operate
     * @param tableName
     * @param dbId
     * @param storeId
     * @param dtpStore
     */
    private void sink(String operate, String tableName, Integer dbId, Integer storeId, Integer dtpStore) {
        if (Objects.isNull(dbId) || Objects.isNull(storeId) || Objects.isNull(dtpStore)) {
            return;
        }

        switch (operate) {
            case CommonConstants.OPERATE_DELETE:
            case CommonConstants.OPERATE_UPDATE_DELETE:
                log.info("DtpStoreSink delete table:{}, operate:{}, dbId:{}, storeId:{}, dtpStore:{}",
                        tableName, operate, dbId, storeId, dtpStore);
                redisService.deleteSet(REDIS_KEY_DTP_STORE + dbId, String.valueOf(storeId));
                break;

            case CommonConstants.OPERATE_INSERT:
            case CommonConstants.OPERATE_UPDATE:
                if (dtpStore.equals(CommonConstants.IS_DTP_STORE_TRUE)) {
                    log.info("DtpStoreSink add table:{}, operate:{}, dbId:{}, storeId:{}, dtpStore:{}",
                            tableName, operate, dbId, storeId, dtpStore);
                    redisService.addSet(REDIS_KEY_DTP_STORE + dbId, String.valueOf(storeId));
                }
                else {
                    log.info("DtpStoreSink delete table:{}, operate:{}, dbId:{}, storeId:{}, dtpStore:{}",
                            tableName, operate, dbId, storeId, dtpStore);
                    redisService.deleteSet(REDIS_KEY_DTP_STORE + dbId, String.valueOf(storeId));
                }
                break;

            default:
                break;
        }
    }
}
