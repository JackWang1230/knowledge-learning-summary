package cn.wr.collect.sync.dao.pcommon;

import cn.wr.collect.sync.model.pricecompare.Partner;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class PartnerDAO {
    private static final long serialVersionUID = -205726916503952259L;
    private static final Logger log = LoggerFactory.getLogger(PartnerDAO.class);
    private static final String SELECT_SQL = " select s.merchant_id, s.store_id, s.group_id, p.dbname " +
            " from `partners_manage`.`partner_stores` s " +
            " left join `partners_manage`.`partners` p on p.organizationId = s.merchant_id " ;

    private static final String WHERE_SQL = " where s.merchant_id = ? and s.store_id = ? ";

    private final ParameterTool parameterTool;

    public PartnerDAO(ParameterTool tool) {
        this.parameterTool = tool;
    }

    public Partner queryOne(Map<String, Object> params) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Partner partner = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_CHAIN_URL),
                    parameterTool.get(MYSQL_DATABASE_CHAIN_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_CHAIN_PASSWORD));


            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL);
            ps.setLong(1, (long) params.get("merchantId"));
            ps.setLong(2, (long) params.get("storeId"));


            rs = ps.executeQuery();

            while (rs.next()) {
                partner = new Partner().convert(rs);
            }
            return partner;
        }
        catch (Exception e) {
            log.error("PriceListDAO queryOne params:{} error:{}", params, e);
        }
        finally {
            log.info("PriceListDAO queryOne params:{}", params);
            MysqlUtils.close(connection, ps, rs);
        }

        return partner;
    }
}
