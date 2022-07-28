package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.dao.price.PriceStoreDAO;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.price.PriceStore;
import cn.wr.collect.sync.utils.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static cn.wr.collect.sync.constants.PropertiesConstants.INIT_PRICE_MERCHANT_ID;
import static cn.wr.collect.sync.constants.PropertiesConstants.INIT_PRICE_STORE_ID;


public class GoodsPriceInitSource extends RichSourceFunction<BasicModel<Model>> {
    private static final long serialVersionUID = -3792999152267631133L;
    private static final Logger log = LoggerFactory.getLogger(GoodsPriceInitSource.class);
    private ParameterTool parameterTool;
    private PriceStoreDAO priceStoreDAO;


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        priceStoreDAO = new PriceStoreDAO(parameterTool);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void cancel() {

    }

    @Override
    public void run(SourceContext<BasicModel<Model>> context) throws Exception {
        String merchantIdStr = parameterTool.get(INIT_PRICE_MERCHANT_ID);
        String storeIdStr = parameterTool.get(INIT_PRICE_STORE_ID);

        Map<Long, PriceStore> priceStoreMap = new HashMap<>();

        if (StringUtils.isNotBlank(merchantIdStr)) {

            String[] merchantIds = merchantIdStr.split(",");
            List<PriceStore> priceStores = priceStoreDAO.queryListForInit("merchantId", merchantIds);

            if (priceStores.size() > 0) {
                priceStores.forEach(priceStore -> {if (!priceStoreMap.containsKey(priceStore.getId())) { priceStoreMap.put(priceStore.getId(), priceStore); }});
            }
        }

        if (StringUtils.isNotBlank(storeIdStr)) {
            String[] storeIds = storeIdStr.split(",");


            List<PriceStore> priceStores = priceStoreDAO.queryListForInit("storeId", storeIds);

            if (priceStores.size() > 0) {
                if (priceStores.size() > 0) {
                    priceStores.forEach(priceStore -> {if (!priceStoreMap.containsKey(priceStore.getId())) { priceStoreMap.put(priceStore.getId(), priceStore); }});
                }
            }
        }

        Class<? extends Model> clazz = Table.BaseDataTable.getClazz(Table.BaseDataTable.price_store.name());
        if (Objects.isNull(clazz)) {
            return;
        }

        if (!priceStoreMap.isEmpty()) {
            Set<Long> keys = priceStoreMap.keySet();
            for (Long k: keys) {
                PriceStore priceStore = priceStoreMap.get(k);
                Map<String,Object> data = JSONObject.parseObject(JSON.toJSONString(priceStore));
                context.collect(ReflectUtil.reflectData(Table.BaseDataTable.price_store.name(), CommonConstants.OPERATE_INSERT, data,
                        null, clazz));
            }
        }
    }
}
