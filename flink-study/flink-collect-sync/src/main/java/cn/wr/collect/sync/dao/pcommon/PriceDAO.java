package cn.wr.collect.sync.dao.pcommon;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.dao.price.PriceListDetailsDAO;
import cn.wr.collect.sync.model.pricecompare.Price;
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

public class PriceDAO implements QueryLimitDao<Price> {
    private static final long serialVersionUID = -205726916503952259L;
    private static final Logger log = LoggerFactory.getLogger(PriceListDetailsDAO.class);

    private static final String SELECT_SQL_01 = "select s.id, CONCAT_WS('-',s.group_id,s.goods_internal_id) as uniqueKey, s.group_id, s.goods_internal_id as internalId, s.price as salePrice, s.price as basePrice from %s.store_goods s ";

    private static final String WHERE_SQL_01 = " where s.id > ? and s.group_id = ? ";

    private static final String ORDER_SQL_01 = " order by s.id asc limit ?; ";


    private final ParameterTool parameterTool;

    public PriceDAO(ParameterTool tool) {
        this.parameterTool = tool;
    }


    @Override
    public List<Price> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        int i = 0;
        while (i < 100) {
            try {
                return queryList(pageSize, params);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    log.error("PriceDAO InterruptedException:{}", e1);
                }
            }
            i ++;
        }
        return Collections.emptyList();
    }

    private List<Price> queryList(int pageSize, Map<String, Object> params) throws Exception {
        //创建连接
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Price> list = new ArrayList<>();
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_CHAIN_URL),
                    parameterTool.get(MYSQL_DATABASE_CHAIN_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_CHAIN_PASSWORD));

            String sql = String.format(SELECT_SQL_01, params.get("dbName"));


            ps = connection.prepareStatement(sql + WHERE_SQL_01 + ORDER_SQL_01);
            ps.setLong(1, (Long) params.get("id"));
            ps.setString(2, (String) params.get("groupId"));
            ps.setInt(3, pageSize);

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Price().convert(rs));
            }
            return list;
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("PriceDAO pageQuery params:{} error:{}", params, e);
            throw new Exception(e);
        }
        finally {
            log.info("PriceDAO pageQuery params: {}, size: {}", params, list.size());
            MysqlUtils.close(connection, ps, rs);
        }
    }
}
