package cn.wr.collect.sync.dao.stock;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.stock.StockMerchant;
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

public class StockMerchantDAO implements QueryLimitDao<StockMerchant> {
    private static final long serialVersionUID = -4944619361549171009L;
    private static final Logger log = LoggerFactory.getLogger(StockMerchantDAO.class);
    private static final String SELECT_SQL = "select  `organization_id`, `root_id`, `internal_code`, `internal_name`, " +
            " `name`, `short_name`, `status`, `organ_structure`, " +
            " (CASE `gmt_updated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmt_updated` END) AS `gmt_updated`, " +
            " (CASE `gmt_created` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmt_created` END) AS `gmt_created`, " +
            " price_state, stock_state, sync_sku_state " +
            " from `" + CommonConstants.SCHEMA_CN_UD_MID_STOCK + "`.`stock_merchant` ";

    private static final String WHERE_SQL = " where organization_id > ? ";
    private static final String ORDER_SQL = " order by organization_id asc limit ?; ";
    private final ParameterTool parameterTool;

    public StockMerchantDAO(ParameterTool tool) {
        this.parameterTool = tool;
    }


    @Override
    public List<StockMerchant> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
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


    private List<StockMerchant> queryList(int pageSize, Map<String, Object> params) throws Exception {
        //创建连接
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<StockMerchant> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_STOCK_URL),
                    parameterTool.get(MYSQL_DATABASE_STOCK_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_STOCK_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL + ORDER_SQL);
            ps.setLong(1, (long) params.get("id"));
            ps.setInt(2, pageSize);

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new StockMerchant().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            log.error("StockMerchantDAO pageQuery params:{} error:{}", params, e);
            throw new Exception(e);
        }
        finally {
            // long end = System.currentTimeMillis();
            log.info("StockMerchantDAO pageQuery params: {}, size: {}", params, list.size());
            MysqlUtils.close(connection, ps, rs);
        }
    }
}
