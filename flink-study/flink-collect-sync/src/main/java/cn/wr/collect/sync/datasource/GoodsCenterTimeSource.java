package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.model.middledb.GoodsCenterTime;
import cn.wr.collect.sync.model.middledb.PgcMerchantInfoShortInit;
import cn.wr.collect.sync.model.redis.DbMerchant;
import cn.wr.collect.sync.redis.RedisService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.wr.collect.sync.constants.PropertiesConstants.SCHEDULED_JOB_BASIC;
import static cn.wr.collect.sync.constants.RedisConstant.REDIS_GOODS_CENTER;


public class GoodsCenterTimeSource extends RichSourceFunction<PgcMerchantInfoShortInit> {
    private static final long serialVersionUID = -3792999152267636833L;
    private static final Logger log = LoggerFactory.getLogger(GoodsCenterTimeSource.class);
    private RedisService redisService;

    private static long TIME_DIFFERENCE_SECOND = 120;
    private static boolean EXIT_FLAG = true;


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        redisService = new RedisService(parameterTool);
        TIME_DIFFERENCE_SECOND = parameterTool.getLong(SCHEDULED_JOB_BASIC);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void cancel() {
        EXIT_FLAG = false;
    }

    @Override
    public void run(SourceContext<PgcMerchantInfoShortInit> context) throws Exception {
        timeSync(context);
    }

    private void timeSync(SourceContext<PgcMerchantInfoShortInit> context) {
        while (EXIT_FLAG) {
            try {
                int count = 2;
                while (EXIT_FLAG) {
                    List<GoodsCenterTime> redisList = redisService.srandMembersGoodsCenter(REDIS_GOODS_CENTER, count);
                    if (CollectionUtils.isEmpty(redisList)) {
                        log.info("GoodsCenterTimeSource query change is empty, return diffTime{}", TIME_DIFFERENCE_SECOND);
                        break;
                    }

                    log.info("GoodsCenterTimeSource query change diffTime{}, size:{}",
                            TIME_DIFFERENCE_SECOND, redisList.size());
                    redisList.forEach(item -> {
                        List<DbMerchant> dbMerchantList = redisService.queryDbMerchant(item.getDbId());
                        if (CollectionUtils.isEmpty(dbMerchantList)) {
                            log.error("GoodsCenterTimeSource query dbMerchant redis is empty: {}", item.getDbId());
                            redisService.deleteSet(REDIS_GOODS_CENTER, JSON.toJSONString(item));
                            return;
                        }

                        DbMerchant dbMerchant = dbMerchantList.stream()
                                .filter(merchant -> merchant.getMerchantId().equals(item.getMerchantId()))
                                .findFirst().orElse(null);

                        if (Objects.isNull(dbMerchant)) {
                            log.error("GoodsCenterTimeSource query dbMerchant is null: {}", item.getMerchantId());
                            redisService.deleteSet(REDIS_GOODS_CENTER, JSON.toJSONString(item));
                            return;
                        }

                        PgcMerchantInfoShortInit init = new PgcMerchantInfoShortInit();
                        init.setDbId(item.getDbId());
                        init.setGoodsInternalId(item.getGoodsInternalId());
                        List<DbMerchant> merchantList = new ArrayList<>();
                        merchantList.add(dbMerchant);
                        init.setMerchantInfos(merchantList);

                        context.collect(init);
                        redisService.deleteSet(REDIS_GOODS_CENTER, JSON.toJSONString(item));
                    });
                    if (CollectionUtils.isEmpty(redisList)) {
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("GoodsCenterTimeSource error:{}", e);
            }
            try {
                Thread.sleep(TIME_DIFFERENCE_SECOND * 1000);
            } catch (InterruptedException e) {
                log.error("GoodsCenterTimeSource InterruptedException:{}", e);
                EXIT_FLAG = false;
                return;
            }
        }
    }
}
