package cn.wr.collect.sync.dao.chain;

import cn.wr.collect.sync.constants.ChainDbTypeEnum;
import cn.wr.collect.sync.model.chain.MatchCode;
import cn.wr.collect.sync.utils.MysqlUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class MatchCodeDAO {
    private static final Logger log = LoggerFactory.getLogger(MatchCodeDAO.class);

    // 全局配置参数
    private final ParameterTool tool;
    // 更新goods表
    private static final String UPDATE_SQL_GOODS = "update %s.standard_goods set trade_code = ? where internal_id = ? and trade_code <> ?;";
    // 更新goods_attr表
    private static final String UPDATE_SQL_GOODS_ATTR = "update %s.goods_attr set is_verify = 1, trade_code = ? where internal_id = ? and trade_code <> ?;";

    public MatchCodeDAO(ParameterTool tool) {
        this.tool = tool;
    }

    public void updateByMatchCode(MatchCode matchCode) {
        int i = 0;
        // 循环执行，数据库异常情况下，可重试链接
        while (i < 10) {
            try {
                this.update(matchCode);
                return;
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e1) {
                    log.error("MatchCodeDAO cycle:{} InterruptedException:{}", i, e1);
                }
            }
            i ++;
        }
    }

    private Connection initConn(Integer dbType) {
        ChainDbTypeEnum chainDb = ChainDbTypeEnum.get(dbType);
        if (Objects.isNull(chainDb)) {
            log.error("MatchCodeDAO chainDb is null: {}", dbType);
            return null;
        }
        switch (chainDb) {
            case Normal:
                return MysqlUtils.retryConnection(tool.get(MYSQL_DATABASE_CHAIN_URL),
                        tool.get(MYSQL_DATABASE_CHAIN_USERNAME),
                        tool.get(MYSQL_DATABASE_CHAIN_PASSWORD));
            case Single:
                return MysqlUtils.retryConnection(tool.get(MYSQL_DATABASE_CHAIN_SINGLE_URL),
                        tool.get(MYSQL_DATABASE_CHAIN_SINGLE_USERNAME),
                        tool.get(MYSQL_DATABASE_CHAIN_SINGLE_PASSWORD));
            default:
                break;
        }
        return null;
    }

    private void update(MatchCode matchCode) throws Exception {
        if (StringUtils.isBlank(matchCode.getDbname()) || StringUtils.isBlank(matchCode.getInternalId())
            || StringUtils.isBlank(matchCode.getTradeCode()) || Objects.isNull(matchCode.getDbType())) {
            log.error("MatchCodeDAO params is not valid: {}", JSON.toJSONString(matchCode));
            return;
        }
        //创建连接
        Connection connection = null;
        PreparedStatement ps = null;
        int goodsUpdateCount = 0;
        int goodsAttrsUpdateCount = 0;
        try {
            // init connection
            connection = this.initConn(matchCode.getDbType());

            if (Objects.isNull(connection)) {
                log.error("MatchCodeDAO connection init failed:{}", JSON.toJSONString(matchCode));
                return;
            }

            // start transaction
            connection.setAutoCommit(false);

            // update goods
            String goodsSql = String.format(UPDATE_SQL_GOODS, matchCode.getDbname());
            ps = connection.prepareStatement(goodsSql);
            ps.setString(1, matchCode.getTradeCode());
            ps.setString(2, matchCode.getInternalId());
            ps.setString(3, matchCode.getTradeCode());
            goodsUpdateCount = ps.executeUpdate();
            if (goodsUpdateCount <= 0) {
                log.info("MatchCodeDAO goods update is empty:{}", JSON.toJSONString(matchCode));

                // commit transaction
                connection.commit();
                return;
            }

            // update goods_attr
            String goodsAttrSql = String.format(UPDATE_SQL_GOODS_ATTR, matchCode.getDbname());
            ps = connection.prepareStatement(goodsAttrSql);
            ps.setString(1, matchCode.getTradeCode());
            ps.setString(2, matchCode.getInternalId());
            ps.setString(3, matchCode.getTradeCode());
            goodsAttrsUpdateCount = ps.executeUpdate();
            if (goodsAttrsUpdateCount <= 0) {
                log.info("MatchCodeDAO goods_attr update is empty:{}", JSON.toJSONString(matchCode));
            }

            // commit transaction
            connection.commit();

        }
        catch (Exception e) {
            try {
                // if exception commit rollback
                if (Objects.nonNull(connection)) connection.rollback();
            }
            catch (SQLException sqlException) {
                log.error("MatchCodeDAO update rollback params:{} error:{}", JSON.toJSONString(matchCode), sqlException);
            }

            log.error("MatchCodeDAO update params:{} error:{}", JSON.toJSONString(matchCode), e);
            throw new Exception(e);
        }
        finally {
            log.info("MatchCodeDAO update params:{}, goods count:{}, attr count:{}",
                    JSON.toJSONString(matchCode), goodsUpdateCount, goodsAttrsUpdateCount);

            // finally close connection ps
            MysqlUtils.close(connection, ps, null);
        }
    }
}
