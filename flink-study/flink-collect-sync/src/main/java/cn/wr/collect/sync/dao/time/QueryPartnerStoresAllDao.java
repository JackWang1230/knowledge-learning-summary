package cn.wr.collect.sync.dao.time;

import cn.wr.collect.sync.model.CompareData;
import cn.wr.collect.sync.model.gc.PartnerStoresAll;
import cn.wr.collect.sync.utils.MysqlUtils;
import cn.wr.collect.sync.utils.ResultSetConvert;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_GOODS_CENTER_TIDB;
import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class QueryPartnerStoresAllDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryPartnerStoresAllDao.class);

    private static final String SELECT_SQL = "select `id`, `table_id`, `db_id`, `merchant_id`, `store_id`, `merchant_name`, "
            + " `internal_id`, `group_id`, `store_name`, `longitude`, `latitude`, `address`, `location`, "
            + " (CASE `created_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `created_at` END) AS `created_at`, "
            + " (CASE `update_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `update_at` END) AS `update_at`, "
            + " `gmtcreated`, `gmtupdated`, `channel`, `last_date`, `last_pg_date`, `store_code`, `out_id`, "
            + " `hash_seven`, `baidu_online` from " + SCHEMA_GOODS_CENTER_TIDB + ".gc_partner_stores_all ";

    private static final String SELECT_SQL02 = "select db_id, group_id "
            + " from `" + SCHEMA_GOODS_CENTER_TIDB + "`.`gc_partner_stores_all` ";
    private static final String SELECT_SQL03 = "select db_id "
            + " from `" + SCHEMA_GOODS_CENTER_TIDB + "`.`gc_partner_stores_all` ";

    private static final String SELECT_SQL04 = "select distinct db_id "
            + " from `" + SCHEMA_GOODS_CENTER_TIDB + "`.`gc_partner_stores_all` ";

    private static final String WHERE_SQL_01 = " where gmtupdated > ? order by gmtupdated ASC, id ASC; ";
    private static final String WHERE_SQL_02 = " where id = ?; ";
    private static final String WHERE_SQL_03 = " where id >= ? and  id <= ? order by id asc; ";
    private static final String WHERE_SQL_04 = " where merchant_id= ? and  store_id = ?; ";
    private static final String WHERE_SQL_05 = " where db_id = ?; ";
    private static final String WHERE_SQL_06 = " where merchant_id = ? limit 1; ";
    private static final String WHERE_SQL_07 = " where channel = ? ; ";
    private static final String WHERE_SQL_08 = " where store_id = ? limit 1; ";

    private static final String UPDATE_SQL = "update " + SCHEMA_GOODS_CENTER_TIDB + ".gc_partner_stores_all set  gmtupdated =  ? ";
    private static final String WHERE_SQL06 = " where db_id = ? ";
    private static final String WHERE_SQL07 = " where merchant_id = ? and store_id = ? ";
    private static final String GROUP_SQL01 = " group by db_id, group_id; ";
    private static final String GROUP_SQL02 = " group by db_id; ";

    private final ParameterTool parameterTool;

    public QueryPartnerStoresAllDao(ParameterTool parameterTool) {
        this.parameterTool = parameterTool;
    }

    /**
     * 分页查询 查询失败情况下可以重试
     * @param params
     * @return
     */
    public List<PartnerStoresAll> query(Map<String, Object> params) {
        int i = 0;
        while (i < 100) {
            try {
                return queryList(params);
            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("QueryPartnerStoresAllDao InterruptedException:{}", e1);
                }
            }
            i++;
        }
        return Collections.emptyList();
    }

    /**
     * 根据条件查询门店集合
     * @param params
     * @return
     * @throws Exception
     */
    private List<PartnerStoresAll> queryList(Map<String, Object> params) throws Exception {
        List<PartnerStoresAll> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));
            if (null != params && null != params.get("startTime")) {
                ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL_01);

                ps.setObject(1, params.get("startTime"));
            }
            else if (null != params && null != params.get("startId") && null != params.get("endId")) {
                ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL_03);

                ps.setLong(1, (long) params.get("startId"));
                ps.setLong(2, (long) params.get("endId"));
            }
            else if (null != params && null != params.get("merchantId") && null != params.get("storeId")) {
                ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL_04);

                ps.setInt(1, (int) params.get("merchantId"));
                ps.setInt(2, (int) params.get("storeId"));
            }
            else if (null != params && null != params.get("dbId")) {
                ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL_05);

                ps.setInt(1, (int) params.get("dbId"));
            }
            else if (null != params && null != params.get("channel")) {
                ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL_07);

                ps.setInt(1, (int) params.get("channel"));
            }
            else {
                ps = connection.prepareStatement(SELECT_SQL);
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new PartnerStoresAll().convert(rs));
            }

            return list;
        }
        catch (Exception e) {
            LOGGER.error("QueryPartnerStoresAllDao queryList params:{} Exception:{}", params, e);
            throw new Exception(e);
        }
        finally {
            LOGGER.info("QueryPartnerStoresAllDao queryList params:{} size:{}", params, list.size());
            MysqlUtils.close(connection, ps, rs);
        }
    }

    /**
     * 根据门店id查询门店详情
     * @param params
     * @return
     */
    public PartnerStoresAll queryById(Map<String, Object> params) {
        int i = 0;
        while (i < 100) {
            try {
                return queryDetail(params);
            }
            catch (Exception e) {
                LOGGER.error("QueryPartnerStoresAllDao queryById Exception:{}", e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("QueryPartnerStoresAllDao queryById InterruptedException:{}", e1);
                }
            }
            i ++;
        }
        return null;
    }

    /**
     * 根据条件查询门店详情
     * @param params
     * @return
     * @throws Exception
     */
    private PartnerStoresAll queryDetail(Map<String, Object> params) throws Exception {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));
            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL_02);
            ps.setLong(1, (long) params.get("id"));
            rs = ps.executeQuery();
            PartnerStoresAll partnerStoresAll = null;
            while (rs.next()) {
                partnerStoresAll = new PartnerStoresAll().convert(rs);
            }
            return partnerStoresAll;
        }
        catch (Exception e) {
            LOGGER.error("QueryPartnerStoresAllDao params:{} error:{}", params, e);
            throw new Exception(e);
        }
        finally {
            LOGGER.info("QueryPartnerStoresAllDao params:{}", params);
            MysqlUtils.close(connection, ps, rs);
        }
    }

    /**
     * 根据merchantId+storeId查询门店详情
     * @param merchantId
     * @param storeId
     * @return
     */
    public PartnerStoresAll queryByMerchantIdAndStoreId(Integer merchantId,Integer storeId) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL_04);
            ps.setInt(1, merchantId);
            ps.setInt(2, storeId);

            rs = ps.executeQuery();
            PartnerStoresAll partnerStoresAll = null;
            while (rs.next()) {
                partnerStoresAll = new PartnerStoresAll().convert(rs);
            }
            return partnerStoresAll;
        } catch (Exception e) {
            LOGGER.error("QueryPartnerStoresAllDao storeId:{}, merchantId:{}, error:{}", storeId, merchantId, e);
            return null;
        } finally {
            LOGGER.info("QueryPartnerStoresAllDao storeId:{}, merchantId:{}", storeId, merchantId);
            MysqlUtils.close(connection, ps, rs);
        }
    }

    /**
     * 根据 merchantId 查询 dbId
     * @param merchantId
     * @return
     */
    public Integer queryDbIdByMerchantId(Integer merchantId) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL04 + WHERE_SQL_06);
            ps.setInt(1, merchantId);

            rs = ps.executeQuery();
            Integer dbId = null;
            while (rs.next()) {
                dbId = rs.getInt(1);
            }
            return dbId;
        } catch (Exception e) {
            LOGGER.error("QueryPartnerStoresAllDao merchantId:{}, error:{}", merchantId, e);
            return null;
        } finally {
            LOGGER.info("QueryPartnerStoresAllDao merchantId:{}", merchantId);
            MysqlUtils.close(connection, ps, rs);
        }
    }

    /**
     * 根据 storeId 查询 dbId
     * @param storeId
     * @return
     */
    public Integer queryDbIdByStoreId(Integer storeId) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL04 + WHERE_SQL_08);
            ps.setInt(1, storeId);

            rs = ps.executeQuery();
            Integer dbId = null;
            while (rs.next()) {
                dbId = rs.getInt(1);
            }
            return dbId;
        } catch (Exception e) {
            LOGGER.error("QueryPartnerStoresAllDao storeId:{}, error:{}", storeId, e);
            return null;
        } finally {
            LOGGER.info("QueryPartnerStoresAllDao storeId:{}", storeId);
            MysqlUtils.close(connection, ps, rs);
        }
    }

    /**
     * 根据merchantId+storeId更新门店更新时间字段
     * @param merchantId
     * @param storeId
     * @return
     */
    public int updateByMerchantIdAndStoreId(Integer merchantId,Integer storeId) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int size = 0;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));
            connection.setAutoCommit(false);
            ps = connection.prepareStatement(UPDATE_SQL+WHERE_SQL_04);
            ps.setObject(1,new Date());
            ps.setInt(2,merchantId);
            ps.setInt(3,storeId);
            size = ps.executeUpdate();
            connection.commit();
            return size;
        }
        catch (Exception e) {
            LOGGER.error(" updatePartnerStoresAll error storeId:{},merchantId:{} error:{}",storeId,merchantId , e);
            return 0;
        }
        finally {
            LOGGER.info("updateQueryPartnerStoresAllDao storeId:PartnerStoresAll storeId:{},merchantId:{} size:{}",storeId,merchantId,size );
            MysqlUtils.close(connection, ps, rs);
        }
    }

    /**
     * 查询dbid/groupId用于比较数据
     * @return
     */
    public List<CompareData> queryForCompareStoreGoods(Map<String, Object> params) {
        int i = 0;
        while (i < 100) {
            try {
                return executeCompareStoreGoods(params);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("QueryBasicChangeDao InterruptedException:{}", e1);
                }
            }
            i ++;
        }
        return Collections.emptyList();
    }

    /**
     * 执行查询语句
     * @param params
     * @return
     * @throws Exception
     */
    private List<CompareData> executeCompareStoreGoods(Map<String, Object> params) throws Exception {
        if (null == params) {
            return Collections.emptyList();
        }
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));

            if (Objects.nonNull(params.get("merchantId")) &&  Objects.nonNull(params.get("storeId"))) {
                ps = connection.prepareStatement(SELECT_SQL02 + WHERE_SQL07 + GROUP_SQL01);
                ps.setInt(1, (int) params.get("merchantId"));
                ps.setInt(2, (int) params.get("storeId"));
            }
            else if (Objects.nonNull(params.get("dbId"))) {
                ps = connection.prepareStatement(SELECT_SQL02 + WHERE_SQL06 + GROUP_SQL01);
                ps.setInt(1, (int) params.get("dbId"));
            }

            rs = ps.executeQuery();
            List<CompareData> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new CompareData().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("PartnerGoodsDao queryForCompareStoreGoods params:{} error:{}", params, e);
            throw new Exception(e);
        }
        finally {
            MysqlUtils.close(connection, ps, rs);
        }
    }


    /**
     * 查询dbid/groupId用于比较数据
     * @return
     */
    public List<Integer> queryForCompareGoods(Map<String, Object> params) {
        while (true) {
            try {
                return executeCompareGoods(params);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("QueryBasicChangeDao InterruptedException:{}", e1);
                }
            }
        }
    }

    /**
     * 执行查询语句
     * @param params
     * @return
     * @throws Exception
     */
    private List<Integer> executeCompareGoods(Map<String, Object> params) throws Exception {
        if (Objects.isNull(params)) {
            return Collections.emptyList();
        }
        if (Objects.nonNull(params.get("dbId"))) {
            return Collections.singletonList((int) params.get("dbId"));
        }
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));

            if (Objects.nonNull(params.get("merchantId")) &&  Objects.nonNull(params.get("storeId"))) {
                ps = connection.prepareStatement(SELECT_SQL03 + WHERE_SQL07 + GROUP_SQL02);
                ps.setInt(1, (int) params.get("merchantId"));
                ps.setInt(2, (int) params.get("storeId"));
            }

            rs = ps.executeQuery();
            List<Integer> list = new ArrayList<>();
            while (rs.next()) {
                list.add(ResultSetConvert.getInt(rs, 1));
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("PartnerGoodsDao queryForCompareGoods params:{} error:{}", params, e);
            throw new Exception(e);
        }
        finally {
            MysqlUtils.close(connection, ps, rs);
        }
    }
}
