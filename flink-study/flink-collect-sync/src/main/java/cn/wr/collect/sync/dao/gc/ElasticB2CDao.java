package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.MetricItem;
import cn.wr.collect.sync.model.SplitMiddleData;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.wr.collect.sync.constants.CommonConstants.*;

public class ElasticB2CDao implements QueryLimitDao<MetricItem<SplitMiddleData>> {
    private static final long serialVersionUID = 8572657859445243856L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticB2CDao.class);

    private static final String SELECT_SQL = "SELECT " +
            /*"NULL AS goods_no, " +
            "NULL AS sync_date, " +*/
            "CONCAT(psa.merchant_id, '-', psa.store_id, '-', psa.channel, '-', pg.internal_id) sku_code, " +
            "pg.approval_number, " +
            "pg.price AS sale_price, " +
            "pg.price AS base_price, " +
            "pg.internal_id internal_id, " +
            // "NULL AS state, " +
            "pg.trade_code, " +
            "pg.common_name, " +
            "pg.form, " +
            "1 AS goods_type, " +
            "psa.channel, " +
            // "IF(j.id IS NULL, IF( pg.`status` = 1 , '0', '1' ), '1' ) is_off_shelf, " +
            "psa.merchant_id, " +
            "psa.store_id, " +
            "psa.location, " +
            "pg.manufacturer, " +
            "psa.hash_seven AS geohash,  " +
            "psa.db_id, " +
            "psa.group_id, " +
            "IF( pg.`status` = 1 , '0', '1' ) AS is_off_shelf " +
            "FROM " + SCHEMA_GOODS_CENTER_TIDB + ".gc_partner_stores_all psa " +
            "INNER JOIN " + SCHEMA_UNION_DRUG_PARTNER + ".partner_goods pg ON psa.db_id = pg.db_id " +
            /*"LEFT JOIN test.partner_goods_info b ON pg.internal_id = b.internal_id " +
            "LEFT JOIN gc_goods_sales_statistics_merchant c ON psa.merchant_id = c.merchant_id AND pg.internal_id = c.internal_id " +
            "LEFT JOIN gc_base_nootc g ON pg.approval_number = g.approval_number " +
            "LEFT JOIN gc_partner_goods_gift j ON j.db_id = psa.db_id AND pg.internal_id = j.internal_id " +
            "LEFT JOIN parnter_goods_search_priority l ON psa.db_id = l.db_id AND psa.merchant_id = l.merchant_id AND pg.internal_id = l.internal_id " +*/
            "WHERE psa.merchant_id = ? " +
            "AND psa.store_id = ? " +
            /*"AND ((psa.gmtupdated >= ?  AND psa.gmtupdated <= ? )  " +
            "OR (b.gmtUpdated >= ?  AND b.gmtupdated <= ? ) " +
            "OR (c.gmtupdated >= ?  AND c.gmtupdated <= ? ) " +
            "OR (g.gmtUpdated >= ?  AND g.gmtupdated <= ? ) " +
            "OR (j.gmtUpdated >= ?  AND j.gmtupdated <= ? )) " +*/
            "LIMIT ?,?";


    @Override
    public List<MetricItem<SplitMiddleData>> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        // long start = System.currentTimeMillis();
        /*long start = System.currentTimeMillis();
        LOGGER.info("ElasticB2CDao findLimit offset:{}, pageSize:{}, params:{}", offset, pageSize, params);*/

        //创建连接
        /*Connection connection = MysqlUtils.getConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));*/

        if (null == connection) return Collections.emptyList();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(SELECT_SQL);
            ps.setInt(1, (Integer) params.get("merchantId"));
            ps.setInt(2, (Integer) params.get("storeId"));
            ps.setLong(3, offset);
            ps.setInt(4, pageSize);

            /*LOGGER.info("ElasticB2CDao offset:{} sql:{}", offset, ps.toString());*/
            rs = ps.executeQuery();

            /*long end = System.currentTimeMillis();
            LOGGER.info("ElasticB2CDao findLimit offset:{} query complete(s):{}", offset, end - start);*/

            List<MetricItem<SplitMiddleData>> list = new ArrayList<>();
            while (rs.next()) {
                SplitMiddleData data = new SplitMiddleData();
                /*data.setGoodsNo(rs.getString(1));
                data.setSyncDate(null != rs.getTimestamp(2) ? rs.getTimestamp(2).toLocalDateTime() : null);*/
                data.setSyncDate(LocalDateTime.now());
                data.setSkuCode(rs.getString(1));
                data.setApprovalNumber(rs.getString(2));
                data.setSalePrice(rs.getBigDecimal(3));
                data.setBasePrice(rs.getBigDecimal(4));
                data.setInternalId(rs.getString(5));
                // data.setState(rs.getString(8));
                data.setTradeCode(rs.getString(6));
                data.setCommonName(rs.getString(7));
                data.setForm(rs.getString(8));
                data.setGoodsType(rs.getInt(9));
                data.setChannel(rs.getString(10));
                // data.setIsOffShelf(rs.getString(14));
                data.setMerchantId(rs.getInt(11));
                data.setStoreId(rs.getInt(12));
                data.setLocation(rs.getString(13));
                data.setManufacturer(rs.getString(14));
                /*data.setIsPrescription(rs.getString(19));
                data.setIsEphedrine(rs.getString(20));
                data.setIsDouble(rs.getString(21));
                data.setCategoryOne(rs.getString(22));
                data.setCategoryTwo(rs.getString(23));
                data.setCategoryThree(rs.getString(24));
                data.setCategoryFour(rs.getString(25));
                data.setCategoryFive(rs.getString(26));
                data.setIsStandard(rs.getString(27));
                data.setIndications(rs.getString(28));
                data.setImg(rs.getString(29));
                data.setSalesVolume(rs.getBigDecimal(30));
                data.setPriority(rs.getInt(31));*/

                // 冗余字段
                data.setGeohash(rs.getString(15));
                data.setDbId(rs.getInt(16));
                data.setGroupId(rs.getString(17));
                data.setIsOffShelfSsg(rs.getString(18));

                /*data.setEnName(rs.getString(25));
                data.setPinyinName(rs.getString(26));
                data.setCureDisease(rs.getString(28));
                data.setPediatricUse(rs.getString(29));
                data.setGeriatricUse(rs.getString(30));
                data.setPregnancyAndNursingMothers(rs.getString(31));
                data.setOverDosage(rs.getString(32));
                data.setDrugName(rs.getString(33));
                data.setRelativeSickness(rs.getString(34));
                data.setDrugType(rs.getString(35));*/

                MetricItem<SplitMiddleData> item = new MetricItem<>();
                item.setItem(data);
                item.setTableName(Table.BaseDataTable.gc_partner_stores_all.name());
                item.setOperate(OPERATE_UPDATE);
                list.add(item);
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("ElasticB2CDao findLimit offset:{}, pageSize:{}, error:{}", offset, pageSize, e);
        }
        finally {
            // long end = System.currentTimeMillis();
            // LOGGER.error("ElasticB2CDao findLimit offset:{}, pageSize:{}, time:{}(s)", offset, pageSize, (end - start)/1000);
            MysqlUtils.close(null, ps, rs);
        }
        return Collections.emptyList();
    }
}
