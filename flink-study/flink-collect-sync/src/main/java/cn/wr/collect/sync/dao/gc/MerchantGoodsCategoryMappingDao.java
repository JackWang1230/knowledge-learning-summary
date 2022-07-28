package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.gc.MerchantGoodsCategoryMapping;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_GOODS_CENTER_TIDB;
import static cn.wr.collect.sync.constants.PropertiesConstants.*;

public class MerchantGoodsCategoryMappingDao implements QueryLimitDao<MerchantGoodsCategoryMapping> {
    private static final long serialVersionUID = -1815165569766885537L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseNootcDao.class);

    /*private static final String SELECT_SQL = "select `id`, `internal_id`, `common_name`, `trade_code`, " +
            " `prescription_category`, `merchant_category`, `mhj`, `category_code`, `merchant_id` " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`merchant_goods_category_mapping` " +
            " where `id` >= (select `id` from " + SCHEMA_GOODS_CENTER_TIDB + ".`merchant_goods_category_mapping` order by `id` asc limit ?, 1) " +
            " order by `id` asc limit ?;";*/

    private static final String SELECT_SQL = "select `id`, `internal_id`, `common_name`, `trade_code`, " +
            " `prescription_category`, `merchant_category`, `mhj`, `category_code`, `merchant_id` " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`merchant_goods_category_mapping` " +
            " where `id` > ? " +
            " order by `id` asc limit ?;";

    @Override
    public List<MerchantGoodsCategoryMapping> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        // LOGGER.info("MerchantGoodsCategoryMappingDao findLimit offset:{}, pageSize:{}", offset, pageSize);
        // long start = System.currentTimeMillis();

        //创建连接
        /*Connection connection = MysqlUtils.getConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));*/
        if (null == connection) return Collections.emptyList();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(SELECT_SQL);
            // ps.setLong(1, offset);
            ps.setLong(1, (long) params.get("id"));
            ps.setInt(2, pageSize);
            rs = ps.executeQuery();
            List<MerchantGoodsCategoryMapping> list = new ArrayList<>(pageSize);
            while (rs.next()) {
                // `id`, `internal_id`, `common_name`, `trade_code`, `prescription_category`, `merchant_category`, `mhj`,
                // `category_code`, `merchant_id`
                MerchantGoodsCategoryMapping item = new MerchantGoodsCategoryMapping();
                item.setId(rs.getLong(1));
                item.setInternalId(rs.getString(2));
                item.setCommonName(rs.getString(3));
                item.setTradeCode(rs.getString(4));
                item.setPrescriptionCategory(rs.getString(5));
                item.setMerchantCategory(rs.getString(6));
                item.setMhj(rs.getString(7));
                item.setCategoryCode(rs.getString(8));
                item.setMerchantId(rs.getLong(9));
                list.add(item);
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("MerchantGoodsCategoryMappingDao findLimit offset:{}, pageSize:{}, error:{}", offset, pageSize, e);
        }
        finally {
            // long end = System.currentTimeMillis();
            // LOGGER.error("MerchantGoodsCategoryMappingDao findLimit offset:{}, pageSize:{}, time:{}(s)", offset, pageSize, (end - start)/1000);
            MysqlUtils.close(null, ps, rs);
        }
        return Collections.emptyList();
    }
}
