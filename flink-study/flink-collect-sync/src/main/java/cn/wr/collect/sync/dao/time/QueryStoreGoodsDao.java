package cn.wr.collect.sync.dao.time;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.PgConcatParams;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import cn.wr.collect.sync.utils.MysqlUtils;
import com.alibaba.fastjson.JSON;
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

public class QueryStoreGoodsDao implements QueryLimitDao<PartnerStoreGoods> {
    private static final long serialVersionUID = -205726916503952259L;
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryStoreGoodsDao.class);
    private final String sql;
    private final PgConcatParams concatParams;
    private final ParameterTool parameterTool;

    public QueryStoreGoodsDao(String sql, PgConcatParams concatParams, ParameterTool parameterTool) {
        this.sql = sql;
        this.concatParams = concatParams;
        this.parameterTool = parameterTool;
    }

    @Override
    public List<PartnerStoreGoods> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        int i = 0;
        while (i < 100) {
            try {
                return this.queryList(pageSize, params);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("QueryStoreGoodsDao InterruptedException:{}", e1);
                }
            }
            i ++;
        }
        return Collections.emptyList();
    }

    private List<PartnerStoreGoods> queryList(int pageSize, Map<String, Object> params) throws Exception {
        long start = System.currentTimeMillis();
        // 参数校验
        if (Objects.isNull(concatParams)
                || (StringUtils.isBlank(concatParams.getStoreId()) && StringUtils.isBlank(concatParams.getMerchantId())
            && StringUtils.isBlank(concatParams.getDbId()) && StringUtils.isBlank(concatParams.getGroupId()))) {
            return Collections.emptyList();
        }

        //创建连接
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<PartnerStoreGoods> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PARTNER_URL),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_PASSWORD));

            // 生成条件语句
            String querySql = this.concatWhereSql();
            ps = connection.prepareStatement(querySql);
            ps.setLong(1, (Long) params.get("id"));
            this.concatPsParams(pageSize, ps);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new PartnerStoreGoods().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("QueryStoreGoodsDao queryList concatParams:{}, params:{}, sql:{}, ps:{} error:{}",
                    JSON.toJSONString(concatParams), params, sql, ps, e);
            throw new Exception(e);
        }
        finally {
            long end = System.currentTimeMillis();
            LOGGER.info("QueryStoreGoodsDao concatParams:{}, params:{}, size:{}, time:{}(ms)",
                    JSON.toJSONString(concatParams), params, list.size(), end - start);
            MysqlUtils.close(connection, ps, rs);
        }
    }

    /**
     * 拼接参数
     * @param pageSize
     * @param ps
     * @throws SQLException
     */
    private void concatPsParams(int pageSize, PreparedStatement ps) throws SQLException {
        int i = 2;
        ps.setInt(i++, Integer.parseInt(concatParams.getDbId()));
        ps.setString(i++, concatParams.getGroupId());
        if (StringUtils.isNotBlank(concatParams.getInternalId())) {
            ps.setString(i++, concatParams.getInternalId());
        }
        ps.setInt(i, pageSize);
    }

    /**
     * 拼接sql
     * @return
     */
    private String concatWhereSql() {
        StringBuilder builder = new StringBuilder();
        builder.append(sql);
        builder.append(SQL_STORE_GOODS_WHERE_01);
        if (StringUtils.isNotBlank(concatParams.getInternalId())) {
            builder.append(SQL_STORE_GOODS_WHERE_02);
        }
        builder.append(SQL_STORE_GOODS_ORDER);
        builder.append(SQL_STORE_GOODS_LIMIT);
        return builder.toString();
    }


}
