package cn.wr.datastream;

import cn.wr.datasource.PriceListDetailsSource;
import cn.wr.sink.InitialPriceListDetailsSink;
import cn.wr.utils.ExecutionEnvUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.constants.PropertiesConstants.STREAM_SINK_PARALLELISM;

/**
 * 初始化price_list_details到hbase 启动类
 * @author RWang
 * @Date 2022/8/16
 */

public class InitialPriceListDetailsToHbaseJob {

    private static final Logger logger = LoggerFactory.getLogger(InitialPriceListDetailsToHbaseJob.class);

    public static void main(String[] args) {

        try {
            // 初始化参数
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                logger.info("InitialPriceListDetailsToHbaseJob parameterTool is null");
                return;
            }
            // 获取环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.getEnv(parameterTool);
            // 查询数据
            env.addSource(new PriceListDetailsSource())
                    .setParallelism(2)
                    .addSink(new InitialPriceListDetailsSink())
                    .setParallelism(parameterTool.getInt(STREAM_SINK_PARALLELISM,6))
                    .name("sink-hbase-price-list-details-initial");

            // 数据写入hbase
            env.execute("[PRD][HBASE] - price_list_details_cache(initial)");

    } catch (Exception e){
            logger.error("InitialPriceListDetailsToHbaseJob error:{}",e);
        }
    }
}
