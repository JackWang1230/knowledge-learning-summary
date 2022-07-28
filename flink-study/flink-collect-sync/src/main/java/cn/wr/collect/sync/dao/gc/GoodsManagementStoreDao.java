package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.model.gc.GoodsManagementStore;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class GoodsManagementStoreDao {

    private static final long serialVersionUID = -20572691323952257L;

    private static final Logger log = LoggerFactory.getLogger(ConfigSkuSourceDAO.class);

    private final ParameterTool parameterTool;

    private static final String SELECT_SQL = "select `stock_no`, `sku_no`, `merchant_id`, `store_id`, `internal_id` " +
            " from `" + CommonConstants.SCHEMA_GOODS_CENTER_TIDB + "`.`gc_goods_management_store` ";

    private static final String WHERE_SQL = " where stock_no = ? and  channel=1 and sale_state=1 ";

    public GoodsManagementStoreDao(ParameterTool tool) {
        this.parameterTool = tool;
    }

    public GoodsManagementStore queryOne(String stockNo) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        GoodsManagementStore goodsManagementStore = null;

        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_DATACENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_DATACENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_DATACENTER_PASSWORD));


            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL);
            ps.setString(1, stockNo);


            rs = ps.executeQuery();

            while (rs.next()) {
                goodsManagementStore = new GoodsManagementStore().convert(rs);
            }
            return goodsManagementStore;
        }
        catch (Exception e) {
            log.error("GoodsManagementStoreDao queryOne params:{} error:{}", stockNo, e);
        }
        finally {
            log.info("GoodsManagementStoreDao queryOne params:{}", stockNo);
            MysqlUtils.close(connection, ps, rs);
        }

        return goodsManagementStore;

    }
}
