package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.RedisConstant;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.redis.RedisService;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncProgressIncrSink extends RichSinkFunction<BasicModel<ElasticO2O>> {
    private static final long serialVersionUID = 6342457884235471690L;
    private static final Logger log = LoggerFactory.getLogger(SyncProgressIncrSink.class);

    private static final String BEGIN_TIME = "beginTime";
    private static final String SYNC_CNT = "syncCnt";
    private static final String TOTAL_CNT = "totalCnt";
    private RedisService redisService;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool parameterTool = (ParameterTool) getRuntimeContext()
                .getExecutionConfig().getGlobalJobParameters();
        redisService = new RedisService(parameterTool);
    }

    @Override
    public void invoke(BasicModel<ElasticO2O> basic, Context context) {
        ElasticO2O data = basic.getData();
        switch (basic.getTableName()) {
            case CommonConstants.GC_PARTNER_STORES_ALL:
            case CommonConstants.BASIC_TIME_STORE_GOODS:
                String productKey = String.format(RedisConstant.REDIS_KEY_SYNC_PROGRESS_PRODUCT, data.getStoreId());
                if (redisService.exists(productKey)) {
                    redisService.hincrBy(productKey, SYNC_CNT, 1);
                }
                break;
            case CommonConstants.ORGANIZE_BASE:
                String stateKey = String.format(RedisConstant.REDIS_KEY_SYNC_PROGRESS_STATE, data.getStoreId());
                if (redisService.exists(stateKey)) {
                    redisService.hincrBy(stateKey, SYNC_CNT, 1);
                }
                break;
            default:
                break;
        }
    }


}
