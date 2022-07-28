package cn.wr.process;

import cn.wr.constants.PropertiesConstants;
import cn.wr.model.StockData;
import cn.wr.utils.DingTalkUtil;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.mutable.StringBuilder;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 比对完成后的异常数据发送钉钉群
 * @author RWang
 * @Date 2022/7/22
 */

public class AbnormalDataDingProcess extends ProcessFunction<StockData,StockData> {
    private static final long serialVersionUID = 3328871566061175319L;
    private static final Logger logger = LoggerFactory.getLogger(AbnormalDataDingProcess.class);

    private static String STOCK_SECRET = null;
    private static String STOCK_URL = null;
    private static String STOCK_TITLE = null;
    private static final String MSG_CONTENT = "#### %s \n\n > %s \n\n";
    private static final CopyOnWriteArrayList<StockData> stockDataList = new CopyOnWriteArrayList<>();

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        ParameterTool tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        STOCK_URL = tool.get(PropertiesConstants.STOCK_ABNORMAL_BOT_URL);
        STOCK_TITLE= tool.get(PropertiesConstants.STOCK_ABNORMAL_BOT_TITLE);
        STOCK_SECRET= tool.get(PropertiesConstants.STOCK_ABNORMAL_BOT_SECRET);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void processElement(StockData stockData, Context context, Collector<StockData> collector) throws Exception {

        // 将异常数据发送钉钉告警
        try {
            if (Objects.nonNull(stockData)){
                stockDataList.add(stockData);
            }
            if (stockDataList.size()>=20){
                 sendDingTalk(stockDataList);
//                stockDataList.forEach(a-> System.out.println(a.toString()));
                stockDataList.clear();
            }
        }catch (Exception e){
            logger.error("failed send dingTalk msg");
        }
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
}
