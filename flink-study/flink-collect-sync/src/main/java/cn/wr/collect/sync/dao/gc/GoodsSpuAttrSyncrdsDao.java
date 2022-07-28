package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.gc.GoodsSpuAttrSyncrds;
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

public class GoodsSpuAttrSyncrdsDao implements QueryLimitDao<GoodsSpuAttrSyncrds> {
    private static final long serialVersionUID = -5344135525758758978L;
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsSpuAttrSyncrdsDao.class);

    private static final String SELECT_SQL = "select `id`, `bar_code`, `attr_id`, `spu_id`, " +
            " (CASE `gmtCreated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtCreated` END) AS `gmtCreated`, " +
            " (CASE `gmtUpdated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtUpdated` END) AS `gmtUpdated`, " +
            " `goods_name` " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_goods_spu_attr_syncrds` " +
            " where `id` > ? " +
            " order by `id` asc limit ?;";

    @Override
    public List<GoodsSpuAttrSyncrds> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        if (null == connection) return Collections.emptyList();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(SELECT_SQL);
            ps.setLong(1, (long) params.get("id"));
            ps.setInt(2, pageSize);
            rs = ps.executeQuery();
            List<GoodsSpuAttrSyncrds> list = new ArrayList<>(pageSize);
            while (rs.next()) {
                list.add(new GoodsSpuAttrSyncrds().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("GoodsSpuAttrSyncrdsDao findLimit offset:{}, pageSize:{}, error:{}", offset, pageSize, e);
        }
        finally {
            MysqlUtils.close(null, ps, rs);
        }
        return Collections.emptyList();
    }
}
