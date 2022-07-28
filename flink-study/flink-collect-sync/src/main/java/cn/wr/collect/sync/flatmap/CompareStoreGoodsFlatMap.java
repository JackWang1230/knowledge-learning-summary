package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.hbase.HBaseService;
import cn.wr.collect.sync.model.CompareData;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import cn.wr.collect.sync.utils.HBaseUtils;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

import static cn.wr.collect.sync.constants.CommonConstants.HBASE_COMPARE_PAGE_SIZE;

public class CompareStoreGoodsFlatMap extends RichFlatMapFunction<CompareData, PartnerStoreGoods> {
    private static final long serialVersionUID = 4264914347965488843L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CompareStoreGoodsFlatMap.class);
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
    public void flatMap(CompareData compareData, Collector<PartnerStoreGoods> collector) {
        LOGGER.info("json:{}", JSON.toJSONString(compareData));
        if (Objects.isNull(compareData)) {
            return;
        }

        int pageSize = HBASE_COMPARE_PAGE_SIZE;

        List<PartnerStoreGoods> storeGoodsList;
        String lastRowKey = compareData.getDbId() + "-" + compareData.getGroupId() + "-0";
        while (true) {
            storeGoodsList = hBaseService.fuzzyQueryStoreGoods(compareData.getDbId(), compareData.getGroupId(), lastRowKey);
            // 查询tidb是否存在
            for (PartnerStoreGoods storeGoods : storeGoodsList) {
                lastRowKey = storeGoods.getDbId() + "-" + storeGoods.getGroupId() + "-" + storeGoods.getGoodsInternalId();
                collector.collect(storeGoods);
            }

            // 跳出循环
            if (pageSize > storeGoodsList.size()) {
                break;
            }
        }
    }

}
