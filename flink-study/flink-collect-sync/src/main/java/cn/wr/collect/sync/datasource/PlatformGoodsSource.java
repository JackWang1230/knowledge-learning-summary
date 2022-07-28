package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.dao.time.QueryPlatformGoodsDao;
import cn.wr.collect.sync.model.gc.PlatformGoods;
import com.alibaba.fastjson.JSON;
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

import static cn.wr.collect.sync.constants.PropertiesConstants.SCHEDULED_JOB_PLATFORM;
import static cn.wr.collect.sync.constants.PropertiesConstants.SCHEDULED_JOB_START_TIME;

public class PlatformGoodsSource extends RichSourceFunction<PlatformGoods> {
    private static final long serialVersionUID = 3138985609168664595L;
    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformGoodsSource.class);
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

        Long timeDiffSecond = parameterTool.getLong(SCHEDULED_JOB_PLATFORM);
        if (null != timeDiffSecond) {
            TIME_DIFFERENCE_SECOND = timeDiffSecond;
        }
    }

    @Override
    public void cancel() {
        flag = false;
    }

    @Override
    public void run(SourceContext<PlatformGoods> ctx) {
        while (flag) {
            // 重置起始时间
            LocalDateTime time = startTime;
            collectData(ctx, time);

            try {
                Thread.sleep(TIME_DIFFERENCE_SECOND * 1000);
            }
            catch (InterruptedException e) {
                LOGGER.error("### PlatformGoodsSource InterruptedException:{}", e);
                flag = false;
                return;
            }
        }
    }

    private void collectData(SourceContext<PlatformGoods> context, LocalDateTime time) {
        QueryPlatformGoodsDao queryDao = new QueryPlatformGoodsDao(parameterTool);
        Long id = 0L;
        int pageSize = 10000;
        while (flag) {
            LOGGER.info("time:{}, startTime:{}", time, startTime);
            Map<String, Object> params = new HashMap<>(3);
            params.put("startTime", time);
            params.put("id", id);
            params.put("pageSize", pageSize);
            List<PlatformGoods> platformList = queryDao.query(params);
            if (CollectionUtils.isEmpty(platformList)) {
                LOGGER.info("### PlatformGoodsSource is empty params:{}", params);
                break;
            }
            platformList.forEach(p -> {
                if (p.getGmtUpdated().isAfter(startTime)) {
                    startTime = p.getGmtUpdated();
                }
                context.collect(p);
                LOGGER.info("send:{}", JSON.toJSONString(p));
            });
            if (platformList.size() < pageSize) {
                break;
            }
            id = platformList.get(platformList.size() - 1).getId();
        }
    }

}
