package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.dao.partner.PartnerGoodsDao;
import cn.wr.collect.sync.model.middledb.PgcMerchantInfoShortInit;
import cn.wr.collect.sync.model.redis.DbMerchant;
import cn.wr.collect.sync.redis.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.PropertiesConstants.INIT_GOODS_CENTER_DB_ID;
import static cn.wr.collect.sync.constants.PropertiesConstants.INIT_GOODS_CENTER_END_TIME;
import static cn.wr.collect.sync.constants.PropertiesConstants.INIT_GOODS_CENTER_MERCHANT_ID;
import static cn.wr.collect.sync.constants.PropertiesConstants.INIT_GOODS_CENTER_START_TIME;


public class GoodsCenterInitSource extends RichSourceFunction<PgcMerchantInfoShortInit> {
    private static final long serialVersionUID = -3792999152267636833L;
    private static final Logger log = LoggerFactory.getLogger(GoodsCenterInitSource.class);
    private RedisService redisService;
    private ParameterTool parameterTool;
    private PartnerGoodsDao partnerGoodsDao;


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        redisService = new RedisService(parameterTool);
        partnerGoodsDao = new PartnerGoodsDao(parameterTool);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void cancel() {

    }

    @Override
    public void run(SourceContext<PgcMerchantInfoShortInit> context) throws Exception {
        String startTime = parameterTool.get(INIT_GOODS_CENTER_START_TIME);
        String endTime = parameterTool.get(INIT_GOODS_CENTER_END_TIME);
        String dbIdStr = parameterTool.get(INIT_GOODS_CENTER_DB_ID);

        if (StringUtils.isNotBlank(dbIdStr)) {
            List<String> dbIdList = Arrays.asList(dbIdStr.split(SymbolConstants.COMMA_EN));
            dbIdList.stream()
                    .distinct()
                    .forEach(dbId -> {
                //按连锁补数据
                List<DbMerchant> infoList = redisService.queryDbMerchant(Integer.valueOf(dbId));
                String merchantIdStr = parameterTool.get(INIT_GOODS_CENTER_MERCHANT_ID);
                if (StringUtils.isNotBlank(merchantIdStr)) {
                    List<String> merchantIdList = Arrays.asList(merchantIdStr.split(SymbolConstants.COMMA_EN));
                    infoList = infoList.stream()
                            .filter(item -> merchantIdList.contains(String.valueOf(item.getMerchantId())))
                            .collect(Collectors.toList());
                }
                PgcMerchantInfoShortInit init = new PgcMerchantInfoShortInit();
                init.setMerchantInfos(infoList);
                init.setDbId(Integer.valueOf(dbId));
                context.collect(init);
            });
        } else if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            //按时间段补数据
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime complementStartTime = LocalDateTime.parse(startTime, formatter);
            LocalDateTime complementEndTime = LocalDateTime.parse(endTime, formatter);
            Map<String, Object> params = new HashMap<>();
            params.put("complementStartTime", complementStartTime);
            params.put("complementEndTime", complementEndTime);
            List<Integer> dbIds = partnerGoodsDao.getDbId(params);
            for (Integer id : dbIds) {
                List<DbMerchant> infoList = redisService.queryDbMerchant(id);
                PgcMerchantInfoShortInit init = new PgcMerchantInfoShortInit();
                init.setMerchantInfos(infoList);
                init.setStartTime(complementStartTime);
                init.setEndTime(complementEndTime);
                init.setDbId(id);
                context.collect(init);
            }
        }
    }
}
