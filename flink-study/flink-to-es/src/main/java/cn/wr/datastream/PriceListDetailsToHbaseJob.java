package cn.wr.datastream;

import cn.wr.filter.PriceListDetailsFilter;
import cn.wr.flatmap.PriceListDetailsFlatMap;
import cn.wr.model.BasicModel;
import cn.wr.model.CanalDataModel;
import cn.wr.model.price.PriceListDetails;
import cn.wr.sink.PriceListDetailsSink;
import cn.wr.utils.ExecutionEnvUtil;
import cn.wr.utils.KafkaUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 实时将price_list_details到hbase 启动类
 * @author RWang
 * @Date 2022/8/16
 */

public class PriceListDetailsToHbaseJob {

    private static final Logger logger = LoggerFactory.getLogger(PriceListDetailsToHbaseJob.class);

    public static void main(String[] args) {

        logger.info("PriceListDetailsToHbaseJob start ....");

        try {
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                logger.info("InitialPriceListDetailsToHbaseJob parameterTool is null");
                return;
            }
            // 获取环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.getEnv(parameterTool);
            DataStreamSource<CanalDataModel> goodsCenterKafkaCanalSource = KafkaUtil.createGoodsCenterKafkaCanalSource(env);
            DataStream<CanalDataModel> canalDataModel = goodsCenterKafkaCanalSource.filter(new PriceListDetailsFilter());
            DataStream<BasicModel<PriceListDetails>> basicModelDataStream = canalDataModel.flatMap(new PriceListDetailsFlatMap());
            basicModelDataStream.keyBy(item->item.getData().getDetailKey())
                    .addSink(new PriceListDetailsSink());

            env.execute("[PRD][HBASE] - price_list_details_cache(increment)");
        }catch (Exception e){
            logger.error("PriceListDetailsToHbaseJob error:{}",e);
        }

    }
}
