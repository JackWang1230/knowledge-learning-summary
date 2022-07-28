package cn.wr.collect.sync.dao.middledb;

import cn.wr.collect.sync.dao.QueryLimitDAO_V2;
import cn.wr.collect.sync.model.middledb.PgcStoreInfo;
import cn.wr.collect.sync.model.middledb.PgcStoreInfoShort;
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

public class PgcStoreInfoDAO implements QueryLimitDAO_V2<PgcStoreInfo> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PgcStoreInfoDAO.class);
    private static final String SELECT_SQL01 = "SELECT `id`, `store_id`, `store_name`, `province_id`, " +
            " `province_name`, `city_id`, `city_name`, `district_id`, `district_name`, `address`, " +
            " `organization_id`, `organization_name`, `longitude`, `latitude`, `db_id`, `group_id`, `status`, " +
            " (CASE `gmtCreated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtCreated` END) AS `gmtCreated`, " +
            " (CASE `gmtUpdated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtUpdated` END) AS `gmtUpdated` " +
            "FROM `" + SCHEMA_GOODS_CENTER_TIDB + "`.`pgc_store_info` WHERE `id` > ? ORDER BY `id` ASC LIMIT ?;";

    private static final String SELECT_SQL02 = "SELECT `db_id`, `organization_id`, `organization_name` " +
            "FROM `" + SCHEMA_GOODS_CENTER_TIDB + "`.`pgc_store_info` GROUP BY db_id, organization_id";

    private ParameterTool parameterTool;

    public PgcStoreInfoDAO(ParameterTool parameterTool) {
        this.parameterTool = parameterTool;
    }

    @Override
    public PgcStoreInfo querySingle(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<PgcStoreInfo> pageQuery(long offsetId, int pageSize, Map<String, Object> params) {
        int i = 0;
        while (i < 100) {
            try {
                return queryList(offsetId, pageSize, params);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("### PgcStoreInfoDAO InterruptedException:{}", e1);
                }
            }
            i ++;
        }
        return Collections.emptyList();
    }

    /**
     * 查询dbId/organizationId缓存redis
     * @return
     */
    public List<PgcStoreInfoShort> queryShort() {
        int i = 0;
        while (i < 100) {
            PreparedStatement ps = null;
            ResultSet rs = null;
            Connection connection = null;
            List<PgcStoreInfoShort> list = new ArrayList<>();
            try {
                connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_SQOOP_URL),
                        parameterTool.get(MYSQL_DATABASE_SQOOP_USERNAME),
                        parameterTool.get(MYSQL_DATABASE_SQOOP_PASSWORD));

                ps = connection.prepareStatement(SELECT_SQL02);

                rs = ps.executeQuery();
                while (rs.next()) {
                    list.add(new PgcStoreInfoShort().convert(rs));
                }
                return list;
            }
            catch (Exception e) {
                LOGGER.error("### PgcStoreInfoDAO Exception:{}", e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("### PgcStoreInfoDAO InterruptedException:{}", e1);
                }
            }
            finally {
                LOGGER.info("### PgcStoreInfoDAO queryShort size:{}", list.size());
                MysqlUtils.close(connection, ps, rs);
            }
            i ++;
        }
        return Collections.emptyList();
    }

    /**
     * 分页查询数据库
     *
     * @param pageSize
     * @param params
     * @return
     * @throws Exception
     */
    private List<PgcStoreInfo> queryList(long offsetId, int pageSize, Map<String, Object> params) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection connection = null;
        List<PgcStoreInfo> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_SQOOP_URL),
                    parameterTool.get(MYSQL_DATABASE_SQOOP_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_SQOOP_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL01);
            ps.setLong(1, offsetId);
            ps.setLong(2, pageSize);

            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new PgcStoreInfo().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("PgcStoreInfoDAO offsetId:{} params:{} Exception:{}", offsetId, params, e);
            throw new Exception(e);
        }
        finally {
            LOGGER.info("PgcStoreInfoDAO offsetId:{} params:{} size:{}", offsetId, params, list.size());
            MysqlUtils.close(connection, ps, rs);
        }
    }
}
