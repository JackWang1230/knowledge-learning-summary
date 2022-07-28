package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.gc.PartnerGoodsImg;
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

public class PartnerGoodsImgDao implements QueryLimitDao<PartnerGoodsImg> {
    private static final long serialVersionUID = -8374903401977678584L;
    private static final Logger LOGGER = LoggerFactory.getLogger(PartnerGoodsImgDao.class);


    private static final String SELECT_SQL = "select `id`, `merchant_id`, `db_id`, `internal_id`, `img`, `gmtCreated`, `gmtUpdated` " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`partner_goods_img` " +
            " where `id` > ? " +
            " order by `id` asc limit ?;";

    @Override
    public List<PartnerGoodsImg> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        // LOGGER.info("PartnerGoodsImgDao findLimit offset:{}, pageSize:{}", offset, pageSize);
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
            List<PartnerGoodsImg> list = new ArrayList<>(pageSize);
            while (rs.next()) {
                // `id`, `merchant_id`, `db_id`, `internal_id`, `img`, `gmtCreated`, `gmtUpdated`
                PartnerGoodsImg item = new PartnerGoodsImg();
                item.setId(rs.getLong(1));
                item.setMerchantId(rs.getInt(2));
                item.setDbId(rs.getInt(3));
                item.setInternalId(rs.getString(4));
                item.setImg(rs.getString(5));
                item.setGmtCreated(null != rs.getTimestamp(6) ? rs.getTimestamp(6).toLocalDateTime() : null);
                item.setGmtUpdated(null != rs.getTimestamp(7) ? rs.getTimestamp(7).toLocalDateTime() : null);
                list.add(item);
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("PartnerGoodsImgDao findLimit offset:{}, pageSize:{}, error:{}", offset, pageSize, e);
        }
        finally {
            // long end = System.currentTimeMillis();
            // LOGGER.error("PartnerGoodsImgDao findLimit offset:{}, pageSize:{}, time:{}(s)", offset, pageSize, (end - start)/1000);
            MysqlUtils.close(null, ps, rs);
        }
        return Collections.emptyList();
    }
}
