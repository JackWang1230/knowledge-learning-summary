package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.gc.GoodsSalesStatisticsMerchant;
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

import static cn.wr.collect.sync.constants.CommonConstants.*;
import static cn.wr.collect.sync.constants.CommonConstants.JDBC_EXCEPTION_01;
import static cn.wr.collect.sync.constants.CommonConstants.JDBC_EXCEPTION_02;
import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_GOODS_CENTER_TIDB;
import static cn.wr.collect.sync.constants.PropertiesConstants.*;

public class GoodsSalesStatisticsMerchantDao implements QueryLimitDao<GoodsSalesStatisticsMerchant> {
    private static final long serialVersionUID = -7525663001735215691L;
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsSalesStatisticsMerchantDao.class);

    private static final String SELECT_SQL = "select `id`, `merchant_id`, `internal_id`, `item_type`, `quantity`, " +
            " `gmtcreated`, `gmtupdated`, `deleted` " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_goods_sales_statistics_merchant` " +
            " where `id` > ? " +
            " order by `id` asc limit ?;";

    @Override
    public List<GoodsSalesStatisticsMerchant> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        try {
            return queryList(offset, pageSize, params, connection);
        }
        catch (Exception e) {
            // com.mysql.jdbc.exceptions.jdbc4.CommunicationsException: Communications link failure
            LOGGER.error("### GoodsSalesStatisticsMerchantDao Exception:{}", e);
            if (e.getMessage().contains(JDBC_EXCEPTION_01) || e.getMessage().contains(JDBC_EXCEPTION_02)) {
                connection = MysqlUtils.resetConnection();
                if (null != connection) {
                    try {
                        return queryList(offset, pageSize, params, connection);
                    }
                    catch (Exception e1) {
                        LOGGER.error("### GoodsSalesStatisticsMerchantDao Communications link failure error:{}", e);
                    }
                }
                else {
                    LOGGER.error("### GoodsSalesStatisticsMerchantDao findLimit offset:{}, pageSize:{}, error:{}", offset, pageSize, e);
                }
            }
        }
        return Collections.emptyList();
    }

    private List<GoodsSalesStatisticsMerchant> queryList(long offset, int pageSize, Map<String, Object> params, Connection connection) throws Exception {
        // LOGGER.info("GoodsSalesStatisticsMerchantDao findLimit offset:{}, pageSize:{}", offset, pageSize);
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
            List<GoodsSalesStatisticsMerchant> list = new ArrayList<>(pageSize);
            while (rs.next()) {
                // `id`, `merchant_id`, `internal_id`, `item_type`, `quantity`, `gmtcreated`, `gmtupdated`, `deleted`
                GoodsSalesStatisticsMerchant item = new GoodsSalesStatisticsMerchant();
                item.setId(rs.getLong(1));
                item.setMerchantId(rs.getLong(2));
                item.setInternalId(rs.getString(3));
                item.setItemType(rs.getInt(4));
                item.setQuantity(rs.getDouble(5));
                item.setGmtcreated(null != rs.getTimestamp(6) ? rs.getTimestamp(6).toLocalDateTime() : null);
                item.setGmtupdated(null != rs.getTimestamp(7) ? rs.getTimestamp(7).toLocalDateTime() : null);
                item.setDeleted(rs.getInt(8));
                list.add(item);
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("GoodsSalesStatisticsMerchantDao findLimit offset:{}, pageSize:{}, error:{}", offset, pageSize, e);
            throw new Exception(e);
        }
        finally {
            // long end = System.currentTimeMillis();
            // LOGGER.error("GoodsSalesStatisticsMerchantDao findLimit offset:{}, pageSize:{}, time:{}(s)", offset, pageSize, (end - start)/1000);
            MysqlUtils.close(null, ps, rs);
        }
    }
}
