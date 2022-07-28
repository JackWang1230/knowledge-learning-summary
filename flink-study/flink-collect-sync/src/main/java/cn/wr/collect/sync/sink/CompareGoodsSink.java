package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.dao.partner.PartnerGoodsDao;
import cn.wr.collect.sync.hbase.HBaseService;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.utils.HBaseUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static cn.wr.collect.sync.constants.CommonConstants.HBASE_PARTNER_GOODS;

@Deprecated
public class CompareGoodsSink extends RichSinkFunction<PartnerGoods> {
    private static final long serialVersionUID = -1308033861317401214L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CompareGoodsSink.class);
    private HBaseService hBaseService;
    private ParameterTool parameterTool;

    @Override
    public void open(Configuration parameters) throws Exception {
        LOGGER.info("### CompareGoodsFlatMap open");
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
        LOGGER.info("### CompareGoodsFlatMap close");
    }

    @Override
    public void invoke(PartnerGoods goods, Context context) throws Exception {
        PartnerGoodsDao goodsDao = new PartnerGoodsDao(parameterTool);
        PartnerGoods partnerGoods = goodsDao.querySingle(goods.getId());
        if (Objects.isNull(partnerGoods)) {
            hBaseService.delete(HBASE_PARTNER_GOODS, goods.getDbId() + "-" + goods.getInternalId());
        }
    }
}
