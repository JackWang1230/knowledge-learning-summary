package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.gc.PartnerStoresAll;
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

public class PartnerStoresAllDao implements QueryLimitDao<PartnerStoresAll> {
    private static final long serialVersionUID = -2390623817884548307L;
    private static final Logger LOGGER = LoggerFactory.getLogger(PartnerStoresAllDao.class);

    private static final String SELECT_SQL = "select `id`, `table_id`, `db_id`, `merchant_id`, `store_id`, `merchant_name`, " +
            " `internal_id`, `group_id`, `store_name`, `longitude`, `latitude`, `address`, `location`, " +
            " (CASE `created_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `created_at` END) AS `created_at`, " +
            " (CASE `update_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `update_at` END) AS `update_at`, " +
            " `gmtcreated`, `gmtupdated`, `channel`, `last_date`, `last_pg_date`, `store_code`, `out_id`, " +
            " `hash_seven`, `baidu_online` from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_partner_stores_all`";
    /*private static final String WHERE_SQL = " where update_at >= ? ";
    private static final String LIMIT_SQL = " limit ?,?;";*/

    @Override
    public List<PartnerStoresAll> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        // LOGGER.info("### PartnerStoresAllDao findLimit offset:{}, pageSize:{}", offset, pageSize);
        // long start = System.currentTimeMillis();

        //创建连接
        if (null == connection) return Collections.emptyList();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(SELECT_SQL);
            /*if (null != params) {
                ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL + LIMIT_SQL);
                ps.setObject(1, params.get("startTime"));
                // ps.setObject(2, params.get("updateAt"));
                ps.setLong(2, offset);
                ps.setInt(3, pageSize);
            }
            else {
                ps = connection.prepareStatement(SELECT_SQL + LIMIT_SQL);
                ps.setLong(1, offset);
                ps.setInt(2, pageSize);
            }*/

            rs = ps.executeQuery();
            List<PartnerStoresAll> list = new ArrayList<>(pageSize);
            while (rs.next()) {
                list.add(new PartnerStoresAll().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("PartnerStoresAllDao findLimit offset:{}, pageSize:{}, params:{}, error:{}", offset, pageSize, params, e);
        }
        finally {
            // long end = System.currentTimeMillis();
            LOGGER.info("PartnerStoresAllDao findLimit offset:{}, pageSize:{}, params:{}", offset, pageSize, params);
            MysqlUtils.close(null, ps, rs);
        }
        return Collections.emptyList();
    }


}
