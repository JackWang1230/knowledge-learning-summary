package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.gc.GoodsAttrInfoSyncrds;
import cn.wr.collect.sync.utils.MysqlUtils;
import cn.wr.collect.sync.utils.ResultSetConvert;
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

public class GoodsAttrInfoSyncrdsDao implements QueryLimitDao<GoodsAttrInfoSyncrds> {
    private static final long serialVersionUID = -4090472106423676136L;
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsAttrInfoSyncrdsDao.class);

    /*private static final String SELECT_SQL = "select `id`, `title`, `key_word`, `parent_id`, `pids`, `level`, `remarks`, " +
            " `allow`, `deleted`, `gmt_updated`, `gmt_created`, `creator`, `updator`, `cate_id` " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_goods_attr_info_syncrds` " +
            " where `id` >= (select `id` from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_goods_attr_info_syncrds` order by `id` asc limit ?, 1) " +
            " order by `id` asc limit ?;";*/

    private static final String SELECT_SQL = "select `id`, `title`, `key_word`, `parent_id`, `pids`, `level`, `remarks`, " +
            " `allow`, `deleted`, `gmt_updated`, `gmt_created`, `creator`, `updator`, `cate_id` " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_goods_attr_info_syncrds` " +
            " where `id` > ? " +
            " order by `id` asc limit ?;";

    @Override
    public List<GoodsAttrInfoSyncrds> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        // LOGGER.info("GoodsAttrInfoSyncrds findLimit offset:{}, pageSize:{}", offset, pageSize);
        // long start = System.currentTimeMillis();

        //创建连接
        if (null == connection) return Collections.emptyList();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(SELECT_SQL);
            // ps.setLong(1, offset);
            ps.setLong(1, (long) params.get("id"));
            ps.setInt(2, pageSize);
            rs = ps.executeQuery();
            List<GoodsAttrInfoSyncrds> list = new ArrayList<>(pageSize);
            while (rs.next()) {
                // `id`, `title`, `key_word`, `parent_id`, `pids`, `level`, `remarks`, `allow`, `deleted`, `gmt_updated`,
                // `gmt_created`, `creator`, `updator`, `cate_id`
                GoodsAttrInfoSyncrds item = new GoodsAttrInfoSyncrds();
                item.setId(ResultSetConvert.getLong(rs, 1));
                item.setTitle(ResultSetConvert.getString(rs, 2));
                item.setKeyWord(ResultSetConvert.getString(rs, 3));
                item.setParentId(ResultSetConvert.getLong(rs, 4));
                item.setPids(ResultSetConvert.getString(rs, 5));
                item.setLevel(ResultSetConvert.getInt(rs, 6));
                item.setRemarks(ResultSetConvert.getString(rs, 7));
                item.setAllow(ResultSetConvert.getInt(rs, 8));
                item.setDeleted(ResultSetConvert.getInt(rs, 9));
                item.setGmtCreated(ResultSetConvert.getLocalDateTime(rs, 10));
                item.setGmtUpdated(ResultSetConvert.getLocalDateTime(rs, 11));
                item.setCreator(ResultSetConvert.getString(rs, 12));
                item.setUpdator(ResultSetConvert.getString(rs, 13));
                item.setCateId(ResultSetConvert.getString(rs, 14));
                list.add(item);
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("GoodsAttrInfoSyncrdsDao findLimit offset:{}, pageSize:{}, error:{}", offset, pageSize, e);
        }
        finally {
            MysqlUtils.close(null, ps, rs);
        }
        return Collections.emptyList();
    }
}
