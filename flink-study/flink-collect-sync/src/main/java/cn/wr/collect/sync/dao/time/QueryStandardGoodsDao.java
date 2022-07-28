package cn.wr.collect.sync.dao.time;

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

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_GOODS_CENTER_TIDB;
import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class QueryStandardGoodsDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryStandardGoodsDao.class);
    private static final String SELECT_SQL_01 = " select distinct trade_code " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".gc_standard_goods_syncrds sgs " +
            " inner join " + SCHEMA_GOODS_CENTER_TIDB + ".gc_goods_spu_attr_syncrds gsa on sgs.trade_code = gsa.bar_code " +
            " where gsa.attr_id = ?;";
    private static final String SELECT_SQL_02 = "select distinct trade_code " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".gc_standard_goods_syncrds sgs " +
            " where sgs.spu_id = ?;";

    private static final String SELECT_SQL_03 = "select distinct trade_code " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".gc_standard_goods_syncrds sgs " +
            " inner join " + SCHEMA_GOODS_CENTER_TIDB + ".gc_goods_spu gs on sgs.spu_id = gs.id " +
            " where gs.approval_number = ? ;";

    private static final String SELECT_SQL_04 = "select distinct trade_code " +
            " from " + SCHEMA_GOODS_CENTER_TIDB + ".gc_standard_goods_syncrds sgs " +
            " inner join " + SCHEMA_GOODS_CENTER_TIDB + ".gc_goods_cate_spu cs on sgs.trade_code = cs.bar_code " +
            " where cs.cate_id = ? and cs.bar_code is not null and cs.bar_code <> '' ;";

    private final ParameterTool parameterTool;

    public QueryStandardGoodsDao(ParameterTool parameterTool) {
        this.parameterTool = parameterTool;
    }

    /**
     * 根据 attrId 查询 标准条码
     * @param attrId
     * @return
     */
    public List<String> queryTradeCodeByAttrId(Long attrId) {
        int i = 0;
        while (i < 100) {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("attrId", attrId);
                return queryList(params);
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
     * 根据 spuId 查询 标准条码
     * @param spuId
     * @return
     */
    public List<String> queryTradeCodeBySpuId(Long spuId) {
        int i = 0;
        while (i < 100) {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("spuId", spuId);
                return queryList(params);
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
     * 根据 cateId 查询 标准条码
     * @param cateId
     * @return
     */
    public List<String> queryTradeCodeByCateId(Long cateId) {
        int i = 0;
        while (i < 100) {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("cateId", cateId);
                return this.queryList(params);
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
     * 根据 批准文号 查询 标准条码
     * @param approvalNumber
     * @return
     */
    public List<String> queryTradeCodeByApprovalNumber(String approvalNumber) {
        int i = 0;
        while (i < 100) {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("approvalNumber", approvalNumber);
                return queryList(params);
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

    private List<String> queryList(Map<String, Object> params) throws Exception {
        long start = System.currentTimeMillis();
        List<String> list = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));
            if (Objects.nonNull(params) && Objects.nonNull(params.get("attrId"))) {
                ps = connection.prepareStatement(SELECT_SQL_01);
                ps.setLong(1, (long) params.get("attrId"));
            }
            else if (Objects.nonNull(params) && Objects.nonNull(params.get("spuId"))) {
                ps = connection.prepareStatement(SELECT_SQL_02);
                ps.setLong(1, (long) params.get("spuId"));
            }
            else if (Objects.nonNull(params) && Objects.nonNull(params.get("approvalNumber"))) {
                ps = connection.prepareStatement(SELECT_SQL_03);
                ps.setString(1, (String) params.get("approvalNumber"));
            }
            else if (Objects.nonNull(params) && Objects.nonNull(params.get("cateId"))) {
                ps = connection.prepareStatement(SELECT_SQL_04);
                ps.setLong(1, (Long) params.get("cateId"));
            }
            else {
                return Collections.emptyList();
            }

            rs = ps.executeQuery();

            list = new ArrayList<>();
            while (rs.next()) {
                if (StringUtils.isNotBlank(rs.getString(1))) {
                    list.add(rs.getString(1));
                }
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("QueryStandardGoodsDao queryList params:{} Exception:{}", params, e);
            throw new Exception(e);
        }
        finally {
            long end = System.currentTimeMillis();
            LOGGER.info("QueryStandardGoodsDao queryList params:{} size:{} time:{}(ms)", params,
                    CollectionUtils.isNotEmpty(list) ? list.size() : 0, (end - start));
            MysqlUtils.close(connection, ps, rs);
        }
    }
}
