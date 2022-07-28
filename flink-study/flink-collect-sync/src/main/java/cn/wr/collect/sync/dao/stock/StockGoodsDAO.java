package cn.wr.collect.sync.dao.stock;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.stock.StockGoods;
import cn.wr.collect.sync.utils.MysqlUtils;
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

import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class StockGoodsDAO implements QueryLimitDao<StockGoods> {
    private static final long serialVersionUID = -4944619361549171009L;
    private static final Logger log = LoggerFactory.getLogger(StockGoodsDAO.class);
    private static final String SELECT_SQL = "select `id`, `stock_no`, `sku_no`, `total_amount`, `used_amount`, " +
            " `quantity`, `unit`, `center_state`, `sale_state`, " +
            " (CASE `start_time` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `start_time` END) AS `start_time`, " +
            " (CASE `end_time` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `end_time` END) AS `end_time`, " +
            " `once`, `virtual_quantity`, `virtual_on`, `merchant_id`, `store_id`, `internal_id`, `source`, " +
            " (CASE `gmt_updated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmt_updated` END) AS `gmt_updated`, " +
            " (CASE `gmt_created` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmt_created` END) AS `gmt_created`, " +
            " ttl, last_get " +
            " from `" + CommonConstants.SCHEMA_CN_UD_MID_STOCK + "`.`stock_goods` ";

    private static final String WHERE_SQL = " where id > ? ";
    private static final String ORDER_SQL = " order by id asc limit ?; ";
    private final ParameterTool parameterTool;

    public StockGoodsDAO(ParameterTool tool) {
        this.parameterTool = tool;
    }

    @Override
    public List<StockGoods> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        int i = 0;
        while (i < 100) {
            try {
                return queryList(pageSize, params);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    log.error("StockGoodsDAO InterruptedException:{}", e1);
                }
            }
            i ++;
        }
        return Collections.emptyList();
    }

    private List<StockGoods> queryList(int pageSize, Map<String, Object> params) throws Exception {
        //创建连接
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<StockGoods> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_STOCK_URL),
                    parameterTool.get(MYSQL_DATABASE_STOCK_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_STOCK_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL + ORDER_SQL);
            ps.setLong(1, (long) params.get("id"));
            ps.setInt(2, pageSize);

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new StockGoods().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            log.error("StockGoodsDAO pageQuery params:{} error:{}", params, e);
            throw new Exception(e);
        }
        finally {
            // long end = System.currentTimeMillis();
            log.info("StockGoodsDAO pageQuery params: {}, size: {}", params, list.size());
            MysqlUtils.close(connection, ps, rs);
        }
    }
}
