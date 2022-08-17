package cn.wr.datastream;

import cn.wr.datasource.AbnormalStockSource;
import cn.wr.flatmap.ComparePartnerAndGoodsCenterFlatMap;
import cn.wr.model.AbnormalStock;
import cn.wr.model.AbnormalStockData;
import cn.wr.sink.AbnormalDataRePushSink;
import cn.wr.utils.ExecutionEnvUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.constants.PropertiesConstants.*;

/**
 * @author RWang
 * @Date 2022/8/5
 */

public class CompareAbnormalStockJob {

    private static final Logger logger = LoggerFactory.getLogger(CompareAbnormalStockJob.class);

    public static void main(String[] args) {

        try {
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                logger.info("StockDataCheckJob parameterTool is null");
                return;
            }
            /* 基础环境配置加载 */
            StreamExecutionEnvironment env = ExecutionEnvUtil.getEnv(parameterTool);
            env.setParallelism(parameterTool.getInt(STREAM_GLOBAl_PARALLELISM));

            DataStreamSource<AbnormalStock> abnormalStockDataStreamSource = env.addSource(new AbnormalStockSource());

            // 处理逻辑
            DataStream<AbnormalStockData> abnormalResult = abnormalStockDataStreamSource
                    .flatMap(new ComparePartnerAndGoodsCenterFlatMap());

            // 发送数据
            abnormalResult.keyBy(stock-> stock.getMerchantId()+HOR_LINE+stock.getStoreId()+HOR_LINE+stock.getInternalId())
                    .addSink(new AbnormalDataRePushSink()).name("rePush data");

            env.execute("[PRD][POLABDB] - abnormal stock data second delay msg v2");

        } catch (Exception e){
            e.printStackTrace();
            logger.error("CompareAbnormalStockJob error:{0}",e);
        }
    }
}
