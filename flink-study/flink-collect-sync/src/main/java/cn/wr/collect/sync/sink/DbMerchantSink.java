package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.function.Compute;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.partner.Partners;
import cn.wr.collect.sync.redis.RedisService;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static cn.wr.collect.sync.constants.RedisConstant.REDIS_KEY_DB_MERCHANT;

public class DbMerchantSink extends RichSinkFunction<BasicModel<Model>> {
    private static final long serialVersionUID = 6342457884235471690L;
    private static final Logger log = LoggerFactory.getLogger(DbMerchantSink.class);

    private RedisService redisService;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool parameterTool = (ParameterTool) getRuntimeContext()
                .getExecutionConfig().getGlobalJobParameters();

        redisService = new RedisService(parameterTool);
    }

    @Override
    public void invoke(BasicModel<Model> basic, Context context) {
        Table.BaseDataTable table = Table.BaseDataTable.getEnum(basic.getTableName());

        if (Objects.isNull(table) || Objects.isNull(basic.getData()) || Table.BaseDataTable.partners != table) {
            return;
        }

        String operate = basic.getOperate();
        Partners partners = (Partners) basic.getData();
        Integer dbId = Compute.dbId(partners);
        if (Objects.isNull(dbId)) {
            log.info("DbMerchantSink dbId wrong: {}", JSON.toJSONString(basic.getData()));
            return;
        }
        Integer merchantId = Compute.merchantId(partners);
        String merchantName = Compute.fullName(partners);
        if (Objects.isNull(merchantId)) {
            log.info("DbMerchantSink merchantId wrong: {}", JSON.toJSONString(basic.getData()));
            return;
        }
        this.sink(operate, basic.getTableName(), dbId, merchantId, merchantName);
    }

    /**
     * sink
     * @param operate
     * @param tableName
     * @param dbId
     * @param merchantId
     */
    private void sink(String operate, String tableName, Integer dbId, Integer merchantId, String merchantName) {
        if (Objects.isNull(dbId) || Objects.isNull(merchantId)) {
            return;
        }

        switch (operate) {
            case CommonConstants.OPERATE_DELETE:
            case CommonConstants.OPERATE_UPDATE_DELETE:
                log.info("DbMerchantSink delete table:{}, operate:{}, dbId:{}, merchantId:{}",
                        tableName, operate, dbId, merchantId);
                redisService.deleteHash(REDIS_KEY_DB_MERCHANT + dbId, String.valueOf(merchantId));
                break;

            case CommonConstants.OPERATE_INSERT:
            case CommonConstants.OPERATE_UPDATE:
                log.info("DbMerchantSink add table:{}, operate:{}, dbId:{}, merchantId:{}",
                        tableName, operate, dbId, merchantId);
                redisService.addHash(REDIS_KEY_DB_MERCHANT + dbId, String.valueOf(merchantId), merchantName);
                break;

            default:
                break;
        }
    }
}
