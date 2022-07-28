package cn.wr.collect.sync.dao.time;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.utils.MysqlUtils;
import cn.wr.collect.sync.utils.ResultSetConvert;
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
import java.util.stream.Collectors;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class PgConcatParamsDAO {
    private final ParameterTool tool;
    private static final Logger log = LoggerFactory.getLogger(PgConcatParamsDAO.class);

    private static final String SQL_01 = "select distinct psa.db_id, psa.merchant_id, cs.sku_no " +
            " from " + CommonConstants.SCHEMA_GOODS_CENTER_TIDB + ".gc_config_sku cs " +
            " inner join " + CommonConstants.SCHEMA_GOODS_CENTER_TIDB + ".gc_partner_stores_all psa on cs.merchant_id = psa.merchant_id " +
            " where cs.barcode = ? ;";

    private static final String SQL_02 = "select distinct psa.db_id, psa.merchant_id, pg.internal_id " +
            " from " + CommonConstants.SCHEMA_GOODS_CENTER_TIDB + ".gc_partner_stores_all psa " +
            " inner join " + CommonConstants.SCHEMA_UNION_DRUG_PARTNER + ".partner_goods pg on psa.db_id = pg.db_id " +
            " where pg.trade_code = ?;";

    private static final String SQL_03 = "select distinct trade_code " +
            " from " + CommonConstants.SCHEMA_GOODS_CENTER_TIDB + ".gc_standard_goods_syncrds sgs " +
            " inner join " + CommonConstants.SCHEMA_GOODS_CENTER_TIDB + ".gc_goods_spu gs on sgs.spu_id = gs.id " +
            " where gs.approval_number = ? and sgs.trade_code is not null and sgs.trade_code <> '' " +
            " and sgs.trade_code <> 'null' and sgs.trade_code <> 'NULL' and sgs.trade_code <> '\' and sgs.trade_code <> '.'" +
            " and sgs.trade_code <> '0'; ";

    private static final String SQL_04 = "select distinct trade_code " +
            " from " + CommonConstants.SCHEMA_UNION_DRUG_PARTNER + ".partner_goods pg " +
            " inner join " + CommonConstants.SCHEMA_GOODS_CENTER_TIDB + ".gc_partner_stores_all psa on pg.db_id = psa.db_id " +
            " where pg.approval_number = ? and pg.trade_code is not null and pg.trade_code <> '' " +
            " and pg.trade_code <> 'null' and pg.trade_code <> 'NULL' and pg.trade_code <> '\' and sgs.trade_code <> '.'" +
            " and sgs.trade_code <> '0'; ";

    public PgConcatParamsDAO(ParameterTool parameterTool) {
        this.tool = parameterTool;
    }

    /**
     * 根据条码获取查询条件集合
     * @param tradeCode
     * @return
     */
    public List<PgConcatParams> queryListByTradeCode(String tradeCode) {
        if (StringUtils.isBlank(tradeCode)) {
            return Collections.emptyList();
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection connection = null;
        List<PgConcatParams> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(tool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    tool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME), tool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));
            if (Objects.isNull(connection)) {
                return Collections.emptyList();
            }

            ps = connection.prepareStatement(SQL_01);
            ps.setString(1, tradeCode);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(this.convertConfigSku(rs));
            }

            ps = connection.prepareStatement(SQL_02);
            ps.setString(1, tradeCode);
            // rs = ps.executeQuery();
            while (rs.next()) {
                list.add(this.convertPartnerGoods(rs));
            }

            if (CollectionUtils.isNotEmpty(list)) {
                return list.stream()
                        .filter(params -> Objects.nonNull(params) && StringUtils.isNotBlank(params.getDbId())
                                && StringUtils.isNotBlank(params.getMerchantId())
                                && StringUtils.isNotBlank(params.getInternalId()))
                        .distinct().collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (SQLException e) {
            log.error("PgConcatParamsDAO tradeCode:{} Exception:{}", tradeCode, e);
        }
        finally {
            MysqlUtils.close(connection, ps, rs);
            log.info("PgConcatParamsDAO queryListByTradeCode tradeCode:{}, size:{}",
                    tradeCode, list.size());
        }
        return list;
    }

    /**
     * 根据批文号获取查询条件集合
     * @param approvalNumber
     * @return
     */
    public List<PgConcatParams> queryListByApprovalNum(String approvalNumber) {
        if (StringUtils.isBlank(approvalNumber)) {
            return Collections.emptyList();
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection connection = null;
        Set<String> tradeCodeSet = new HashSet<>();
        try {
            connection = MysqlUtils.retryConnection(tool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    tool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME), tool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));
            if (Objects.isNull(connection)) {
                return Collections.emptyList();
            }

            ps = connection.prepareStatement(SQL_03);
            ps.setString(1, approvalNumber);
            rs = ps.executeQuery();
            while (rs.next()) {
                tradeCodeSet.add(ResultSetConvert.getString(rs, 1));
            }

            ps = connection.prepareStatement(SQL_04);
            ps.setString(1, approvalNumber);
            // rs = ps.executeQuery();
            while (rs.next()) {
                tradeCodeSet.add(ResultSetConvert.getString(rs, 1));
            }

            if (CollectionUtils.isEmpty(tradeCodeSet)) return Collections.emptyList();

            return tradeCodeSet.stream().flatMap(tradeCode -> {
                List<PgConcatParams> l = this.queryListByTradeCode(tradeCode);
                return l.stream();
            }).distinct().collect(Collectors.toList());
        } catch (SQLException e) {
            log.error("PgConcatParamsDAO approvalNumber:{} Exception:{}", approvalNumber, e);
        }
        finally {
            MysqlUtils.close(connection, ps, rs);
            log.info("PgConcatParamsDAO queryListByApprovalNum approvalNumber:{}, size:{}",
                    approvalNumber, tradeCodeSet.size());
        }
        return Collections.emptyList();
    }

    /**
     * 参数转换 基于 gc_config_sku 表处理
     * @param rs
     * @return
     * @throws SQLException
     */
    private PgConcatParams convertConfigSku(ResultSet rs) throws SQLException {
        PgConcatParams concatParams = new PgConcatParams();
        concatParams.setDbId(ResultSetConvert.getString(rs, 1));
        String merchantId = ResultSetConvert.getString(rs, 2);
        concatParams.setMerchantId(merchantId);
        String skuNo = ResultSetConvert.getString(rs, 3);
        if (StringUtils.isNotBlank(skuNo) && StringUtils.isNotBlank(merchantId) && skuNo.indexOf(merchantId) == 0) {
            concatParams.setInternalId(skuNo.substring(merchantId.length() + 1));
        }
        return concatParams;
    }

    /**
     * 参数转换 基于 partner_goods 表处理
     * @param rs
     * @return
     * @throws SQLException
     */
    private PgConcatParams convertPartnerGoods(ResultSet rs) throws SQLException {
        PgConcatParams concatParams = new PgConcatParams();
        concatParams.setDbId(ResultSetConvert.getString(rs, 1));
        concatParams.setMerchantId(ResultSetConvert.getString(rs, 2));
        concatParams.setInternalId(ResultSetConvert.getString(rs, 3));
        return concatParams;
    }
}
