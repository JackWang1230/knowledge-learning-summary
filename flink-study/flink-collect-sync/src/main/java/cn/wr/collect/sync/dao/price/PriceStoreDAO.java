package cn.wr.collect.sync.dao.price;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.price.PriceStore;
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

public class PriceStoreDAO implements QueryLimitDao<PriceStore> {
    private static final long serialVersionUID = -205726916503952259L;
    private static final Logger log = LoggerFactory.getLogger(PriceStoreDAO.class);

    private static final String SELECT_SQL = "select `id`, `tenant_id`, `organization_id`, " +
            " `store_id`, `list_id`, `ud_list_id`, `store_key`, " +
            " (CASE `gmt_updated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmt_updated` END) AS `gmt_updated`, " +
            " (CASE `gmt_created` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmt_created` END) AS `gmt_created` " +
            " from `" + CommonConstants.SCHEMA_CN_UD_GSBP_PRICE + "`.`price_store` ";

    private static final String WHERE_SQL = " where organization_id = ? and list_id = ? and id > ? ";

    private static final String WHERE_SQL_01 = " where organization_id = ? and store_id = ? ";

    private static final String ORDER_SQL = " order by id asc limit ?; ";

    private final ParameterTool parameterTool;

    public PriceStoreDAO(ParameterTool tool) {
        this.parameterTool = tool;
    }

    @Override
    public List<PriceStore> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        int i = 0;
        while (i < 100) {
            try {
                return queryList(pageSize, params);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    log.error("PriceStoreDAO InterruptedException:{}", e1);
                }
            }
            i ++;
        }
        return Collections.emptyList();
    }

    private List<PriceStore> queryList(int pageSize, Map<String, Object> params) throws Exception {
        //创建连接
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<PriceStore> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PRICE_URL),
                    parameterTool.get(MYSQL_DATABASE_PRICE_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PRICE_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL + ORDER_SQL);
            ps.setLong(1, (long) params.get("organizationId"));
            ps.setString(2, (String) params.get("listId"));
            ps.setLong(3, (long) params.get("id"));
            ps.setInt(4, pageSize);

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new PriceStore().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            log.error("PriceStoreDAO pageQuery params:{} error:{}", params, e);
            throw new Exception(e);
        }
        finally {
            // long end = System.currentTimeMillis();
            log.info("PriceStoreDAO pageQuery params: {}, size: {}", params, list.size());
            MysqlUtils.close(connection, ps, rs);
        }
    }

    public List<PriceStore> queryListForInit(String queryType, String[] organizationIds) throws Exception {
        //创建连接
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<PriceStore> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PRICE_URL),
                    parameterTool.get(MYSQL_DATABASE_PRICE_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PRICE_PASSWORD));

            StringBuffer stringBuffer = new StringBuffer("?");
            for (int i = 0; i <= organizationIds.length - 2; i++)
            {
                stringBuffer.append(",?");
            }

            if (queryType.equals("merchantId")) {
                ps = connection.prepareStatement(SELECT_SQL + " where organization_id in (".concat(stringBuffer.toString()).concat(")"));
            } else {
                ps = connection.prepareStatement(SELECT_SQL + " where store_id in (".concat(stringBuffer.toString()).concat(")"));
            }

            for (int i = 0; i <= organizationIds.length - 1; i++) {
                ps.setLong(i + 1, Long.parseLong(organizationIds[i]));
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new PriceStore().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            log.error("PriceStoreDAO queryListForInit params:{} error:{}", organizationIds, e);
            throw new Exception(e);
        }
        finally {
            // long end = System.currentTimeMillis();
            log.info("PriceStoreDAO queryListForInit params: {}, size: {}", organizationIds, list.size());
            MysqlUtils.close(connection, ps, rs);
        }
    }


    public PriceStore queryOne(Map<String, Object> params) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PriceStore priceStore = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PRICE_URL),
                    parameterTool.get(MYSQL_DATABASE_PRICE_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PRICE_PASSWORD));


            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL_01);
            ps.setLong(1, (long) params.get("organizationId"));
            ps.setLong(2, (long) params.get("storeId"));


            rs = ps.executeQuery();

            while (rs.next()) {
                priceStore = new PriceStore().convert(rs);
            }
            return priceStore;
        }
        catch (Exception e) {
            log.error("PriceStore queryOne params:{} error:{}", params, e);
        }
        finally {
            log.info("PriceStore queryOne params:{}", params);
            MysqlUtils.close(connection, ps, rs);
        }

        return priceStore;
    }
}
