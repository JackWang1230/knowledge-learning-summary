package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.gc.BaseSpuImg;
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

public class BaseSpuImgDao implements QueryLimitDao<BaseSpuImg> {
    private static final long serialVersionUID = 1479664544634277689L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseSpuImgDao.class);

    /*private static final String SELECT_SQL = "select `id`, `name`, `approval_number`, `pic`, `gmtUpdated`, `gmtCreated` " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_base_spu_img` " +
            " where `id` >= (select `id` from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_base_spu_img` order by `id` asc limit ?, 1) " +
            " order by `id` asc limit ?;";*/
    private static final String SELECT_SQL = "select `id`, `name`, `approval_number`, `pic`, `gmtUpdated`, `gmtCreated` " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_base_spu_img` " +
            " where `id` > ? " +
            " order by `id` asc limit ?;";


    @Override
    public List<BaseSpuImg> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        // LOGGER.info("BaseSpuImgDao findLimit offset:{}, pageSize:{}", offset, pageSize);
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
            List<BaseSpuImg> list = new ArrayList<>(pageSize);
            while (rs.next()) {
                // `id`, `name`, `approval_number`, `pic`, `gmtUpdated`, `gmtCreated`
                BaseSpuImg item = new BaseSpuImg();
                item.setId(rs.getLong(1));
                item.setName(rs.getString(2));
                item.setApprovalNumber(rs.getString(3));
                item.setPic(rs.getString(4));
                item.setGmtUpdated(rs.getTimestamp(5).toLocalDateTime());
                item.setGmtCreated(rs.getTimestamp(6).toLocalDateTime());
                list.add(item);
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("BaseSpuImgDao findLimit offset:{}, pageSize:{}, error:{}", offset, pageSize, e);
        }
        finally {
            // long end = System.currentTimeMillis();
            // LOGGER.error("BaseSpuImgDao findLimit offset:{}, pageSize:{}, time:{}(s)", offset, pageSize, (end - start)/1000);
            MysqlUtils.close(null, ps, rs);
        }
        return Collections.emptyList();
    }
}
