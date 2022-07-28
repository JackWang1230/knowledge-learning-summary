package cn.wr.collect.sync.dao.price;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.price.PriceList;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;

public class PriceListDAO implements QueryLimitDao<PriceList> {
    private static final long serialVersionUID = -4944619361549171009L;
    private static final Logger LOGGER = LoggerFactory.getLogger(PriceListDAO.class);

    private static final String SELECT_SQL = "select `id`, `tenant_id`, `organization_id`, `list_id`, " +
            " `list_name`, `parent_list_id`, `pids`, " +
            " `list_code`, `list_key`, " +
            " (CASE `gmt_updated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmt_updated` END) AS `gmt_updated`, " +
            " (CASE `gmt_created` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmt_created` END) AS `gmt_created` " +
            " from `" + CommonConstants.SCHEMA_CN_UD_GSBP_PRICE + "`.`price_list` ";

    private static final String WHERE_SQL = " where organization_id = ? and list_id = ? ";
    private final ParameterTool parameterTool;

    public PriceListDAO(ParameterTool tool) {
        this.parameterTool = tool;
    }


    @Override
    public List<PriceList> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        return null;
    }


    public PriceList queryOne(Map<String, Object> params) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PriceList priceList = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PRICE_URL),
                    parameterTool.get(MYSQL_DATABASE_PRICE_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PRICE_PASSWORD));


            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL);
            ps.setLong(1, (long) params.get("organizationId"));
            ps.setString(2, (String) params.get("listId"));


            rs = ps.executeQuery();

            while (rs.next()) {
                priceList = new PriceList().convert(rs);
            }
            return priceList;
        }
        catch (Exception e) {
            LOGGER.error("PriceListDAO queryOne params:{} error:{}", params, e);
        }
        finally {
            LOGGER.info("PriceListDAO queryOne params:{}", params);
            MysqlUtils.close(connection, ps, rs);
        }

        return priceList;
    }


}
