package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.datasource.CompareStoreGoodsSource;
import cn.wr.collect.sync.flatmap.*;
import cn.wr.collect.sync.model.*;
import cn.wr.collect.sync.model.partner.PartnerStoreGoods;
import cn.wr.collect.sync.sink.CompareStoreGoodsSink;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;


@Deprecated
public class CompareStoreGoodsJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicDataInitJob.class);

    public static void main(String[] args) {
        LOGGER.info("### CompareStoreGoodsJob run...");
        try {
            // 初始化参数
            String proFilePath = ParameterTool.fromArgs(args).get("conf");

            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);

            if (null == parameterTool) {
                LOGGER.info("### CompareStoreGoodsJob parameterTool is null");
                return;
            }
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 查询门店
            DataStreamSource<CompareData> storeData = env.addSource(new CompareStoreGoodsSource())
                    .setParallelism(1);

            // 查询hbase partner_store_goods
            SingleOutputStreamOperator<PartnerStoreGoods> hbaseData = storeData.flatMap(new CompareStoreGoodsFlatMap())
                    .setParallelism(1);

            // 比对tidb partner_store_goods数据
            hbaseData.addSink(new CompareStoreGoodsSink())
                    .setParallelism(1);

            env.execute("Compare Store Goods Job");
        }
        catch (Exception e) {
            LOGGER.error("CompareStoreGoodsJob Exception:{}", e);
        }
    }
}
