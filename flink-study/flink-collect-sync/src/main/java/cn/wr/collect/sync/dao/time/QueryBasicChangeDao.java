package cn.wr.collect.sync.dao.time;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.utils.MysqlUtils;
import com.alibaba.fastjson.JSON;
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

import static cn.wr.collect.sync.constants.PropertiesConstants.*;
import static cn.wr.collect.sync.constants.SqlConstants.*;

public class QueryBasicChangeDao implements QueryLimitDao<PartnerGoods> {
    private static final long serialVersionUID = -205726916503952259L;
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryBasicChangeDao.class);
    private final String sql;
    private final PgConcatParams concatParams;
    private final ParameterTool parameterTool;

    public QueryBasicChangeDao(String sql, PgConcatParams concatParams, ParameterTool parameterTool) {
        this.sql = sql;
        this.concatParams = concatParams;
        this.parameterTool = parameterTool;
    }

    @Override
    public List<PartnerGoods> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        int i = 0;
        while (i < 100) {
            try {
                return this.queryList(pageSize, params);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("QueryBasicChangeDAO InterruptedException:{}", e1);
                }
            }
            i ++;
        }
        return Collections.emptyList();
    }

    private List<PartnerGoods> queryList(int pageSize, Map<String, Object> params) throws Exception {
        // 参数校验
        if (Objects.isNull(concatParams)
                || (StringUtils.isBlank(concatParams.getStoreId()) && StringUtils.isBlank(concatParams.getMerchantId())
            && StringUtils.isBlank(concatParams.getDbId()) && StringUtils.isBlank(concatParams.getApprovalNumber())
            && StringUtils.isBlank(concatParams.getInternalId()) && StringUtils.isBlank(concatParams.getTradeCode())
            && Objects.isNull(concatParams.getSpuId()))) {
            return Collections.emptyList();
        }

        //创建连接
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<PartnerGoods> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PARTNER_URL),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_PASSWORD));

            // 生成条件语句
            String querySql = this.concatWhereSql();
            ps = connection.prepareStatement(querySql);
            this.concatPsParams((Long) params.get("id"), pageSize, ps);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new PartnerGoods().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("QueryBasicChangeDAO queryList concatParams:{} params:{}, sql:{}, ps:{} error:{}",
                    JSON.toJSONString(concatParams), params, sql, ps, e);
            throw new Exception(e);
        }
        finally {
            LOGGER.info("QueryBasicChangeDAO queryList concatParams:{} params:{}, size:{}",
                    JSON.toJSONString(concatParams), params,
                    CollectionUtils.isNotEmpty(list) ? list.size() : 0);
            MysqlUtils.close(connection, ps, rs);
        }
    }

    /**
     * 拼接参数
     * @param offsetId
     * @param pageSize
     * @param ps
     * @throws SQLException
     */
    private void concatPsParams(long offsetId, int pageSize, PreparedStatement ps) throws SQLException {
        int i = 1;
        if (Objects.nonNull(concatParams.getSpuId())) {
            ps.setLong(i++, offsetId);
            ps.setLong(i++, concatParams.getSpuId());
        }
        else if (StringUtils.isNotBlank(concatParams.getApprovalNumber())) {
            ps.setString(i++, concatParams.getApprovalNumber());
            ps.setString(i++, concatParams.getApprovalNumber());
            ps.setLong(i++, offsetId);
        }
        else {
            ps.setLong(i++, offsetId);
            if (StringUtils.isNotBlank(concatParams.getDbId())) {
                ps.setInt(i++, Integer.parseInt(concatParams.getDbId()));
            }
            if (StringUtils.isNotBlank(concatParams.getInternalId())) {
                ps.setString(i++, concatParams.getInternalId());
            }
            if (StringUtils.isNotBlank(concatParams.getTradeCode())) {
                ps.setString(i++, concatParams.getTradeCode());
            }
        }
        ps.setInt(i, pageSize);
    }

    /**
     * 拼接sql
     * @return
     */
    private String concatWhereSql() {
        StringBuilder builder = new StringBuilder();

        if (Objects.nonNull(concatParams.getSpuId())) {
            builder.append(sql);
            builder.append(SQL_GOODS_JOIN01);
            builder.append(SQL_GOODS_WHERE_03);
            builder.append(SQL_GOODS_ORDER);
            builder.append(SQL_GOODS_LIMIT);
        }
        else if (StringUtils.isNotBlank(concatParams.getApprovalNumber())) {
            builder.append(SQL_PARTNER_GOODS_APPROVAL_NUM);
        }
        else {
            builder.append(sql);
            builder.append(SQL_GOODS_WHERE_01);
            if (StringUtils.isNotBlank(concatParams.getDbId())) {
                builder.append(" and db_id = ? ");
            }
            if (StringUtils.isNotBlank(concatParams.getInternalId())) {
                builder.append(" and internal_id = ? ");
            }
            if (StringUtils.isNotBlank(concatParams.getTradeCode())) {
                builder.append(" and trade_code = ? ");
            }
            builder.append(SQL_GOODS_ORDER);
            builder.append(SQL_GOODS_LIMIT);
        }

        return builder.toString();
    }


}
