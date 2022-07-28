package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.dao.middledb.PgcStoreInfoDAO;
import cn.wr.collect.sync.dao.middledb.PncTableSyncDateDAO;
import cn.wr.collect.sync.model.SqoopDataEvent;
import cn.wr.collect.sync.model.middledb.PgcStoreInfoShort;
import cn.wr.collect.sync.model.middledb.PncTableSyncDate;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.DateUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

import static cn.wr.collect.sync.constants.CommonConstants.FLAG_TRUE;
import static cn.wr.collect.sync.constants.PropertiesConstants.GOODSCENTER_FIRST_INIT;
import static cn.wr.collect.sync.constants.PropertiesConstants.SCHEDULED_GOODSCENTER_TIME;
import static cn.wr.collect.sync.constants.RedisConstant.PGC_STORE_INFO_SHORT_KEY;
import static cn.wr.collect.sync.constants.RedisConstant.PGC_STORE_INFO_SHORT_REFRESH_KEY;
import static cn.wr.collect.sync.constants.RedisConstant.REDIS_TABLE_SEPARATOR;
import static cn.wr.collect.sync.constants.RedisConstant.SQOOP_REDIS_PREFIX;

@Deprecated
public class StoreSyncSource extends RichSourceFunction<SqoopDataEvent<PgcStoreInfoShort>> {
    private static final long serialVersionUID = 2502308608435276094L;
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreSyncSource.class);
    private RedisService redisService;
    private ParameterTool parameterTool;
    private int hour = 4;
    private int minute = 0;
    private int second = 0;
    private boolean firstInit = true;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        redisService = new RedisService(parameterTool);
        // 加载配置 是否首次初始化
        firstInit = parameterTool.getBoolean(GOODSCENTER_FIRST_INIT);
        // 加载配置 每天定时同步时间点
        String[] scheduledTime = parameterTool.get(SCHEDULED_GOODSCENTER_TIME).split(":");
        hour = Integer.valueOf(scheduledTime[0]);
        minute = Integer.valueOf(scheduledTime[1]);
        second = Integer.valueOf(scheduledTime[2]);
        LOGGER.info("### StoreSyncSource config firstInit:{} hour:{}, minute:{}, second:{}", firstInit, hour, minute, second);
    }

    @Override
    public void cancel() {}

    @Override
    public void run(SourceContext<SqoopDataEvent<PgcStoreInfoShort>> context) {
        while (true) {
            if (firstInit) {
                PncTableSyncDateDAO tableSyncDateDAO = new PncTableSyncDateDAO(parameterTool);
                PncTableSyncDate tableSyncDate = tableSyncDateDAO.querySingle(null);
                if (checkIfSyncComplete(tableSyncDate)) {
                    try {
                        PgcStoreInfoDAO storeInfoDAO = new PgcStoreInfoDAO(parameterTool);
                        List<PgcStoreInfoShort> list = storeInfoDAO.queryShort();
                        if (CollectionUtils.isNotEmpty(list)) {
                            redisService.set(SQOOP_REDIS_PREFIX + REDIS_TABLE_SEPARATOR + PGC_STORE_INFO_SHORT_REFRESH_KEY, FLAG_TRUE);
                            redisService.clearRedis(SQOOP_REDIS_PREFIX + REDIS_TABLE_SEPARATOR + PGC_STORE_INFO_SHORT_KEY + REDIS_TABLE_SEPARATOR);
                            context.collect(new SqoopDataEvent<>(list));
                        }
                    }
                    catch (Exception e) {
                        LOGGER.error("### BasicInitSource Exception:{}", e);
                    }
                }
            }
            try {
                long delay = DateUtils.calculateDiffTime(hour, minute, second);
                LOGGER.info("### StoreSyncSource hour:{}, minute:{}, second:{}, delay:{}", hour, minute, second, delay);
                Thread.sleep(delay);
                firstInit = true;
            }
            catch (InterruptedException e) {
                LOGGER.error("### StoreSyncSource InterruptedException:{}", e);
            }
        }
    }


    /**
     * 校验是否数据是否同步完成
     * @param tableSyncDate
     * @return
     */
    private boolean checkIfSyncComplete(PncTableSyncDate tableSyncDate) {
        if (null == tableSyncDate) {
            return false;
        }
        String lastSyncDate = DateUtils.format(tableSyncDate.getLastSyncDate(), "yyyy-MM-dd");
        String currentDate = DateUtils.format(LocalDateTime.now(), "yyyy-MM-dd");
        return StringUtils.equals(currentDate, lastSyncDate);
    }
}
