package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.gc.ConfigSku;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_GOODS_CENTER_TIDB;
import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class ConfigSkuDAO implements QueryLimitDao<ConfigSku> {
    private static final long serialVersionUID = 3248884954630873856L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigSkuDAO.class);

    private static final String SELECT_SQL01 = "SELECT id, sku_no, barcode, approval_number, goods_type, goods_sub_type, state, audit_state, " +
            " (CASE `gmtCreated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtCreated` END) AS `gmtCreated`, " +
            " (CASE `gmtUpdated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtUpdated` END) AS `gmtUpdated`," +
            " merchant_id, source_name, origin_price, spec_name " +
            "FROM `" + SCHEMA_GOODS_CENTER_TIDB + "`.`gc_config_sku` WHERE `id` > ? ORDER BY `id` ASC LIMIT ?;";

    private ParameterTool parameterTool;

    public ConfigSkuDAO(ParameterTool parameterTool) {
        this.parameterTool = parameterTool;
    }

    @Override
    public List<ConfigSku> findLimit(long offsetId, int pageSize, Map<String, Object> params, Connection connection) {
        while (true) {
            try {
                return queryList(pageSize, params);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("ConfigSkuDAO InterruptedException:{}", e1);
                }
            }
        }
    }

    /**
     * 分页查询数据库
     * @param pageSize
     * @param params
     * @return
     * @throws Exception
     */
    private List<ConfigSku> queryList(int pageSize, Map<String, Object> params) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection connection = null;
        List<ConfigSku> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL01);
            ps.setLong(1, (long) params.get("id"));
            ps.setInt(2, pageSize);

            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ConfigSku().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("ConfigSkuDAO params:{} Exception:{}", params, e);
            throw new Exception(e);
        }
        finally {
            LOGGER.info("ConfigSkuDAO params:{} size:{}", params, list.size());
            MysqlUtils.close(connection, ps, rs);
        }
    }
}
