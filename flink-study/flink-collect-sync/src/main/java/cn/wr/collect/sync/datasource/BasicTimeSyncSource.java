package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
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

import static cn.wr.collect.sync.constants.CommonConstants.BASIC_TIME_GOODS;
import static cn.wr.collect.sync.constants.CommonConstants.BASIC_TIME_STORE_GOODS;
import static cn.wr.collect.sync.constants.PropertiesConstants.SCHEDULED_JOB_BASIC;
import static cn.wr.collect.sync.constants.RedisConstant.REDIS_REFRESH_DATA_PREFIX;
import static cn.wr.collect.sync.constants.RedisConstant.REDIS_TABLE_PREFIX;

public class BasicTimeSyncSource extends RichSourceFunction<BasicModel<PgConcatParams>> {
    private static final long serialVersionUID = 3138985609168664595L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicTimeSyncSource.class);
    // 定时任务时间差
    private static long TIME_DIFFERENCE_SECOND = 120;
    private RedisService redisService;
    private static boolean EXIT_FLAG = true;
    // 定时任务查询redis key
    private static final String REFRESH_KEY =  REDIS_TABLE_PREFIX + REDIS_REFRESH_DATA_PREFIX
            + Table.BasicTimeSyncTable.refresh_partner_goods_v10.name();

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        redisService  = new RedisService(parameterTool);
        TIME_DIFFERENCE_SECOND = parameterTool.getLong(SCHEDULED_JOB_BASIC);
    }

    @Override
    public void cancel() {
        LOGGER.info("BasicTimeSyncSource cancel ...");
        EXIT_FLAG = false;
    }

    @Override
    public void close() throws Exception {
        super.close();
        EXIT_FLAG = false;
    }

    @Override
    public void run(SourceContext<BasicModel<PgConcatParams>> ctx) {
        while (EXIT_FLAG) {
            try {
                int count = 2;
                while (EXIT_FLAG) {
                    List<PgConcatParams> redisList = redisService.srandmembers(REFRESH_KEY, count);
                    if (CollectionUtils.isEmpty(redisList)) {
                        LOGGER.info("BasicTimeSyncSource query change is empty, return diffTime{}", TIME_DIFFERENCE_SECOND);
                        break;
                    }

                    LOGGER.info("BasicTimeSyncSource query change diffTime{}, size:{}",
                            TIME_DIFFERENCE_SECOND, redisList.size());
                    redisList.forEach(item -> {
                        if (StringUtils.isBlank(item.getGroupId())) {
                            ctx.collect(new BasicModel<>(BASIC_TIME_GOODS, CommonConstants.OPERATE_UPDATE, item));
                        }
                        else {
                            ctx.collect(new BasicModel<>(BASIC_TIME_STORE_GOODS, CommonConstants.OPERATE_UPDATE, item));
                        }
                        redisService.deleteSet(REFRESH_KEY, JSON.toJSONString(item));
                    });
                    if (CollectionUtils.isEmpty(redisList)) {
                        break;
                    }
                }
            } catch (Exception e) {
                LOGGER.error("BasicTimeSyncSource error:{}", e);
            }
            try {
                Thread.sleep(TIME_DIFFERENCE_SECOND * 1000);
            } catch (InterruptedException e) {
                LOGGER.error("BasicTimeSyncSource InterruptedException:{}", e);
                EXIT_FLAG = false;
                return;
            }
        }
    }
}
