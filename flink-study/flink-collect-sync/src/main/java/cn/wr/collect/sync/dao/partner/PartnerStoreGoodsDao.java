package cn.wr.collect.sync.dao.partner;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_UNION_DRUG_PARTNER;
import static cn.wr.collect.sync.constants.PropertiesConstants.MYSQL_DATABASE_PARTNER_PASSWORD;
import static cn.wr.collect.sync.constants.PropertiesConstants.MYSQL_DATABASE_PARTNER_URL;
import static cn.wr.collect.sync.constants.PropertiesConstants.MYSQL_DATABASE_PARTNER_USERNAME;

public class PartnerStoreGoodsDao implements QueryLimitDao<PartnerStoreGoods> {
    private static final long serialVersionUID = -3237592635360864326L;
    private static final Logger LOGGER = LoggerFactory.getLogger(PartnerStoreGoodsDao.class);
    private static final String SELECT_SQL = "select `id`, `db_id`, `table_id`, `goods_internal_id`, `group_id`, " +
            " `price`, `status`, " +
            " (CASE `created_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `created_at` END) AS `created_at`, " +
            " (CASE `updated_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `updated_at` END) AS `updated_at`, " +
            " `gmtcreated`, `gmtupdated`, `member_price` " +
            " from " + SCHEMA_UNION_DRUG_PARTNER + ".`partner_store_goods` ";
    private static final String WHERE_SQL = " where id > ? ";
    private static final String WHERE_SQL01 = " and gmtupdated >= ? and gmtupdated <= ? ";
    private static final String WHERE_SQL04 = " where id = ? ";
    private static final String ORDER_SQL = " order by id asc limit ?; ";

    private static final String SELECT_FROM = "select `id`, `db_id`, `table_id`, `goods_internal_id`, `group_id`, " +
            " `price`, `status`, " +
            " (CASE `created_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `created_at` END) AS `created_at`, " +
            " (CASE `updated_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `updated_at` END) AS `updated_at`, " +
            " `gmtcreated`, `gmtupdated`, `member_price` " +
            " from " + SCHEMA_UNION_DRUG_PARTNER + ".`partner_store_goods` " +
            " where 1=1 ";
    private static final String WHERE_SQL02 = " and db_id = ? and group_id = ? ";

    private static final String WHERE_SQL03 = " and db_id = ? ";

    private static final String WHERE_SQL05 = " where db_id = ? and group_id = ? and goods_internal_id = ? order by gmtupdated desc limit 1";

    private static final String WHERE_SQL06 = " where id in ( %s )";

    private final ParameterTool parameterTool;

    public PartnerStoreGoodsDao(ParameterTool parameterTool) {
        this.parameterTool = parameterTool;
    }

    @Override
    public List<PartnerStoreGoods> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        int i = 0;
        while (i < 100) {
            try {
                return queryList(pageSize, params);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("### PartnerStoreGoodsDao InterruptedException:{}", e1);
                }
            }
            i ++;
        }
        return Collections.emptyList();
    }

    private List<PartnerStoreGoods> queryList(int pageSize, Map<String, Object> params) throws Exception {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<PartnerStoreGoods> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PARTNER_URL),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_PASSWORD));
            if (Objects.nonNull(params.get("idList")) && CollectionUtils.isNotEmpty((List<Long>) params.get("idList"))) {
                String s = String.format(WHERE_SQL06, StringUtils.join((List<Long>) params.get("idList"),","));
                ps = connection.prepareStatement(SELECT_SQL + s);
            } else if (null != params.get("complementStartTime") && null != params.get("complementEndTime")) {
                ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL + WHERE_SQL01 + ORDER_SQL);
                ps.setLong(1, (long) params.get("id"));
                ps.setObject(2, params.get("complementStartTime"));
                ps.setObject(3, params.get("complementEndTime"));
                ps.setInt(4, pageSize);
            }
            else if (null != params.get("dbId") && !"0".equals(params.get("dbId"))) {
                ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL + WHERE_SQL03 + ORDER_SQL);
                ps.setLong(1, (long) params.get("id"));
                ps.setInt(2, (int) params.get("dbId"));
                ps.setInt(3, pageSize);
            }
            else {
                ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL + ORDER_SQL);
                ps.setLong(1, (long) params.get("id"));
                ps.setInt(2, pageSize);
            }

            LOGGER.info("sql:{}", ps);
            rs = ps.executeQuery();
            this.resetPartnerStoreGoods(rs, list);

            return list;
        }
        catch (Exception e) {
            LOGGER.error("PartnerStoreGoodsDao queryList params:{}, error:{}", params, e);
            throw new Exception(e);
        }
        finally {
            LOGGER.info("PartnerStoreGoodsDao queryList params:{}, size:{}", params, list.size());
            MysqlUtils.close(connection, ps, rs);
        }
    }


    public List<PartnerStoreGoods> queryListByDbIdAndGroupId(Integer dbId,String groupId){
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<PartnerStoreGoods> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PARTNER_URL),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_PASSWORD));
            ps = connection.prepareStatement(SELECT_FROM + WHERE_SQL02 );
            ps.setInt(1,dbId);
            ps.setObject(2,groupId);
            rs = ps.executeQuery();
            resetPartnerStoreGoods(rs, list);
            return list;
        }
        catch (Exception e) {
            LOGGER.error("### PartnerStoreGoodsDao queryList dbId:{},groupId:{} error:{}",dbId,groupId , e);
            return null;
        }
        finally {
            LOGGER.error("### PartnerStoreGoodsDao queryList dbId:{},groupId:{} error:{}",dbId,groupId);
            MysqlUtils.close(connection, ps, rs);
        }
    }

    private void resetPartnerStoreGoods(ResultSet rs, List<PartnerStoreGoods> list) throws SQLException {
        while (rs.next()) {
            list.add(new PartnerStoreGoods().convert(rs));
        }
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    public PartnerStoreGoods querySingle(Long id) {
        //创建连接
        int retry = 0;
        while (true) {
            try {
                return querySingleById(id);
            }
            catch (Exception e) {
                if (retry > 100) return null;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("### PartnerStoreGoodsDao InterruptedException:{}", e1);
                }
                retry ++;
            }
        }
    }

    private PartnerStoreGoods querySingleById(Long id) throws Exception {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PARTNER_URL),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL04);
            ps.setLong(1, id);

            rs = ps.executeQuery();
            if (rs.next()) {
                return new PartnerStoreGoods().convert(rs);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error("### PartnerStoreGoodsDao querySingleById id:{} error:{}", id, e);
            throw new Exception(e);
        }
        finally {
            // long end = System.currentTimeMillis();
            LOGGER.info("### PartnerStoreGoodsDao querySingleById id:{}", id);
            MysqlUtils.close(connection, ps, rs);
        }
    }


    /**
     * 查询单条记录
     * @param id
     * @return
     */
    public PartnerStoreGoods querySingle(Integer dbId, String groupId, String goodsInternalId) {
        //创建连接
        int retry = 0;
        while (true) {
            try {
                return querySingleByDbIdGroupIdInternalId(dbId, groupId, goodsInternalId);
            }
            catch (Exception e) {
                if (retry > 100) return null;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("### PartnerStoreGoodsDao InterruptedException:{}", e1);
                }
                retry ++;
            }
        }
    }

    private PartnerStoreGoods querySingleByDbIdGroupIdInternalId(Integer dbId, String groupId, String goodsInternalId)
            throws Exception {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PARTNER_URL),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL05);
            ps.setLong(1, dbId);
            ps.setString(2, groupId);
            ps.setString(3, goodsInternalId);

            rs = ps.executeQuery();
            if (rs.next()) {
                return new PartnerStoreGoods().convert(rs);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error("### PartnerStoreGoodsDao querySingleById dbId:{} groupId:{} goodsInternalId:{} error:{}",
                    dbId, groupId, goodsInternalId, e);
            throw new Exception(e);
        }
        finally {
            MysqlUtils.close(connection, ps, rs);
        }
    }
}
