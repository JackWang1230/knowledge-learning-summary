package cn.wr.collect.sync.dao.gc;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.gc.BaseGoods;
import cn.wr.collect.sync.utils.MysqlUtils;
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

public class BaseGoodsDao implements QueryLimitDao<BaseGoods> {
    private static final long serialVersionUID = 8572657859445243856L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseGoodsDao.class);

    /*private static final String SELECT_SQL = "select `id`, `approval_number`, `product_name`, `spec`, `product_unit`, `product_addr`, " +
            " `approve_date`, `end_date`, `goods_type`, `domestic_foreign`, `relation_id`, `cate_one`, `cate_two`, `cate_three`, " +
            " `cate_four`, `cate_five` from "  + SCHEMA_GOODS_CENTER_TIDB +  ".`base_goods` " +
            " where id >= (select id from " + SCHEMA_GOODS_CENTER_TIDB + ".base_goods order by id asc limit ?, 1) " +
            " order by id asc limit ?;";*/
    private static final String SELECT_SQL = "select `id`, `approval_number`, `product_name`, `spec`, `product_unit`, `product_addr`, " +
            " `approve_date`, `end_date`, `goods_type`, `domestic_foreign`, `relation_id`, `cate_one`, `cate_two`, `cate_three`, " +
            " `cate_four`, `cate_five` from "  + SCHEMA_GOODS_CENTER_TIDB +  ".`base_goods` " +
            " where id > ? " +
            " order by id asc limit ?;";

    @Override
    public List<BaseGoods> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection) {
        // LOGGER.info("BaseGoods findLimit offset:{}, pageSize:{}", offset, pageSize);
        // long start = System.currentTimeMillis();

        //创建连接
        /*Connection connection = MysqlUtils.getConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));*/

        if (null == connection) return Collections.emptyList();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(SELECT_SQL);
            // ps.setLong(1, offset);
            ps.setLong(1, (long) params.get("id"));
            ps.setInt(2, pageSize);
            rs = ps.executeQuery();
            List<BaseGoods> list = new ArrayList<>(pageSize);
            while (rs.next()) {
                // `id`, `approval_number`, `product_name`, `spec`, `product_unit`, `product_addr`, `approve_date`, `end_date`,
                // `goods_type`, `domestic_foreign`, `relation_id`, `cate_one`, `cate_two`, `cate_three`, `cate_four`, `cate_five`
                BaseGoods item = new BaseGoods();
                item.setId(rs.getLong(1));
                item.setApprovalNumber(rs.getString(2));
                item.setProductName(rs.getString(3));
                item.setSpec(rs.getString(4));
                item.setProductUnit(rs.getString(5));
                item.setProductAddr(rs.getString(6));
                item.setApproveDate(rs.getString(7));
                item.setEndDate(rs.getString(8));
                item.setGoodsType(rs.getInt(9));
                item.setDomesticForeign(rs.getInt(10));
                item.setRelationId(rs.getLong(11));
                item.setCateOne(rs.getString(12));
                item.setCateTwo(rs.getString(13));
                item.setCateThree(rs.getString(14));
                item.setCateFour(rs.getString(15));
                item.setCateFive(rs.getString(16));
                list.add(item);
            }
            return list;
        }
        catch (Exception e) {
            LOGGER.error("rs:{} ps:{} connection:{}", rs, ps, connection);
            LOGGER.error("BaseGoodsDao findLimit offset:{}, pageSize:{}, error:{}", offset, pageSize, e);
        }
        finally {
            // long end = System.currentTimeMillis();
            // LOGGER.error("BaseGoodsDao findLimit offset:{}, pageSize:{}, time:{}(s)", offset, pageSize, (end - start)/1000);
            MysqlUtils.close(null, ps, rs);
        }
        return Collections.emptyList();
    }
}
