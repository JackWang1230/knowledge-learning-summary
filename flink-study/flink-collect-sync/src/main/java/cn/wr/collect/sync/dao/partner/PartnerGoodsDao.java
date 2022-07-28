package cn.wr.collect.sync.dao.partner;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_UNION_DRUG_PARTNER;
import static cn.wr.collect.sync.constants.PropertiesConstants.MYSQL_DATABASE_PARTNER_PASSWORD;
import static cn.wr.collect.sync.constants.PropertiesConstants.MYSQL_DATABASE_PARTNER_URL;
import static cn.wr.collect.sync.constants.PropertiesConstants.MYSQL_DATABASE_PARTNER_USERNAME;

public class PartnerGoodsDao implements QueryLimitDao<PartnerGoods> {
    private static final long serialVersionUID = -4944619361549171009L;
    private static final Logger LOGGER = LoggerFactory.getLogger(PartnerGoodsDao.class);
    private static final String SELECT_SQL = "select `id`, `db_id`, `table_id`, `internal_id`, `common_name`, `trade_code`, " +
            " `approval_number`, `form`, `pack`, `price`, `manufacturer`, `status`, " +
            " (CASE `goods_create_time` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `goods_create_time` END) AS `goods_create_time`, " +
            " (CASE `goods_update_time` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `goods_update_time` END) AS `goods_update_time`, " +
            " (CASE `created_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `created_at` END) AS `created_at`, " +
            " (CASE `updated_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `updated_at` END) AS `updated_at`, " +
            " (CASE `gmtcreated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtcreated` END) AS `gmtcreated`, " +
            " (CASE `gmtupdated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtupdated` END) AS `gmtupdated` " +
            " from " + SCHEMA_UNION_DRUG_PARTNER + ".`partner_goods` ";

    private static final String SELECT_SQL_02 = "select distinct db_id from "+ SCHEMA_UNION_DRUG_PARTNER + ".`partner_goods` where 1 = 1 ";

    private static final String WHERE_SQL = " where id > ? ";
    private static final String WHERE_SQL01 = " and db_id = ? ";
    private static final String WHERE_SQL02 = " and gmtupdated >= ? and gmtupdated <= ? ";
    private static final String WHERE_SQL03 = " where id = ? ";
    private static final String WHERE_SQL04 = " where db_id = ? and internal_id = ? order by gmtupdated desc limit 1 ";
    private static final String WHERE_SQL05 = " where id in ( %s )";
    private static final String WHERE_SQL06 = " and internal_id = ? ";
    private static final String ORDER_SQL = " order by id asc limit ?; ";
    private final ParameterTool parameterTool;

    public PartnerGoodsDao(ParameterTool tool) {
        this.parameterTool = tool;
    }

    @Override
    public List<PartnerGoods> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        int i = 0;
        while (i < 100) {
            try {
                return queryList(pageSize, params);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("### PartnerGoodsDao InterruptedException:{}", e1);
                }
            }
            i ++;
        }
        return Collections.emptyList();
    }

    private List<PartnerGoods> queryList(int pageSize, Map<String, Object> params) throws Exception {
        //创建连接
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<PartnerGoods> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PARTNER_URL),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_PASSWORD));
            if (Objects.nonNull(params.get("idList")) && CollectionUtils.isNotEmpty((List<Long>) params.get("idList"))) {
                String s = String.format(WHERE_SQL05, StringUtils.join((List<Long>) params.get("idList"),","));
                ps = connection.prepareStatement(SELECT_SQL + s);
            } else if (null != params.get("complementStartTime") && null != params.get("complementEndTime") && params.get("dbId") != null) {
                ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL + WHERE_SQL01 + WHERE_SQL02 + ORDER_SQL);
                ps.setLong(1, (long) params.get("id"));
                ps.setInt(2, (int) params.get("dbId"));
                ps.setObject(3, params.get("complementStartTime"));
                ps.setObject(4, params.get("complementEndTime"));
                ps.setInt(5, pageSize);
            } else if (null != params.get("complementStartTime") && null != params.get("complementEndTime")) {
                ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL + WHERE_SQL02 + ORDER_SQL);
                ps.setLong(1, (long) params.get("id"));
                ps.setObject(2, params.get("complementStartTime"));
                ps.setObject(3, params.get("complementEndTime"));
                ps.setInt(4, pageSize);
            }
            else if (Objects.nonNull(params.get("goodsInternalId")) && Objects.nonNull(params.get("dbId"))) {
                ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL + WHERE_SQL01 + WHERE_SQL06 + ORDER_SQL);
                ps.setLong(1, (long) params.get("id"));
                ps.setInt(2, (int) params.get("dbId"));
                ps.setString(3, (String) params.get("goodsInternalId"));
                ps.setInt(4, pageSize);
            }
            else if (null != params.get("dbId")) {
                ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL + WHERE_SQL01 + ORDER_SQL);
                ps.setLong(1, (long) params.get("id"));
                ps.setInt(2, (int) params.get("dbId"));
                ps.setInt(3, pageSize);
            } else {
                ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL + ORDER_SQL);
                ps.setLong(1, (long) params.get("id"));
                ps.setInt(2, pageSize);
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new PartnerGoods().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("### PartnerGoodsDao queryList params:{} error:{}", params, e);
            throw new Exception(e);
        }
        finally {
            // long end = System.currentTimeMillis();
            LOGGER.info("### PartnerGoodsDao queryList params:{}, size:{}", params, list.size());
            MysqlUtils.close(connection, ps, rs);
        }
    }

    public List<Integer> getDbId( Map<String, Object> params ) throws Exception{
        //创建连接
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Integer> list = new ArrayList<>();

        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PARTNER_URL),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_PASSWORD));

             if (null != params.get("complementStartTime") && null != params.get("complementEndTime")) {
                ps = connection.prepareStatement(SELECT_SQL_02 + WHERE_SQL02 );
                ps.setObject(1, params.get("complementStartTime"));
                ps.setObject(2, params.get("complementEndTime"));
            }
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getInt("db_id"));
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("### PartnerGoodsDao getDbId params:{} error:{}", params, e);
            throw new Exception(e);
        }
        finally {
            LOGGER.info("### PartnerGoodsDao getDbId params:{}, size:{}", params, list.size());
            MysqlUtils.close(connection, ps, rs);
        }
    }

    /**
     * 根据id查询单条记录
     * @param id
     * @return
     */
    public PartnerGoods querySingle(Long id) {
        //创建连接
        int retry = 0;
        while (retry < 100) {
            try {
                return querySingleById(id);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("### PartnerGoodsDao InterruptedException:{}", e1);
                }
                retry ++;
            }
        }
        return null;
    }

    private PartnerGoods querySingleById(Long id) throws Exception {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PARTNER_URL),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL03);
            ps.setLong(1, id);

            rs = ps.executeQuery();
            if (rs.next()) {
                return new PartnerGoods().convert(rs);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error("### PartnerGoodsDao querySingleById id:{} error:{}", id, e);
            throw new Exception(e);
        }
        finally {
            // long end = System.currentTimeMillis();
            // LOGGER.info("### PartnerGoodsDao querySingleById id:{}", id);
            MysqlUtils.close(connection, ps, rs);
        }
    }


    /**
     * 根据dbId/internalId查询单条记录
     * @param dbId
     * @param internalId
     * @return
     */
    public PartnerGoods querySingle(Integer dbId, String internalId) {
        //创建连接
        int retry = 0;
        while (retry < 100) {
            try {
                return this.querySingleByDbIdInternalId(dbId, internalId);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("### PartnerGoodsDao InterruptedException:{}", e1);
                }
                retry ++;
            }
        }
        return null;
    }

    private PartnerGoods querySingleByDbIdInternalId(Integer dbId, String internalId) throws Exception {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PARTNER_URL),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_PASSWORD));
            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL04);
            ps.setLong(1, dbId);
            ps.setString(2, internalId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new PartnerGoods().convert(rs);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error("### PartnerGoodsDao querySingleById dbId:{}, internalId:{} error:{}", dbId, internalId, e);
            throw new Exception(e);
        }
        finally {
            MysqlUtils.close(connection, ps, rs);
        }
    }
}
