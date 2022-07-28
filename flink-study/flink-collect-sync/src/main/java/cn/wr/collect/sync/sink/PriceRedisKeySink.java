package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.RedisConst;
import cn.wr.collect.sync.function.Compute;
import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.redis.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static cn.wr.collect.sync.constants.CommonConstants.*;


public class PriceRedisKeySink extends RichSinkFunction<BasicModel<ElasticO2O>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PriceRedisKeySink.class);

    private ParameterTool parameterTool = null;
    private RedisService redisService;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        redisService = new RedisService(parameterTool);
    }

    @Override
    public void invoke(BasicModel<ElasticO2O> model, Context context) throws Exception {
        ElasticO2O data = model.getData();

        if (!Objects.isNull(data) && StringUtils.isNotBlank(data.getSkuCode())) {
            String value = Compute.priceRedisKey(data.getSkuCode(), data.getSalePrice(), data.getBasePrice());
            String key = String.format(RedisConst.REDIS_SKU_CODE, value);
            redisService.delete(key);
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
        LOGGER.info("### PriceRedisKeySink close");
    }
}
