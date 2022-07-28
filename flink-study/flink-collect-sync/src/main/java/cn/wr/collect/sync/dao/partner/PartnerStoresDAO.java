package cn.wr.collect.sync.dao.partner;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.partner.PartnerStores;
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

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_UNION_DRUG_PARTNER;
import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class PartnerStoresDAO implements QueryLimitDao<PartnerStores> {
    private static final long serialVersionUID = -4944619361549171009L;
    private static final Logger log = LoggerFactory.getLogger(PartnerStoresDAO.class);
    private static final String SELECT_SQL = "select `id`, `db_id`, `table_id`, `internal_id`, `group_id`, " +
            " `common_name`, `number`, `address`, `phone`, `status`, `out_id`, `province`, `city`, `area`, " +
            " `business_time`, `longitude`, `latitude`, " +
            " (CASE `created_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `created_at` END) AS `created_at`, " +
            " (CASE `updated_at` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `updated_at` END) AS `updated_at`, " +
            " (CASE `gmtcreated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtcreated` END) AS `gmtcreated`, " +
            " (CASE `gmtupdated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtupdated` END) AS `gmtupdated` " +
            " from " + SCHEMA_UNION_DRUG_PARTNER + ".`partner_stores` ";

    private static final String WHERE_SQL = " where id > ? ";
    private static final String ORDER_SQL = " order by id asc limit ?; ";
    private final ParameterTool parameterTool;

    public PartnerStoresDAO(ParameterTool tool) {
        this.parameterTool = tool;
    }

    @Override
    public List<PartnerStores> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        int i = 0;
        while (i < 100) {
            try {
                return queryList(pageSize, params);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    log.error("PartnerStoresDAO InterruptedException:{}", e1);
                }
            }
            i ++;
        }
        return Collections.emptyList();
    }

    /**
     * 分页查询
     * @param pageSize
     * @param params
     * @return
     * @throws Exception
     */
    private List<PartnerStores> queryList(int pageSize, Map<String, Object> params) throws Exception {
        //创建连接
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<PartnerStores> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PARTNER_URL),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PARTNER_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL + ORDER_SQL);
            ps.setLong(1, (long) params.get("id"));
            ps.setInt(2, pageSize);
            log.info(ps.toString());
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new PartnerStores().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            log.error("PartnerStoresDAO queryList params:{} error:{}", params, e);
            throw new Exception(e);
        }
        finally {
            log.info("PartnerStoresDAO queryList params:{}, size:{}", params, list.size());
            MysqlUtils.close(connection, ps, rs);
        }
    }
}
