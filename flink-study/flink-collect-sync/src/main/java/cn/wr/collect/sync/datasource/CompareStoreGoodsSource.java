package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.dao.time.QueryPartnerStoresAllDao;
import cn.wr.collect.sync.model.CompareData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.wr.collect.sync.constants.PropertiesConstants.COMPARE_DB_ID;
import static cn.wr.collect.sync.constants.PropertiesConstants.COMPARE_MERCHANT_ID;
import static cn.wr.collect.sync.constants.PropertiesConstants.COMPARE_STORE_ID;


public class CompareStoreGoodsSource extends RichSourceFunction<CompareData> {
    private static final long serialVersionUID = 4954574170753500071L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CompareStoreGoodsSource.class);
    private ParameterTool parameterTool;
    private Integer dbId = null;
    private Integer merchantId = null;
    private Integer storeId = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        super.open(parameters);
        String dbIdStr = parameterTool.get(COMPARE_DB_ID);
        String merchantIdStr = parameterTool.get(COMPARE_MERCHANT_ID);
        String storeIdStr = parameterTool.get(COMPARE_STORE_ID);
        if (StringUtils.isNotBlank(merchantIdStr) && StringUtils.isNotBlank(storeIdStr)) {
            merchantId = Integer.valueOf(merchantIdStr);
            storeId = Integer.valueOf(storeIdStr);
        }
        if (StringUtils.isNotBlank(dbIdStr)) {
            dbId = Integer.valueOf(dbIdStr);
        }
    }

    @Override
    public void cancel() {
    }

    @Override
    public void run(SourceContext<CompareData> context) {
        if ((null == merchantId || null == storeId) && null == dbId) {
            LOGGER.info("### CompareStoreGoodsSource params is null, return");
            return;
        }
        QueryPartnerStoresAllDao queryDao = new QueryPartnerStoresAllDao(parameterTool);
        Map<String, Object> params = new HashMap<>();
        params.put("dbId", dbId);
        params.put("merchantId", merchantId);
        params.put("storeId", storeId);
        List<CompareData> compareDataList = queryDao.queryForCompareStoreGoods(params);
        if (CollectionUtils.isEmpty(compareDataList)) {
            LOGGER.info("### CompareStoreGoodsSource compareDataList is empty, return");
            return;
        }
        /*Map<Integer, List<PartnerStoresAll>> collect = storeList.stream()
                .collect(Collectors.groupingBy(PartnerStoresAll::getDbId));
        collect.entrySet().stream().forEach(dbGroup -> {
            Map<String, List<PartnerStoresAll>> groupGroup = dbGroup.getValue().stream()
                    .collect(Collectors.groupingBy(PartnerStoresAll::getGroupId));

            groupGroup.entrySet().stream().map(item -> {

            });

        });*/
        compareDataList.forEach(item -> {
            context.collect(item);
        });
    }
}
