package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.dao.gc.StandardGoodsSyncrdsDao;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.model.basic.BasicModel;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.wr.collect.sync.constants.CommonConstants.BASIC_TIME_GOODS;


public class BasicSelfSyncSource extends RichSourceFunction<BasicModel<PgConcatParams>> {
    private static final long serialVersionUID = 3138985609168664595L;
    private static final Logger log = LoggerFactory.getLogger(BasicSelfSyncSource.class);
    private StandardGoodsSyncrdsDao standardDAO;


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        standardDAO = new StandardGoodsSyncrdsDao(tool);
    }

    @Override
    public void cancel() {
        log.info("BasicSyncSource cancel ...");
    }

    @Override
    public void run(SourceContext<BasicModel<PgConcatParams>> ctx) {
        List<PgConcatParams> redisList = standardDAO.queryTradeCodeList();
        AtomicInteger count = new AtomicInteger(0);
        int size = redisList.size();
        redisList.forEach(item -> {
            int current = count.incrementAndGet();
            if (StringUtils.isBlank(item.getApprovalNumber())
                    && StringUtils.isBlank(item.getDbId()) && StringUtils.isBlank(item.getInternalId())
                    && StringUtils.isBlank(item.getTradeCode())) {
                return;
            }
            log.info("BasicSelfSyncSource total:{}, current:{}, param:{}", size, current, JSON.toJSONString(item));
            ctx.collect(new BasicModel<>(BASIC_TIME_GOODS, CommonConstants.OPERATE_INSERT, item));
        });
    }

}
