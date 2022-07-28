package cn.wr.collect.sync.dao.price;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;

public class PriceCompareDAO {
    private static final long serialVersionUID = -205726916503122259L;
    private static final Logger log = LoggerFactory.getLogger(PriceCompareDAO.class);

    private final ParameterTool parameterTool;

    private static final String DELETE_SQL =  " delete from `" + CommonConstants.SCHEMA_GOODS_CENTER_TIDB + "`.`price_compare` ";

    public PriceCompareDAO(ParameterTool tool) {
        this.parameterTool = tool;
    }


    public boolean delete(String[] merchantIds) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_PRICE_COMPARE_URL),
                    parameterTool.get(MYSQL_DATABASE_PRICE_COMPARE_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_PRICE_COMPARE_PASSWORD));

            StringBuffer stringBuffer = new StringBuffer("?");
            for (int i = 0; i <= merchantIds.length - 2; i++) {
                stringBuffer.append(",?");
            }

            ps = connection.prepareStatement(DELETE_SQL + " where merchant_id in (".concat(stringBuffer.toString()).concat(")"));

            for (int i = 0; i <= merchantIds.length - 1; i++) {
                ps.setLong(i + 1, Long.parseLong(merchantIds[i]));
            }

            int num=ps.executeUpdate();

            if (num>0){
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("PriceCompareDAO delete params:{} error:{}", merchantIds, e);
        }
        finally {
            log.info("PriceCompareDAO delete params:{}", merchantIds);
            MysqlUtils.close(connection, ps, rs);
        }

        return false;
    }

}
