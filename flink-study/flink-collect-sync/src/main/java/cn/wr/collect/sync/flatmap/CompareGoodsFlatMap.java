package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.hbase.HBaseService;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.utils.HBaseUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

import static cn.wr.collect.sync.constants.CommonConstants.HBASE_COMPARE_PAGE_SIZE;

public class CompareGoodsFlatMap extends RichFlatMapFunction<Integer, PartnerGoods> {
    private static final long serialVersionUID = -3525911623685221834L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CompareGoodsFlatMap.class);
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
    public void flatMap(Integer dbId, Collector<PartnerGoods> collector) {
        LOGGER.info("json:{}", JSON.toJSONString(dbId));
        if (Objects.isNull(dbId)) {
            return;
        }

        int pageSize = HBASE_COMPARE_PAGE_SIZE;
        List<PartnerGoods> partnerGoodsList;
        String lastRowKey = null;
        while (true) {
            partnerGoodsList = hBaseService.fuzzyQueryGoods(dbId, lastRowKey);
            if (CollectionUtils.isEmpty(partnerGoodsList)) {
                break;
            }
            // 查询tidb是否存在
            for (PartnerGoods goods : partnerGoodsList) {
                lastRowKey = goods.getDbId() + "-" + goods.getInternalId();
                collector.collect(goods);
            }

            // 跳出循环
            if (pageSize > partnerGoodsList.size()) {
                break;
            }
        }
    }

}
