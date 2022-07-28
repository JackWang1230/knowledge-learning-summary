package cn.wr.collect.sync.dao.price;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.price.PriceListDetails;
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

import static cn.wr.collect.sync.constants.PropertiesConstants.*;

public class PriceListDetailsDAO implements QueryLimitDao<PriceListDetails> {
    private static final long serialVersionUID = -205726916503952259L;
    private static final Logger log = LoggerFactory.getLogger(PriceListDetailsDAO.class);
    private static final String SELECT_SQL = "select `id`, `tenant_id`, `organization_id`, `list_id`, `internal_id`, " +
            " `sku_price`, `original_price`, `member_price`, `min_price`, `max_price`, `channel`, " +
            " `sub_channel`, `list_status`, `source`, `detail_key`, " +
            " (CASE `gmt_updated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmt_updated` END) AS `gmt_updated`, " +
            " (CASE `gmt_created` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmt_created` END) AS `gmt_created` " +
            " from `" + CommonConstants.SCHEMA_CN_UD_GSBP_PRICE + "`.`price_list_details` ";

    private static final String WHERE_SQL = " where organization_id = ? and list_id = ? and id > ? ";

    private static final String WHERE_SQL_01 = " where organization_id = ? and list_id = ? and internal_id = ? ";

//    private static final String WHERE_SQL_02 = " where organization_id = ? and list_id = ? and internal_id in ('1028459', '1020414' ,'1002922', '1033458', '1025559', '1014107', '1015921')";

    private static final String ORDER_SQL = " order by id asc limit ?; ";
    private final ParameterTool parameterTool;

    public PriceListDetailsDAO(ParameterTool tool) {
        this.parameterTool = tool;
    }

    @Override
    public List<PriceListDetails> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        int i = 0;
        while (i < 100) {
            try {
                return queryList(pageSize, params);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    log.error("PriceListDetailsDAO InterruptedException:{}", e1);
                }
            }
            i ++;
        }
        return Collections.emptyList();
    }

    private List<PriceListDetails> queryList(int pageSize, Map<String, Object> params) throws Exception {
        //创建连接
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<PriceListDetails> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PRICE_URL),
                    parameterTool.get(MYSQL_DATABASE_PRICE_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PRICE_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL + ORDER_SQL);
            ps.setLong(1, (long) params.get("organizationId"));
            ps.setString(2, (String) params.get("listId"));
            ps.setLong(3, (long) params.get("id"));
            ps.setInt(4, pageSize);

//            ps.setInt(3, pageSize);

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new PriceListDetails().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            log.error("PriceListDetailsDAO pageQuery params:{} error:{}", params, e);
            throw new Exception(e);
        }
        finally {
            // long end = System.currentTimeMillis();
            log.info("PriceListDetailsDAO pageQuery params: {}, size: {}", params, list.size());
            MysqlUtils.close(connection, ps, rs);
        }
    }

    public PriceListDetails queryOne(Map<String, Object> params) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PriceListDetails priceListDetails = null;

        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PRICE_URL),
                    parameterTool.get(MYSQL_DATABASE_PRICE_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PRICE_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL_01);
            ps.setLong(1, (long) params.get("organizationId"));
            ps.setString(2, (String) params.get("listId"));
            ps.setString(3, (String) params.get("internalId"));

            rs = ps.executeQuery();

            while (rs.next()) {
                priceListDetails = new PriceListDetails().convert(rs);
            }
            return priceListDetails;
        }
        catch (Exception e) {
            log.error("PriceListDetails queryOne:{} error:{}", params, e);
        }
        finally {
            log.info("PriceListDetails queryOne params:{}", params);
            MysqlUtils.close(connection, ps, rs);
        }

        return priceListDetails;

    }
}
