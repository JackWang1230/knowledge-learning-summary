package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.model.gc.ConfigSkuSource;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;

public class ConfigSkuSourceDAO {
    private static final long serialVersionUID = -20572691113952259L;

    private static final Logger log = LoggerFactory.getLogger(ConfigSkuSourceDAO.class);

    private final ParameterTool parameterTool;

    private static final String SELECT_SQL = "select `sku_no`, `merchant_id`, `control_status` " +
            " from `" + CommonConstants.SCHEMA_GOODS_CENTER_TIDB + "`.`gc_config_sku` ";

    private static final String WHERE_SQL = " where sku_no = ? ";

    public ConfigSkuSourceDAO(ParameterTool tool) {
        this.parameterTool = tool;
    }

    public ConfigSkuSource queryOne(String skuNo) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ConfigSkuSource configSkuSource = null;

        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_DATACENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_DATACENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_DATACENTER_PASSWORD));


            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL);
            ps.setString(1, skuNo);


            rs = ps.executeQuery();

            while (rs.next()) {
                configSkuSource = new ConfigSkuSource().convert(rs);
            }
            return configSkuSource;
        }
        catch (Exception e) {
            log.error("ConfigSkuSourceDAO queryOne params:{} error:{}", skuNo, e);
        }
        finally {
            log.info("ConfigSkuSourceDAO queryOne params:{}", skuNo);
            MysqlUtils.close(connection, ps, rs);
        }

        return configSkuSource;

    }

}
