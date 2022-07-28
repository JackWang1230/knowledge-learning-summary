package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.model.DtpStore;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.RedisPoolUtil;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Objects;

import static cn.wr.collect.sync.constants.RedisConstant.REDIS_KEY_DTP_STORE;

public class DtpStoreInitSink extends RichSinkFunction<DtpStore> {
    private static final long serialVersionUID = 6342457884235471690L;
    private static final Logger log = LoggerFactory.getLogger(DtpStoreInitSink.class);

    private RedisService redisService;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool parameterTool = (ParameterTool) getRuntimeContext()
                .getExecutionConfig().getGlobalJobParameters();

        redisService = new RedisService(parameterTool);
    }

    @Override
    public void close() throws Exception {
        RedisPoolUtil.closePool();
    }

    @Override
    public void invoke(DtpStore dtpStore, Context context) {
        log.info("DtpStoreInitSink dtpStore:{}", JSON.toJSONString(dtpStore));
        if (Objects.isNull(dtpStore) || Objects.isNull(dtpStore.getDbId()) || Objects.isNull(dtpStore.getStoreId())) {
            log.info("DtpStoreInitSink dtpStore is not valid:{}", JSON.toJSONString(dtpStore));
            return;
        }
        redisService.addSet(REDIS_KEY_DTP_STORE + dtpStore.getDbId(),
                Collections.singletonList(String.valueOf(dtpStore.getStoreId())));
    }

}
