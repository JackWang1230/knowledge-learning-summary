package cn.wr.collect.sync.dao.partner;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.partner.Partners;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_UNION_DRUG_PARTNER;
import static cn.wr.collect.sync.constants.PropertiesConstants.*;

public class PartnersDAO implements QueryLimitDao<Partners> {
    private static final long serialVersionUID = -4944619361549171009L;
    private static final Logger LOGGER = LoggerFactory.getLogger(PartnersDAO.class);
    private static final String SELECT_SQL = "select `id`, `common_name`, `full_name`, `cooperation`, `dbname`, " +
            " `discount`, `status`, " +
            " (CASE `created_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `created_at` END) AS `created_at`, " +
            " (CASE `updated_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `updated_at` END) AS `updated_at`, " +
            " (CASE `start_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `start_at` END) AS `start_at`, " +
            " (CASE `finish_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `finish_at` END) AS `finish_at`, " +
            " (CASE `release_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `release_at` END) AS `release_at`, " +
            " `out_id`, `organizationId`, `orderInsert`, `blacklist_status`, `transiteDiscount`, `type`, `channel`, `partners_type` " +
            " from " + SCHEMA_UNION_DRUG_PARTNER + ".`partners` ";

    private static final String SELECT_SQL_02 = "select `id`, `common_name`, `full_name`, `cooperation`, `dbname`, " +
            " `discount`, `status`, " +
            " (CASE `created_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `created_at` END) AS `created_at`, " +
            " (CASE `updated_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `updated_at` END) AS `updated_at`, " +
            " (CASE `start_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `start_at` END) AS `start_at`, " +
            " (CASE `finish_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `finish_at` END) AS `finish_at`, " +
            " (CASE `release_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `release_at` END) AS `release_at`, " +
            " `out_id`, `organizationId`, `orderInsert`, `blacklist_status`, `transiteDiscount`, `type`, `channel`, `partners_type` " +
            " from " + SCHEMA_UNION_DRUG_PARTNER + ".`partners` " +
            " where organizationId = ? and cooperation not like 'o2o%' limit 1; ";

    private static final String WHERE_SQL = " where id > ? ";
    private static final String ORDER_SQL = " order by id asc limit ?; ";
    private final ParameterTool parameterTool;

    public PartnersDAO(ParameterTool tool) {
        this.parameterTool = tool;
    }

    @Override
    public List<Partners> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        int i = 0;
        while (i < 100) {
            try {
                return queryList(pageSize, params);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("PartnersDAO InterruptedException:{}", e1);
                }
            }
            i ++;
        }
        return Collections.emptyList();
    }

    private List<Partners> queryList(int pageSize, Map<String, Object> params) throws Exception {
        //创建连接
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Partners> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PARTNER_URL),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL + ORDER_SQL);
            ps.setLong(1, (long) params.get("id"));
            ps.setInt(2, pageSize);

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Partners().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("PartnersDAO queryList params:{} error:{}", params, e);
            throw new Exception(e);
        }
        finally {
            // long end = System.currentTimeMillis();
            LOGGER.info("PartnersDAO queryList params:{}, size:{}", params, list.size());
            MysqlUtils.close(connection, ps, rs);
        }
    }

    public Partners findByMerchantId(Integer merchantId) {
        if (Objects.isNull(merchantId)) {
            return null;
        }
        int i = 0;
        while (i < 100) {
            try {
                return this.selectByMerchantId(merchantId);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("PartnersDAO InterruptedException:{}", e1);
                }
            }
            i ++;
        }
        return null;
    }

    private Partners selectByMerchantId(Integer merchantId) throws Exception {

        //创建连接
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Partners partners = new Partners();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PARTNER_URL),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL_02);
            ps.setInt(1, merchantId);

            rs = ps.executeQuery();

            while (rs.next()) {
                partners = new Partners().convert(rs);
            }
            return partners;
        }
        catch (Exception e) {
            LOGGER.error("PartnersDAO queryList merchantId:{} error:{}", merchantId, e);
            throw new Exception(e);
        }
        finally {
            // long end = System.currentTimeMillis();
            LOGGER.info("PartnersDAO selectByMerchantId merchantId:{}", merchantId);
            MysqlUtils.close(connection, ps, rs);
        }
    }
}
