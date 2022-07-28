package cn.wr.collect.sync.dao.time;

import cn.wr.collect.sync.model.gc.PlatformGoods;
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

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_GOODS_CENTER_TIDB;
import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class QueryPlatformGoodsDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryStandardGoodsDao.class);
    private static final String SELECT_SQL_01 =  "SELECT `id`, `merchantId`, `storeId`, `goodsInternalId`, `channel`, " +
            "`status`, " +
            " (CASE `gmtCreated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtCreated` END) AS `gmtCreated`, " +
            " (CASE `gmtUpdated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmtUpdated` END) AS `gmtUpdated` " +
            "FROM `" + SCHEMA_GOODS_CENTER_TIDB + "`.`platform_goods` ";
    private static final String WHERE_01 = " where id > ? and gmtUpdated > ? order by id asc limit ?";

    private ParameterTool parameterTool;

    public QueryPlatformGoodsDao(ParameterTool parameterTool) {
        this.parameterTool = parameterTool;
    }

    public List<PlatformGoods> query(Map<String, Object> params) {
        int i = 0;
        while (i < 100) {
            try {
                return queryList(params);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("### QueryPlatformGoodsDao InterruptedException:{}", e1);
                }
            }
            i ++;
        }
        return Collections.emptyList();
    }

    private List<PlatformGoods> queryList(Map<String, Object> params) throws Exception {
        List<PlatformGoods> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = MysqlUtils.retryConnection(parameterTool.get(MYSQL_DATABASE_GOODSCENTER_URL),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_USERNAME),
                    parameterTool.get(MYSQL_DATABASE_GOODSCENTER_PASSWORD));

            ps = connection.prepareStatement(SELECT_SQL_01 + WHERE_01);
            ps.setObject(1, params.get("id"));
            ps.setObject(2, params.get("startTime"));
            ps.setObject(3, params.get("pageSize"));

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new PlatformGoods().convert(rs));
            }

            return list;
        }
        catch (Exception e) {
            LOGGER.error("### QueryPlatformGoodsDao queryList params:{} Exception:{}", params, e);
            throw new Exception(e);
        }
        finally {
            LOGGER.info("### QueryPlatformGoodsDao queryList params:{} size:{}", params, list.size());
            MysqlUtils.close(connection, ps, rs);
        }
    }

}
