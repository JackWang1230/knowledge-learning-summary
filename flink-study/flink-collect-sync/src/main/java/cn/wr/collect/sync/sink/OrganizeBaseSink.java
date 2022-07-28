package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.RedisConstant;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.gc.OrganizeBase;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.RedisPoolUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class OrganizeBaseSink extends RichSinkFunction<BasicModel<Model>> {
    private static final long serialVersionUID = 6342457884235471690L;
    private static final Logger log = LoggerFactory.getLogger(OrganizeBaseSink.class);

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
        super.close();
        RedisPoolUtil.closePool();
    }

    @Override
    public void invoke(BasicModel<Model> basic, Context context) {
        if (Objects.isNull(basic) || Objects.isNull(basic.getData()) || StringUtils.isBlank(basic.getTableName())
            || StringUtils.isBlank(basic.getOperate())) {
            return;
        }
        OrganizeBase data = (OrganizeBase) basic.getData();
        this.sink(basic.getOperate(), basic.getTableName(), data);
    }

    /**
     * sink
     * @param operate
     * @param tableName
     * @param organizeBase
     */
    private void sink(String operate, String tableName, OrganizeBase organizeBase) {
        if (Objects.isNull(organizeBase)) {
            return;
        }

        switch (operate) {
            case CommonConstants.OPERATE_DELETE:
            case CommonConstants.OPERATE_UPDATE_DELETE:
                log.info("OrganizeBaseSink delete table:{}, operate:{}, data:{}",
                        tableName, operate, JSON.toJSONString(organizeBase));
                redisService.delete(RedisConstant.COLLECT_CACHE_ORGANIZE_BASE_STORE + organizeBase.getOrganizationId());
                break;

            case CommonConstants.OPERATE_INSERT:
            case CommonConstants.OPERATE_UPDATE:
                log.info("OrganizeBaseSink set table:{}, operate:{}, data:{}",
                        tableName, operate, organizeBase);
                redisService.set(RedisConstant.COLLECT_CACHE_ORGANIZE_BASE_STORE + organizeBase.getOrganizationId(),
                        JSON.toJSONString(organizeBase));
                break;

            default:
                break;
        }
    }
}
