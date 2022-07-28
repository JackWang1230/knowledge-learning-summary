package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.datasource.CompareGoodsSource;
import cn.wr.collect.sync.flatmap.CompareGoodsFlatMap;
import cn.wr.collect.sync.model.partner.PartnerGoods;
import cn.wr.collect.sync.sink.CompareGoodsSink;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Deprecated
public class CompareGoodsJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicDataInitJob.class);

    public static void main(String[] args) {
        LOGGER.info("### CompareGoodsJob run...");
        try {
            // 初始化参数
            String proFilePath = ParameterTool.fromArgs(args).get("conf");

            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);

            if (null == parameterTool) {
                LOGGER.info("### CompareGoodsJob parameterTool is null");
                return;
            }
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 查询dbId
            DataStreamSource<Integer> storeData = env.addSource(new CompareGoodsSource())
                    .setParallelism(1);

            // 查询hbase partner_goods
            SingleOutputStreamOperator<PartnerGoods> hbaseData = storeData.flatMap(new CompareGoodsFlatMap())
                    .setParallelism(1);

            // 比对tidb partner_goods 数据
            hbaseData.addSink(new CompareGoodsSink())
                    .setParallelism(1);

            env.execute("Compare Goods Job");
        }
        catch (Exception e) {
            LOGGER.error("CompareGoodsJob Exception:{}", e);
        }
    }
}
