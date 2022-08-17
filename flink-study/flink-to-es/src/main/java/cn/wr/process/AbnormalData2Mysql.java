package cn.wr.process;

import cn.wr.constants.PropertiesConstants;
import cn.wr.model.StockData;
import cn.wr.utils.DataBasesUtil;
import cn.wr.utils.DingTalkUtil;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.mutable.StringBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static cn.wr.constants.PropertiesConstants.HOR_LINE;
import static cn.wr.constants.SqlConstants.INSERT_ABNORMAL_STOCK_GOODS;

/**
 *  同时写入mysql
 * @author RWang
 * @Date 2022/7/22
 */

public class AbnormalData2Mysql extends ProcessFunction<StockData,StockData> {
    private static final long serialVersionUID = 3328871566061175319L;
    private static final Logger logger = LoggerFactory.getLogger(AbnormalData2Mysql.class);

    private static String STOCK_SECRET = null;
    private static String STOCK_URL = null;
    private static String STOCK_TITLE = null;
    private static final String MSG_CONTENT = "#### %s \n\n > %s \n\n";
    // private static final CopyOnWriteArrayList<StockData> stockDataList = new CopyOnWriteArrayList<>();
    private static Connection connection;
    private static PreparedStatement ps;


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        ParameterTool tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        connection = DataBasesUtil.getGoodsCenterPolarConnection(tool);
        STOCK_URL = tool.get(PropertiesConstants.STOCK_ABNORMAL_BOT_URL);
        STOCK_TITLE= tool.get(PropertiesConstants.STOCK_ABNORMAL_BOT_TITLE);
        STOCK_SECRET= tool.get(PropertiesConstants.STOCK_ABNORMAL_BOT_SECRET);
    }

    @Override
    public void close() throws Exception {
        super.close();
        DataBasesUtil.close(connection,ps);
    }

    @Override
    public void processElement(StockData stockData, Context context, Collector<StockData> collector) throws Exception {

        /** 将异常数据写入mysql */
        try {
            if (Objects.nonNull(stockData)){
                // 写入mysql 异常数据表中
                write2Mysql(stockData);
            }
        }catch (Exception e){
            logger.error("failed write into mysql:{0}",e);
        }

        // 将异常数据发送钉钉告警
       /* try {
            if (Objects.nonNull(stockData)){
                // 写入mysql 异常数据表中
                write2Mysql(stockData);
                stockDataList.add(stockData);
            }
            if (stockDataList.size()>=10){
                 sendDingTalk(stockDataList);
//                stockDataList.forEach(a-> System.out.println(a.toString()));
                stockDataList.clear();
            }
        }catch (Exception e){
            logger.error("failed send dingTalk msg");
        }*/
    }

    /**
     * 发送钉钉
     * @param stockData stockData
     */
    public void sendDingTalk(StockData stockData){
        String content = String.format(MSG_CONTENT,STOCK_TITLE, JSON.toJSONString(stockData));
        DingTalkUtil.sendDingDingWithSecret(STOCK_URL,STOCK_SECRET,STOCK_TITLE,content);
    }


    /**
     * 发送钉钉
     * @param stockDataList stockDataList
     */
    public void sendDingTalk(List<StockData> stockDataList){

        StringBuilder content = new StringBuilder();
        stockDataList.forEach(stockData -> {
            content.append(String.format(MSG_CONTENT,STOCK_TITLE, JSON.toJSONString(stockData)));
        });
        DingTalkUtil.sendDingDingWithSecret(STOCK_URL,STOCK_SECRET,STOCK_TITLE,content.toString());

    }

    /**
     *  写入mysql库
     * @param stockData stockData
     */
    public void write2Mysql(StockData stockData){

        try {
             ps = connection.prepareStatement(INSERT_ABNORMAL_STOCK_GOODS);
             ps.setString(1,stockData.getMerchantId()+HOR_LINE+stockData.getInternalId()+HOR_LINE+stockData.getStoreId());
             ps.setLong(2,stockData.getMerchantId());
             ps.setLong(3,stockData.getStoreId());
             ps.setString(4,stockData.getInternalId());
             ps.setInt(5,1);
             ps.setLong(6,stockData.getMerchantId());
             ps.setLong(7,stockData.getStoreId());
             ps.setString(8,stockData.getInternalId());
             ps.setInt(9,1);
        } catch (SQLException e) {
            logger.error("write2Mysql exception:{0}",e);
        }finally {
            DataBasesUtil.close(connection,ps);
        }


    }
}
