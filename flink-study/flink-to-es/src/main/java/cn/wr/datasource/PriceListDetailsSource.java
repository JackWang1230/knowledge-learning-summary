package cn.wr.datasource;

import cn.wr.model.price.PriceListDetails;
import cn.wr.model.price.PriceListDetailsInitialEvent;
import cn.wr.utils.DataBasesUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.wr.constants.PropertiesConstants.DATA_PAGE_SIZE;

/**
 * 数据库直接查询price_list_details 表数据用于初始化
 * @author RWang
 * @Date 2022/8/16
 */

public class PriceListDetailsSource extends RichSourceFunction<PriceListDetailsInitialEvent> {
    private static final long serialVersionUID = -5609842505707114483L;

    private ParameterTool parameterTool;
    private static boolean RUNNING = true;
    private static final Logger logger = LoggerFactory.getLogger(PriceListDetailsSource.class);

    private static final String SELECT_SQL = " select `id`,`tenant_id`,`organization_id`,`list_id`" +
            ",`internal_id`,`sku_price`,`original_price`" +
            ",`member_price`,`min_price`,`max_price`,`channel`,`sub_channel`,`list_status`,`source`,`detail_key`," +
            "  (CASE `gmt_created` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmt_created` END) AS `gmt_created` ," +
            "  (CASE `gmt_updated` WHEN '0000-00-00 00:00:00' THEN NULL ELSE `gmt_updated` END) AS `gmt_updated` " +
            " from `cn_udc_gsbp_price`.`price_list_details`";
    private static final String WHERE_SQL = " where id > ? ";
    private static final String ORDER_SQL = " order by id asc limit ?; ";


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
    }

    @Override
    public void run(SourceContext<PriceListDetailsInitialEvent> ctx) throws Exception {

         int pageSize = parameterTool.getInt(DATA_PAGE_SIZE);
//        int pageSize = 100;
        String tableName = "price_list_details";
        Map<String, Object> params = new HashMap<>();
        params.put("id", 0L);
        while (RUNNING){

            List<PriceListDetails> priceListDetailsList = queryPriceListDetailsById(pageSize, params);
            logger.info("PriceListDetailsSource,param:{},SchemeInfoSize:{}", params, priceListDetailsList.size());
            if (CollectionUtils.isNotEmpty(priceListDetailsList)){
                PriceListDetailsInitialEvent priceListDetailsInitialEvent = new PriceListDetailsInitialEvent(tableName, priceListDetailsList);
                ctx.collect(priceListDetailsInitialEvent);
            }
            if (CollectionUtils.isEmpty(priceListDetailsList) || priceListDetailsList.size() != pageSize) {
                break;
            }
            params.put("id", priceListDetailsList.get(priceListDetailsList.size() - 1).getId());

        }
    }

    @Override
    public void cancel() {

        RUNNING = false;
    }

    /**
     * 基于id 每次1w条查询数据库数据
     * @param pageSize 查询大小
     * @param params 参数
     * @return List<PriceListDetails>
     * @throws Exception Exception
     */
    public List<PriceListDetails> queryPriceListDetailsById(int pageSize, Map<String, Object> params) throws Exception {

        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<PriceListDetails> list = new ArrayList<>();
        try {
            connection = DataBasesUtil.getGoodsCenterPolarConnection(parameterTool);

            ps = connection.prepareStatement(SELECT_SQL + WHERE_SQL + ORDER_SQL);
                ps.setLong(1, (long) params.get("id"));
                ps.setInt(2, pageSize);

            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new PriceListDetails().convert(rs));
            }
            return list;
        } catch (Exception e) {
            logger.error("PriceListDetailsSource queryPriceListDetailsById params:{} error:{}", params, e);
            throw new Exception(e);
        } finally {
            logger.info("PriceListDetailsSource queryPriceListDetailsById params:{}, size:{}", params, list.size());
            DataBasesUtil.close(connection, ps,rs);
        }

    }
}
