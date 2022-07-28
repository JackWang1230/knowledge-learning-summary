package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.gc.BaseNootc;
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

public class BaseNootcDao implements QueryLimitDao<BaseNootc> {
    private static final long serialVersionUID = 2502512185496389889L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseNootcDao.class);

    /*private static final String SELECT_SQL = "select `id`, `approval_number`, `name`, `otc_type`, `is_ephedrine`, `is_double`, " +
            "`gmtUpdated`, `gmtCreated` from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_base_nootc` " +
            " where `id` >= (select `id` from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_base_nootc` order by `id` asc limit ?, 1) " +
            " order by `id` asc limit ?;";*/

    private static final String SELECT_SQL = "select `id`, `approval_number`, `name`, `otc_type`, `is_ephedrine`, `is_double`, " +
            "`gmtUpdated`, `gmtCreated` from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_base_nootc` " +
            " where `id` > ? " +
            " order by `id` asc limit ?;";


    @Override
    public List<BaseNootc> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        // LOGGER.info("BaseNootcDao findLimit offset:{}, pageSize:{}", offset, pageSize);
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
            List<BaseNootc> list = new ArrayList<>(pageSize);
            while (rs.next()) {
                // `id`, `approval_number`, `name`, `otc_type`, `is_ephedrine`, `is_double`, `gmtUpdated`, `gmtCreated`
                BaseNootc item = new BaseNootc();
                item.setId(rs.getLong(1));
                item.setApprovalNumber(rs.getString(2));
                item.setName(rs.getString(3));
                item.setOtcType(rs.getInt(4));
                item.setIsEphedrine(rs.getInt(5));
                item.setIsDouble(rs.getInt(6));
                item.setGmtCreated(null != rs.getTimestamp(7) ? rs.getTimestamp(7).toLocalDateTime() : null);
                item.setGmtUpdated(null != rs.getTimestamp(8) ? rs.getTimestamp(8).toLocalDateTime() : null);
                list.add(item);
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("BaseNootcDao findLimit offset:{}, pageSize:{}, error:{}", offset, pageSize, e);
        }
        finally {
            // long end = System.currentTimeMillis();
            // LOGGER.error("BaseNootcDao findLimit offset:{}, pageSize:{}, time:{}(s)", offset, pageSize, (end - start)/1000);
            MysqlUtils.close(null, ps, rs);
        }

        return Collections.emptyList();
    }
}
