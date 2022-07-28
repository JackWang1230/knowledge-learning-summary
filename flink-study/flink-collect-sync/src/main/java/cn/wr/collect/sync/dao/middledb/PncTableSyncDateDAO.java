package cn.wr.collect.sync.dao.middledb;

import cn.wr.collect.sync.dao.QueryLimitDAO_V2;
import cn.wr.collect.sync.model.middledb.PncTableSyncDate;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_GOODS_CENTER_TIDB;
import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class PncTableSyncDateDAO implements QueryLimitDAO_V2<PncTableSyncDate> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PncTableSyncDateDAO.class);
    private ParameterTool parameterTool;
    private static final String TABLE_NAME = "pgc_store_info";
    private static final String SELECT_SQL = "SELECT `id`, `table_name`, " +
            " (CASE `last_sync_date` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `last_sync_date` END) AS `last_sync_date`, " +
            " (CASE `gmtUpdated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtUpdated` END) AS `gmtUpdated`, " +
            " (CASE `gmtCreated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtCreated` END) AS `gmtCreated` " +
            " FROM `" + SCHEMA_GOODS_CENTER_TIDB + "`.`pnc_table_sync_date` WHERE `table_name` = ?";

    public PncTableSyncDateDAO(ParameterTool parameterTool) {
        this.parameterTool = parameterTool;
    }

    @Override
    public List<PncTableSyncDate> pageQuery(long offsetId, int pageSize, Map<String, Object> params) {
        return Collections.emptyList();
    }

    @Override
    public PncTableSyncDate querySingle(Map<String, Object> params) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int i = 0 ;
        while (i < 100) {
            try {
                connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_SQOOP_URL),
                        parameterTool.get(MYSQL_DATABASE_SQOOP_USERNAME),
                        parameterTool.get(MYSQL_DATABASE_SQOOP_PASSWORD));
                ps = connection.prepareStatement(SELECT_SQL);
                ps.setString(1, TABLE_NAME);
                rs = ps.executeQuery();
                PncTableSyncDate tableSyncDate = null;
                while (rs.next()) {
                    tableSyncDate = new PncTableSyncDate().convert(rs);
                }
                return tableSyncDate;
            }
            catch (Exception e) {
                LOGGER.error("### PncTableSyncDateDAO params:{}, Exception:{}", params, e);
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e1) {
                    LOGGER.error("### PncTableSyncDateDAO InterruptedException:{}", e);
                }
            }
            finally {
                MysqlUtils.close(connection, ps, rs);
            }
            i ++;
        }
        return null;
    }
}
