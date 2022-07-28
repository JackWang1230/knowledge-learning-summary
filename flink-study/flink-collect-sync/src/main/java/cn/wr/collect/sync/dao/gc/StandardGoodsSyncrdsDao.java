package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.model.gc.StandardGoodsSyncrds;
import cn.wr.collect.sync.utils.MysqlUtils;
import cn.wr.collect.sync.utils.ResultSetConvert;
import org.apache.flink.api.java.utils.ParameterTool;
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

public class StandardGoodsSyncrdsDao implements QueryLimitDao<StandardGoodsSyncrds> {
    private static final long serialVersionUID = -7920470969953185108L;
    private static final Logger LOGGER = LoggerFactory.getLogger(StandardGoodsSyncrdsDao.class);

    private static final String SELECT_SQL = "select `id`, `spu_id`, `goods_name`, `trade_code`, `specification`, `retail_price`," +
            " `gross_weight`, `origin_place`, `brand`, `category_id`, `search_keywords`, `search_association_words`, " +
            " `sub_title`, `highlights`, `details_code`, `urls`, `status`, `gmtCreated`, `gmtUpdated` " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_standard_goods_syncrds` " +
            " where `id` > ? " +
            " order by `id` asc limit ?;";

    private static final String SELECT_SQL_01 = "select `id`, `spu_id`, `goods_name`, `trade_code`, `specification`, `retail_price`," +
            " `gross_weight`, `origin_place`, `brand`, `category_id`, `search_keywords`, `search_association_words`, " +
            " `sub_title`, `highlights`, `details_code`, `urls`, `status`, `gmtCreated`, `gmtUpdated` " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".`gc_standard_goods_syncrds` " +
            " where `id` > ? and `search_keywords` is not null and `search_keywords` <> '' " +
            " order by `id` asc limit ?;";

    private static final String SELECT_SQL_02 = "select distinct trade_code " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".gc_standard_goods_syncrds where trade_code is not null and trade_code <>'';";

    // 查询审批完成标准商品库商品
    /*private static final String SELECT_SQL_03 = "select distinct trade_code " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".gc_standard_goods_syncrds where status = 1;";*/
    // 查询DTP商品重刷数据
    private static final String SELECT_SQL_03 = "select distinct sgs.trade_code from gc_standard_goods_syncrds sgs " +
            " inner join gc_goods_spu_attr_syncrds gsas on sgs.trade_code = gsas.bar_code " +
            " inner join gc_goods_attr_info_syncrds gais on gsas.attr_id = gais.id " +
            " where gais.id = 31055;";

    private final ParameterTool parameterTool;

    public StandardGoodsSyncrdsDao(ParameterTool parameterTool) {
        this.parameterTool = parameterTool;
    }

    @Override
    public List<StandardGoodsSyncrds> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        int i = 0;
        while (i < 100) {
            try {
                return queryList(offset, pageSize, params);
            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("StandardGoodsSyncrdsDao InterruptedException:{}", e1);
                }
            }
            i ++;
        }

        return Collections.emptyList();
    }

    /**
     * 查询搜索关键词列表
     * @param offsetId
     * @param pageSize
     * @param params
     * @return
     * @throws Exception
     */
    public List<StandardGoodsSyncrds> queryList(long offsetId, int pageSize, Map<String, Object> params) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection connection = null;
        List<StandardGoodsSyncrds> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL);
            ps.setLong(1, offsetId);
            ps.setLong(2, pageSize);

            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new StandardGoodsSyncrds().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.info("StandardGoodsSyncrdsDao offsetId:{} params:{} Exception:{}", offsetId, params, e);
            throw new Exception(e);
        }
        finally {
            LOGGER.info("StandardGoodsSyncrdsDao offsetId:{} params:{} size:{}", offsetId, params, list.size());
            MysqlUtils.close(connection, ps, rs);
        }
    }

    public List<PgConcatParams> queryTradeCodeList() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection connection = null;
        List<PgConcatParams> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL_03);

            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new PgConcatParams(ResultSetConvert.getString(rs, 1)));
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.info("StandardGoodsSyncrdsDao queryTradeCodeList Exception:{}", e);
        }
        finally {
            LOGGER.info("StandardGoodsSyncrdsDao queryTradeCodeList size:{}", list.size());
            MysqlUtils.close(connection, ps, rs);
        }
        return Collections.emptyList();
    }
}
