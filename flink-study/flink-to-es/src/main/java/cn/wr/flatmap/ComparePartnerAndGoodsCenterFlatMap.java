package cn.wr.flatmap;

import cn.wr.model.*;
import cn.wr.utils.DataBasesUtil;
import cn.wr.utils.MongoDBUtil;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.Collector;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static cn.wr.constants.PropertiesConstants.MONGO_DATABASE_DBNAME;
import static cn.wr.constants.SqlConstants.SELECT_GOODS_CENTER_STOCK_GOODS;
import static cn.wr.constants.SqlConstants.SELECT_PARTNER_DBNAME;

/**
 * @author RWang
 * @Date 2022/8/8
 */

public class ComparePartnerAndGoodsCenterFlatMap extends RichFlatMapFunction<AbnormalStock, AbnormalStockData> {

    private static final long serialVersionUID = -6535760090361717352L;
    private static final Logger logger = LoggerFactory.getLogger(ComparePartnerAndGoodsCenterFlatMap.class);

    private static final String STORE_INTERNAL_ID = "store_internal_id";
    private static final String GOODS_INTERNAL_ID = "goods_internal_id";
    private static final String STOCK_SUFFIX = "_goods_stock";
    private ParameterTool parameterTool;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void flatMap(AbnormalStock value, Collector<AbnormalStockData> out) {


        // 查询polardb 商品中心 获取库存信息
        Connection goodsCenterPolarConnection = DataBasesUtil.getGoodsCenterPolarConnection(parameterTool);
        // 查询polardb 数据中心 获取dbName
        Connection dataCenterPolarConnection = DataBasesUtil.getDataCenterPolarConnection(parameterTool);
        // 查询mongodb 获取 连锁对接数据
        MongoClient mongoClient = MongoDBUtil.getMongoClient(parameterTool);
        PreparedStatement ps = null;
        try {
            // 基于连锁id 去partner 中查询出dbName
            if (Objects.nonNull(value)) {
                ps = dataCenterPolarConnection.prepareStatement(SELECT_PARTNER_DBNAME);
                ps.setLong(1, value.getMerchantId());
                ResultSet resultSet = ps.executeQuery();
                String dbName = null;
                while (resultSet.next()) {
                    dbName = resultSet.getString(1);
                }
                if (StringUtils.isNotBlank(dbName)) {
                    // 查询mongo数据
                    PartnerStockData mongoStockGoods = getMongoStockGoods(mongoClient, dbName, value);

                    // 查询商品中心polardb
                    GoodsCenterStockData polarDbStockGoods = getPolarDbStockGoods(goodsCenterPolarConnection, value);

                    // 对比两者的库存和上下架,发送存在不一致的数据
                    comparePartnerAndGoodsCenter(mongoStockGoods, polarDbStockGoods, out);
                }
            }
        } catch (Exception e) {
            logger.error("Connection Exception:{0}", e);
        } finally {
            DataBasesUtil.close(dataCenterPolarConnection, ps);
            MongoDBUtil.closeMongo(mongoClient);
        }

    }

    /**
     * 获取连锁mongo的库存数据
     */
    public PartnerStockData getMongoStockGoods(MongoClient mongoClient, String dbName, AbnormalStock abnormalStock) {

        MongoCollection<Document> collection = mongoClient
                .getDatabase(parameterTool.get(MONGO_DATABASE_DBNAME))
                .getCollection(dbName + STOCK_SUFFIX);

        Bson queryValue = Filters.and(Filters.eq(STORE_INTERNAL_ID, String.valueOf(abnormalStock.getStoreId())),
                Filters.eq(GOODS_INTERNAL_ID, abnormalStock.getInternalId()));
        FindIterable<Document> documents = collection.find(queryValue);
        ObjectMapper objectMapper = new ObjectMapper();
        for (Document document : documents) {

            PartnerStockData partnerStockData = objectMapper.convertValue(document, PartnerStockData.class);
            partnerStockData.setMerchantId(abnormalStock.getMerchantId());
            return partnerStockData;
        }
        return null;

    }

    /**
     * 获取商品中心polardb的库存数据
     */
    public GoodsCenterStockData getPolarDbStockGoods(Connection goodsCenterPolarConnection, AbnormalStock value) {

        PreparedStatement psGoodsCenter = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            psGoodsCenter = goodsCenterPolarConnection.prepareStatement(SELECT_GOODS_CENTER_STOCK_GOODS);
            psGoodsCenter.setString(1, value.getStockNo());
            ResultSet resultSet1 = psGoodsCenter.executeQuery();
            ResultSetMetaData metaData = resultSet1.getMetaData();
            int columnCount = metaData.getColumnCount();
            Map<String, Object> rowData = new HashMap<>();
            while (resultSet1.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(metaData.getColumnLabel(i), resultSet1.getObject(i));
                }
                GoodsCenterStockData goodsCenterStockData = objectMapper.convertValue(rowData, GoodsCenterStockData.class);
                return StringUtils.isBlank(goodsCenterStockData.getInternalId()) ? null : goodsCenterStockData;
            }
        } catch (Exception e) {
            logger.error("getPolarDbStockGoods method Exception:{0}", e);
            e.printStackTrace();
        } finally {
            DataBasesUtil.close(goodsCenterPolarConnection, psGoodsCenter);
        }
        return null;

    }

    /**
     * 对比连锁和 商品中心的 库存及上下架数据
     *
     * @param partnerStockData     partnerStockData
     * @param goodsCenterStockData goodsCenterStockData
     * @return
     */
    public void comparePartnerAndGoodsCenter(PartnerStockData partnerStockData,
                                             GoodsCenterStockData goodsCenterStockData,
                                             Collector<AbnormalStockData> out) {

        // 1. 如果连锁查询不到数据 ，但是商品中心存在数据 不处理
        if (Objects.isNull(partnerStockData) && Objects.nonNull(goodsCenterStockData)) {
            return;
        }
        // 2. 连锁查询到数据 商品中心差不到数据
        if (Objects.nonNull(partnerStockData) && Objects.isNull(goodsCenterStockData)) {
            // 重新推送库存 以及 上下架
            AbnormalStockData abnormalStockData = new AbnormalStockData();
            abnormalStockData.setMerchantId(partnerStockData.getMerchantId());
            abnormalStockData.setInternalId(partnerStockData.getGoodsInternalId());
            abnormalStockData.setStoreId(Long.parseLong(partnerStockData.getStoreInternalId()));
            abnormalStockData.setIsStockOrStatus(4);
            out.collect(abnormalStockData);
        }
        // 3. 商品中心 和 连锁不一致 封装转发下游
        if (Objects.nonNull(partnerStockData) & Objects.nonNull(goodsCenterStockData)) {

            AbnormalStockData abnormalStockData = new AbnormalStockData();
            abnormalStockData.setMerchantId(goodsCenterStockData.getMerchantId());
            abnormalStockData.setInternalId(goodsCenterStockData.getInternalId());
            abnormalStockData.setStoreId(goodsCenterStockData.getStoreId());
            // 存在库存精度不一致情况 需要统一转换
            String partnerQuantity = new BigDecimal(partnerStockData.getStockQuantity()).stripTrailingZeros().toPlainString();
            String goodsCenterQuantity = new BigDecimal(goodsCenterStockData.getQuantity()).stripTrailingZeros().toPlainString();
            // 3.1) 商品中心 库存和 连锁库存不一致 封装转发下游
            if (!partnerQuantity.equals(goodsCenterQuantity)) {
                abnormalStockData.setIsStockOrStatus(2);
                out.collect(abnormalStockData);
            }
            // 3.2) 商品中心 上下架和 连锁上下架不一致 封装转发下游
            if (!partnerStockData.getStatus().equals(goodsCenterStockData.getSaleState())) {
                abnormalStockData.setIsStockOrStatus(3);
                out.collect(abnormalStockData);
            }


        }


    }
}
