package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.gc.PartnerGoodsGift;
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

public class PartnerGoodsGiftDao implements QueryLimitDao<PartnerGoodsGift> {
    private static final long serialVersionUID = -980669532025187155L;
    private static final Logger LOGGER = LoggerFactory.getLogger(PartnerGoodsGiftDao.class);

    /*private static final String SELECT_SQL = "select `id`, `db_id`, `internal_id`, `gmtCreated`, `gmtUpdated` " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_partner_goods_gift` " +
            " where `id` >= (select `id` from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_partner_goods_gift` order by `id` asc limit ?, 1) " +
            " order by `id` asc limit ?;";*/
    private static final String SELECT_SQL = "select `id`, `db_id`, `internal_id`, `gmtCreated`, `gmtUpdated` " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_partner_goods_gift` " +
            " where `id` > ? " +
            " order by `id` asc limit ?;";

    @Override
    public List<PartnerGoodsGift> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        // LOGGER.info("PartnerGoodsGiftDao findLimit offset:{}, pageSize:{}", offset, pageSize);
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
            List<PartnerGoodsGift> list = new ArrayList<>(pageSize);
            while (rs.next()) {
                // `id`, `db_id`, `internal_id`, `gmtCreated`, `gmtUpdated`
                PartnerGoodsGift item = new PartnerGoodsGift();
                item.setId(rs.getLong(1));
                item.setDbId(rs.getInt(2));
                item.setInternalId(rs.getString(3));
                item.setGmtCreated(null != rs.getTimestamp(4) ? rs.getTimestamp(4).toLocalDateTime() : null);
                item.setGmtUpdated(null != rs.getTimestamp(5) ? rs.getTimestamp(5).toLocalDateTime() : null);
                list.add(item);
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("PartnerGoodsGiftDao findLimit offset:{}, pageSize:{}, error:{}", offset, pageSize, e);
        }
        finally {
            // long end = System.currentTimeMillis();
            // LOGGER.error("PartnerGoodsGiftDao findLimit offset:{}, pageSize:{}, time:{}(s)", offset, pageSize, (end - start)/1000);
            MysqlUtils.close(null, ps, rs);
        }
        return Collections.emptyList();
    }
}
