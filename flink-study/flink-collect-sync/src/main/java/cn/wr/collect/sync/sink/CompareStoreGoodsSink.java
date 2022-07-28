package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.dao.partner.PartnerStoreGoodsDao;
import cn.wr.collect.sync.hbase.HBaseService;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import cn.wr.collect.sync.utils.HBaseUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static cn.wr.collect.sync.constants.CommonConstants.*;
import static cn.wr.collect.sync.constants.CommonConstants.HBASE_PARTNER_STORE_GOODS;

@Deprecated
public class CompareStoreGoodsSink extends RichSinkFunction<PartnerStoreGoods> {
    private static final long serialVersionUID = 4264914347965488843L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CompareStoreGoodsSink.class);
    private HBaseService hBaseService;
    private ParameterTool parameterTool;

    @Override
    public void open(Configuration parameters) throws Exception {
        LOGGER.info("### CompareStoreGoodsFlatMap open");
        super.open(parameters);
        this.parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        this.hBaseService = new HBaseService(HBaseUtils.getConnection(parameterTool));
    }

    @Override
    public void close() throws Exception {
        super.close();
        // 关闭hbase连接
        if (null != hBaseService) {
            hBaseService.closeConnection();
        }
        LOGGER.info("### CompareStoreGoodsFlatMap close");
    }

    @Override
    public void invoke(PartnerStoreGoods storeGoods, Context context) throws Exception {
        PartnerStoreGoodsDao storeGoodsDao = new PartnerStoreGoodsDao(parameterTool);
        PartnerStoreGoods partnerStoreGoods = storeGoodsDao.querySingle(storeGoods.getId());
        if (Objects.isNull(partnerStoreGoods)) {
            hBaseService.delete(HBASE_PARTNER_STORE_GOODS, storeGoods.getDbId() + "-" + storeGoods.getGroupId() + "-" + storeGoods.getGoodsInternalId());
        }
    }
}
