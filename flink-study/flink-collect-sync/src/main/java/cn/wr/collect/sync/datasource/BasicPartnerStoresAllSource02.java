package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.constants.ElasticEnum;
import cn.wr.collect.sync.dao.time.QueryPartnerStoresAllDao;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.gc.PartnerStoresAll;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.wr.collect.sync.constants.CommonConstants.BASIC_TIME_GOODS;
import static cn.wr.collect.sync.constants.CommonConstants.BASIC_TIME_STORE_GOODS;
import static cn.wr.collect.sync.constants.CommonConstants.OPERATE_INSERT;
import static cn.wr.collect.sync.constants.PropertiesConstants.SCHEDULED_JOB_START_TIME;
import static cn.wr.collect.sync.constants.PropertiesConstants.SCHEDULED_JOB_STORES;


public class BasicPartnerStoresAllSource02 extends RichSourceFunction<BasicModel<PgConcatParams>> {
    private static final long serialVersionUID = 3138985609168664595L;
    private static final Logger log = LoggerFactory.getLogger(BasicPartnerStoresAllSource02.class);
    // 定时任务时间差
    private static long TIME_DIFFERENCE_SECOND = 120;
    private LocalDateTime startTime;
    private ParameterTool parameterTool;
    private static boolean flag = true;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        String startTimeStr = parameterTool.get(SCHEDULED_JOB_START_TIME);
        if (StringUtils.isNotBlank(startTimeStr)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            startTime = LocalDateTime.parse(startTimeStr, formatter);
        } else {
            startTime = LocalDateTime.now();
        }
        TIME_DIFFERENCE_SECOND = parameterTool.getLong(SCHEDULED_JOB_STORES);
    }

    @Override
    public void cancel() {
        flag = false;
    }

    @Override
    public void run(SourceContext<BasicModel<PgConcatParams>> ctx) {
        while (flag) {
            // 重置起始时间
            LocalDateTime time = startTime;
            this.collectData(ctx, time);

            try {
                Thread.sleep(TIME_DIFFERENCE_SECOND * 1000);
            }
            catch (InterruptedException e) {
                log.error("BasicPartnerStoresAllSource02 InterruptedException:{}", e);
                flag = false;
                return;
            }
        }
    }

    private void collectData(SourceContext<BasicModel<PgConcatParams>> context, LocalDateTime time) {
        Map<String, Object> params = new HashMap<>(1);
        QueryPartnerStoresAllDao queryDao = new QueryPartnerStoresAllDao(parameterTool);
        params.put("startTime", time);
        List<PartnerStoresAll> storesList = queryDao.query(params);
        if (CollectionUtils.isEmpty(storesList)) {
            log.info("BasicPartnerStoresAllSource02 partnerStoresAllList is empty params:{}", params);
            return;
        }
        // 由于数据实际更新时间和数据同步到tidb的时间存在延迟，需缓存时间
        startTime = storesList.get(storesList.size() - 1).getGmtupdated();
        AtomicInteger syncCnt = new AtomicInteger(0);
        storesList
                .stream()
                .filter(ps -> Objects.nonNull(ps) && Objects.nonNull(ps.getDbId()) && Objects.nonNull(ps.getMerchantId())
                        && Objects.nonNull(ps.getStoreId()) && StringUtils.isNotBlank(ps.getGroupId()))
                .forEach(ps -> {
                    int current = syncCnt.incrementAndGet();
                    log.info("BasicPartnerStoresAllSource02 current store total:{}, current:{}, cruId:{}",
                            storesList.size(), current, ps.getId());
                    if (StringUtils.equals(ElasticEnum.O2O.getChannel(), ps.getChannel())) {
                        context.collect(new BasicModel<>(BASIC_TIME_STORE_GOODS,
                                OPERATE_INSERT, PgConcatParams.builder()
                                .dbId(String.valueOf(ps.getDbId()))
                                .merchantId(String.valueOf(ps.getMerchantId()))
                                .storeId(String.valueOf(ps.getStoreId()))
                                .groupId(ps.getGroupId())
                                .build()));
                    } else {
                        context.collect(new BasicModel<>(BASIC_TIME_GOODS,
                                OPERATE_INSERT, PgConcatParams.builder()
                                .dbId(String.valueOf(ps.getDbId()))
                                .merchantId(String.valueOf(ps.getMerchantId()))
                                .storeId(String.valueOf(ps.getStoreId()))
                                .build()));
                    }
                });
    }

}
