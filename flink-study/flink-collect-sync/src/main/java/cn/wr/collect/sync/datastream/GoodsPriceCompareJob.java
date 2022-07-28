package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.datasource.GoodsPriceCompareSource;
import cn.wr.collect.sync.model.pricecompare.PriceCompare;
import cn.wr.collect.sync.sink.PriceCompareSink;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

@Slf4j
public class GoodsPriceCompareJob {

    public static void main(String[] args) {

        try {
            // 获取参数配置
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                log.info("GoodsPriceCompareJob parameterTool is null");
                return;
            }

            // 设置执行环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            SingleOutputStreamOperator<List<PriceCompare>> priceCompare = env.addSource(new GoodsPriceCompareSource())
                    .setParallelism(1);

            priceCompare.addSink(new PriceCompareSink()).setParallelism(1);

            env.execute("GoodsPriceCompareJob");

        } catch (Exception e) {
            log.error("GoodsPriceCompareJob message: {} error:{}", e.getMessage(), e);
        }

    }
}
