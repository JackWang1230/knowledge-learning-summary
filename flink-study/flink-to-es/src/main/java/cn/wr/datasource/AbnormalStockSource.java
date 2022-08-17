package cn.wr.datasource;

import cn.wr.model.AbnormalStock;
import cn.wr.utils.DataBasesUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static cn.wr.constants.PropertiesConstants.SCHEDULED_JOB_BASIC;
import static cn.wr.constants.SqlConstants.SELECT_ABNORMAL_STOCK;
import static cn.wr.constants.SqlConstants.UPDATE_ABNORMAL_STOCK;

/**
 * @author RWang
 * @Date 2022/8/5
 */

public class AbnormalStockSource extends RichSourceFunction<AbnormalStock> {
    private static final long serialVersionUID = -1831595614793311382L;

    private static final Logger logger = LoggerFactory.getLogger(AbnormalStockSource.class);

    /* 5min */
    private static long TIME_DIFFERENCE_SECOND = 300;
    private static boolean EXIT_FLAG = true;
    private ParameterTool parameterTool;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        TIME_DIFFERENCE_SECOND = parameterTool.getLong(SCHEDULED_JOB_BASIC);
    }

    @Override
    public void close() throws Exception {
        super.close();
        EXIT_FLAG = false;
    }

    @Override
    public void cancel() {

        logger.info("AbnormalStockSource has been canceled");
        EXIT_FLAG = false;
    }

    @Override
    public void run(SourceContext<AbnormalStock> collector) {

        while (EXIT_FLAG) {

            Connection polarConnection = DataBasesUtil.getGoodsCenterPolarConnection(parameterTool);
            PreparedStatement ps = null;
            try {
                while (EXIT_FLAG) {
                    ps = polarConnection.prepareStatement(SELECT_ABNORMAL_STOCK);
                    CopyOnWriteArrayList<AbnormalStock> abnormalStocks = new CopyOnWriteArrayList<>();
                    ObjectMapper objectMapper = new ObjectMapper();
                    ResultSet rs = ps.executeQuery();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    // stored this resultSet object into map ,then convert jsonString into object from this map
                    Map<String, Object> rowData = new HashMap<>();
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            rowData.put(metaData.getColumnLabel(i), rs.getObject(i));
                        }
                        collector.collect(objectMapper.convertValue(rowData, AbnormalStock.class));
                        abnormalStocks.add(objectMapper.convertValue(rowData, AbnormalStock.class));
                    }
                    ps = polarConnection.prepareStatement(UPDATE_ABNORMAL_STOCK);
                    if (CollectionUtils.isEmpty(abnormalStocks)) {
                        break;
                    }
                    for (int i = 0; i < abnormalStocks.size(); i++) {
                        AbnormalStock abnormalStock = abnormalStocks.get(i);
                        if (Objects.nonNull(abnormalStock.getStockNo())) {
                            ps.setString(1, abnormalStock.getStockNo());
                        }
                        ps.addBatch();
                        if (i % 500 == 0) {
                            ps.executeBatch();
                            ps.clearBatch();
                        }
                    }
                    ps.executeBatch();
                    ps.clearBatch();
                    abnormalStocks.clear();
                    if (CollectionUtils.isEmpty(abnormalStocks)) {
                        break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DataBasesUtil.close(polarConnection, ps);
            }
            try {
                Thread.sleep(TIME_DIFFERENCE_SECOND * 1000);
            } catch (InterruptedException e) {
                logger.error("AbnormalStockSource InterruptedException:{0}", e);
                EXIT_FLAG = false;
                return;
            }

        }

    }
}
