package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.dao.dtpstore.DtpStoreDAO;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.RedisPoolUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.CommonConstants.BASIC_TIME_GOODS;
import static cn.wr.collect.sync.constants.PropertiesConstants.SCHEDULED_JOB_BASIC;
import static cn.wr.collect.sync.constants.RedisConstant.REDIS_KEY_REFRESH;
import static cn.wr.collect.sync.constants.RedisConstant.REDIS_KEY_REFRESH_ES;
import static cn.wr.collect.sync.constants.RedisConstant.REDIS_KEY_REFRESH_HBASE;


public class TruncateSyncSource extends RichSourceFunction<BasicModel<PgConcatParams>> {
    private static final Logger log = LoggerFactory.getLogger(TruncateSyncSource.class);
    private static final long serialVersionUID = 1412306237882302751L;
    // 定时任务时间差
    private static long TIME_DIFFERENCE_SECOND = 120;
    private RedisService redisService;
    private static boolean EXIT_FLAG = true;
    private DtpStoreDAO dtpStoreDAO;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        redisService  = new RedisService(parameterTool);
        TIME_DIFFERENCE_SECOND = parameterTool.getLong(SCHEDULED_JOB_BASIC);
        dtpStoreDAO  = new DtpStoreDAO(parameterTool);
    }

    @Override
    public void cancel() {
        log.info("TruncateSyncSource cancel ...");
        EXIT_FLAG = false;
    }

    @Override
    public void run(SourceContext<BasicModel<PgConcatParams>> ctx) {
        while (EXIT_FLAG) {
            try {
                Set<String> dbIdList = redisService.smember(RedisPoolUtil.DataBase.Collect, REDIS_KEY_REFRESH_ES);
                log.info("TruncateSyncSource dbIdList:{}", dbIdList);
                for (String dbId : dbIdList) {
                    PgConcatParams params = new PgConcatParams();
                    params.setDbId(dbId);
                    ctx.collect(new BasicModel<>(BASIC_TIME_GOODS, CommonConstants.OPERATE_UPDATE, params));

                    List<Integer> merchantIdList = dtpStoreDAO.queryMerchantIdListByDbId(Integer.valueOf(dbId));
                    List<String> collect = merchantIdList.stream().map(String::valueOf).collect(Collectors.toList());
                    /*List<PartnerStoresAll> storeList = redisService.queryPartnerStoresAll(Integer.valueOf(dbId));
                    List<String> merchantIdList = storeList.stream().map(store -> String.valueOf(store.getMerchantId()))
                            .distinct().collect(Collectors.toList());*/
                    redisService.deleteSet(RedisPoolUtil.DataBase.GoodsCenter, REDIS_KEY_REFRESH, collect);
                    redisService.deleteSet(RedisPoolUtil.DataBase.Collect, REDIS_KEY_REFRESH_HBASE, dbId);
                    redisService.deleteSet(RedisPoolUtil.DataBase.Collect, REDIS_KEY_REFRESH_ES, dbId);
                }
            } catch (Exception e) {
                log.error("TruncateSyncSource error:{}", e);
            }
            try {
                Thread.sleep(TIME_DIFFERENCE_SECOND * 1000);
            } catch (InterruptedException e) {
                log.error("TruncateSyncSource InterruptedException:{}", e);
                EXIT_FLAG = false;
                return;
            }
        }
    }

    private void sendDingDing(String dbId) {

    }
}
