package cn.wr.sink;

import cn.wr.constants.PropertiesConstants;
import cn.wr.model.AbnormalStockData;
import cn.wr.model.ResultDTO;
import cn.wr.utils.DingTalkUtil;
import cn.wr.utils.HttpUtil;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author RWang
 * @Date 2022/8/9
 */

public class AbnormalDataRePushSink extends RichSinkFunction<AbnormalStockData> {


    private static final Logger logger = LoggerFactory.getLogger(AbnormalDataRePushSink.class);
    private static final long serialVersionUID = 517937245377337131L;
    private static final String MERCHANT_ID = "merchantId";
    private static final String STORE_ID = "storeId";
    private static final String INTERNAL_ID = "internalId";
    private static String STOCK_SECRET = null;
    private static String STOCK_DING_URL = null;
    private static String STOCK_TITLE = null;
    private static String STOCK_URL = null;
    private static String SHELVE_URL = null;
    private static final String MSG_CONTENT = "#### %s \n\n > %s \n\n";

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        ParameterTool tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        STOCK_DING_URL = tool.get(PropertiesConstants.STOCK_ABNORMAL_BOT_URL);
        STOCK_TITLE= tool.get(PropertiesConstants.STOCK_ABNORMAL_BOT_TITLE);
        STOCK_SECRET= tool.get(PropertiesConstants.STOCK_ABNORMAL_BOT_SECRET);
        STOCK_URL= tool.get(PropertiesConstants.HTTP_STOCK_URL);
        SHELVE_URL= tool.get(PropertiesConstants.HTTP_SHELVE_URL);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void invoke(AbnormalStockData value, Context context) throws Exception {

        // 过来判断数据类型 2 库存 3 上下架 4，全部
        // 数据先钉钉报警
        sendDingTalk(value);
        List<Map<String, Object>> maps = new ArrayList<>();
        ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();
        map.put(MERCHANT_ID,String.valueOf(value.getMerchantId()));
        map.put(STORE_ID,String.valueOf(value.getStoreId()));
        map.put(INTERNAL_ID,value.getInternalId());
        maps.add(map);
        // 重新推送
        String result = null;
        ResultDTO resultDTO= null;
        if (value.getIsStockOrStatus()==2){
            // 重新推送库存
             result = HttpUtil.doPost(STOCK_URL, maps);
             resultDTO = JSON.parseObject(result, ResultDTO.class);
            if (null == resultDTO || resultDTO.getErrno() != 0) {
                logger.error("###  send stock quantity error reqDTO:{}, result:{}",
                        JSON.toJSONString(maps), result);
            }

        }
        if (value.getIsStockOrStatus()==3){
            // 重新推送上下架
             result = HttpUtil.doPost(SHELVE_URL, maps);
            if (null == resultDTO || resultDTO.getErrno() != 0) {
                logger.error("###  send stock state error reqDTO:{}, result:{}",
                        JSON.toJSONString(maps), result);
            }
        }
        if (value.getIsStockOrStatus()==4){
            // 全部重推
            result = HttpUtil.doPost(STOCK_URL,maps);
            if (null == resultDTO || resultDTO.getErrno() != 0) {
                logger.error("###  send stock quantity error reqDTO:{}, result:{}",
                        JSON.toJSONString(maps), result);
            }
            result = HttpUtil.doPost(SHELVE_URL,maps);
            if (null == resultDTO || resultDTO.getErrno() != 0) {
                logger.error("###  send stock state error reqDTO:{}, result:{}",
                        JSON.toJSONString(maps), result);
            }
        }

    }


    /**
     * 发送钉钉
     * @param stockData stockData
     */
    public void sendDingTalk(AbnormalStockData stockData){
        String content = String.format(MSG_CONTENT,STOCK_TITLE, JSON.toJSONString(stockData));
        DingTalkUtil.sendDingDingWithSecret(STOCK_DING_URL,STOCK_SECRET,STOCK_TITLE,content);
    }
}
