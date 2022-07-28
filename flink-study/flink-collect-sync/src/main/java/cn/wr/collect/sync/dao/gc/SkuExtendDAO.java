package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.gc.SkuExtend;
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

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_GOODS_CENTER_TIDB;
import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class SkuExtendDAO implements QueryLimitDao<SkuExtend> {
    private static final long serialVersionUID = -8006866097079036299L;
    private static final Logger LOGGER = LoggerFactory.getLogger(SkuExtendDAO.class);
    private static final String SELECT_SQL01 = "SELECT `id`, `title`, `key_word`, `sku_no`, " +
            " (CASE `gmtCreated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtCreated` END) AS `gmtCreated`, " +
            " (CASE `gmtUpdated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtUpdated` END) AS `gmtUpdated` " +
            " FROM `" + SCHEMA_GOODS_CENTER_TIDB + "`.`gc_sku_extend` WHERE `id` > ? ORDER BY `id` ASC LIMIT ?;";

    private ParameterTool parameterTool;

    public SkuExtendDAO(ParameterTool parameterTool) {
        this.parameterTool = parameterTool;
    }

    @Override
    public List<SkuExtend> findLimit(long offsetId, int pageSize, Map<String, Object> params, Connection connection) {
        int retry = 1;
        while (retry < 100) {
            try {
                return this.queryList(offsetId, pageSize, params);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("### SkuExtendDAO InterruptedException:{}", e1);
                }
            }
            retry ++;
        }
        return Collections.emptyList();
    }

    /**
     * 分页查询数据库
     * @param pageSize
     * @param params
     * @return
     * @throws Exception
     */
    private List<SkuExtend> queryList(long offsetId, int pageSize, Map<String, Object> params) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection connection = null;
        List<SkuExtend> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL01);
            ps.setLong(1, offsetId);
            ps.setLong(2, pageSize);

            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new SkuExtend().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("SkuExtendDAO offsetId:{} params:{} Exception:{}", offsetId, params, e);
            throw new Exception(e);
        }
        finally {
            LOGGER.info("SkuExtendDAO offsetId:{} params:{} size:{}", offsetId, params, list.size());
            MysqlUtils.close(connection, ps, rs);
        }
    }
}
