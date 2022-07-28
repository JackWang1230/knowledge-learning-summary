package cn.wr.collect.sync.datasource;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.dao.pcommon.PartnerDAO;
import cn.wr.collect.sync.dao.pcommon.PriceDAO;
import cn.wr.collect.sync.dao.price.PriceCompareDAO;
import cn.wr.collect.sync.dao.price.PriceListDetailsDAO;
import cn.wr.collect.sync.dao.price.PriceStoreDAO;
import cn.wr.collect.sync.model.price.*;
import cn.wr.collect.sync.model.pricecompare.Partner;
import cn.wr.collect.sync.model.pricecompare.Price;
import cn.wr.collect.sync.model.pricecompare.PriceCompare;
import cn.wr.collect.sync.utils.QueryUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.PropertiesConstants.COMPARE_PRICE_MERCHANT_ID;


public class GoodsPriceCompareSource extends RichSourceFunction<List<PriceCompare>> {
    private static final long serialVersionUID = -3792999152267631133L;
    private static final Logger log = LoggerFactory.getLogger(GoodsPriceCompareSource.class);
    private ParameterTool parameterTool;
    private PriceStoreDAO priceStoreDAO;
    private PriceCompareDAO priceCompareDAO;
    private PartnerDAO partnerDAO;


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        priceStoreDAO = new PriceStoreDAO(parameterTool);
        priceCompareDAO = new PriceCompareDAO(parameterTool);
        partnerDAO = new PartnerDAO(parameterTool);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void cancel() {

    }

    @Override
    public void run(SourceContext<List<PriceCompare>> context) throws Exception {
        String merchantIdStr = parameterTool.get(COMPARE_PRICE_MERCHANT_ID);

        if (StringUtils.isNotBlank(merchantIdStr)) {

            String[] merchantIds = merchantIdStr.split(",");
            List<PriceStore> priceStores = priceStoreDAO.queryListForInit("merchantId", merchantIds);

            log.info("连锁id：{}, 查询的门店总数：{}", merchantIds, priceStores.size());

            if (priceStores.size() > 0) {
                //先清空原来比较的数据
                priceCompareDAO.delete(merchantIds);

                priceStores.stream().forEach(priceStore -> {
                    long start = System.currentTimeMillis();

                    Long merchantId = priceStore.getOrganizationId();
                    Long storeId = priceStore.getStoreId();
                    if (Objects.isNull(merchantId) || Objects.isNull(storeId)) {
                        return;
                    }
                    //获取对应连锁库的信息
                    Map<String, Object> p = new HashMap<>();
                    p.put("merchantId", merchantId);
                    p.put("storeId", storeId);
                    Partner partner = partnerDAO.queryOne(p);

                    if (Objects.isNull(partner) || StringUtils.isBlank(partner.getGroupId())
                    || StringUtils.isBlank(partner.getDbName())) {
                        return;
                    }
                    String dbName = partner.getDbName();
                    if (!dbName.startsWith("partner_common_")) {
                        return;
                    }

                    //获取连锁库里面的商品数据
                    //查出所有的连锁商品
                    Map<String, Object> partnerParams = new HashMap<>();
                    int partnerPage = 0;
                    Long partnerId = 0L;
                    List<Price> partnerPriceList = new ArrayList<>();
                    List<Price> limitPlus;
                    QueryLimitDao partnerQueryDAO = new PriceDAO(parameterTool);

                    while (true) {
                        partnerParams.put("dbName", dbName);
                        partnerParams.put("groupId", partner.getGroupId());
                        partnerParams.put("id", partnerId);

                        limitPlus = QueryUtil.findLimitPlus(partnerQueryDAO, partnerPage, partnerParams, null);

                        if (CollectionUtils.isEmpty(limitPlus)) {
                            break;
                        }

                        partnerPriceList.addAll(limitPlus);

                        if (limitPlus.size() < QueryUtil.QUERY_PAGE_SIZE) {
                            break;
                        }
                        partnerId = limitPlus.get(limitPlus.size() - 1).getId();

                        partnerPage++;
                    }

                    log.info("连锁id：{}, 门店id：{}，从连锁库查询的商品价格总数：{}", merchantId, storeId, partnerPriceList.size());

                    Map<String,Price> partnerPriceMap = partnerPriceList.stream()
                            .collect(Collectors.toMap(Price::getUniqueKey, Function.identity()));

                    Map<String, Object> mapParams = new HashMap<>();

                    int page = 0;
                    Long id = 0L;
                    List<PriceListDetails> modelList;
                    List<PriceCompare> priceCompares = new ArrayList<>();
                    List<PriceListDetails> priceListDetailsList = new ArrayList<>();
                    QueryLimitDao queryDAO = new PriceListDetailsDAO(parameterTool);
                    while (true) {
                        mapParams.put("organizationId", priceStore.getOrganizationId());
                        mapParams.put("listId", priceStore.getListId());
                        mapParams.put("id", id);

                        // 从数据库查询数据,一次查1w条
                        modelList = QueryUtil.findLimitPlus(queryDAO, page, mapParams, null);
                        if (CollectionUtils.isEmpty(modelList)) {
                            break;
                        }

                        priceListDetailsList.addAll(modelList);

                        if (modelList.size() < QueryUtil.QUERY_PAGE_SIZE) {
                            break;
                        }
                        id = modelList.get(modelList.size() - 1).getId();
                        page++;
                    }

                    log.info("连锁id：{}, 门店id：{}，从价格中心查询的商品价格总数：{}", merchantId, storeId, priceListDetailsList.size());

                    priceListDetailsList.forEach(model -> {
                        PriceListDetails priceListDetails = (PriceListDetails) model;

                        if (StringUtils.isNotBlank(priceListDetails.getInternalId())) {

                            if (partnerPriceMap.containsKey(partner.getGroupId()+"-"+priceListDetails.getInternalId())) {

                                Price price = partnerPriceMap.get(partner.getGroupId() + "-" + priceListDetails.getInternalId());

                                if (!(priceListDetails.getSkuPrice().compareTo(price.getSalePrice()) == 0
                                        && priceListDetails.getOriginalPrice().compareTo(price.getBasePrice()) == 0)) {

                                    PriceCompare priceCompare = new PriceCompare();
                                    priceCompare.setMerchantId(merchantId);
                                    priceCompare.setStoreId(storeId);
                                    priceCompare.setInternalId(priceListDetails.getInternalId());
                                    priceCompare.setPriceSalePrice(priceListDetails.getSkuPrice());
                                    priceCompare.setPriceBasePrice(priceListDetails.getOriginalPrice());
                                    priceCompare.setPartnerSalePrice(price.getSalePrice());
                                    priceCompare.setPartnerBasePrice(price.getBasePrice());

                                    priceCompares.add(priceCompare);

                                }
                            } else {
                                // 在连锁库中不存在的
                                PriceCompare priceCompare = new PriceCompare();
                                priceCompare.setMerchantId(merchantId);
                                priceCompare.setStoreId(storeId);
                                priceCompare.setInternalId(priceListDetails.getInternalId());
                                priceCompare.setPriceSalePrice(priceListDetails.getSkuPrice());
                                priceCompare.setPriceBasePrice(priceListDetails.getOriginalPrice());

                                priceCompares.add(priceCompare);
                            }
                        }
                    });

                    log.info("数据总量：{}, 比对用时：" + (System.currentTimeMillis() - start)+"【单位：毫秒】，比对出结果：{}", priceListDetailsList.size(), priceCompares.size());

                    context.collect(priceCompares);
                });


            }
        }
    }
}
