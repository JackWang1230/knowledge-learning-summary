package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.datasource.PlatformGoodsSource;
import cn.wr.collect.sync.model.gc.PlatformGoods;
import cn.wr.collect.sync.sink.PlatformGoodsSink;
import cn.wr.collect.sync.utils.ESSinkUtil;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import cn.wr.collect.sync.utils.PlatformRetryRequestFailureHandler;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;
import static cn.wr.collect.sync.constants.PropertiesConstants.FLINK_COLLECT_VERSION;


@Deprecated
public class PlatformGoodsTimeJob {
    private static final Logger log = LoggerFactory.getLogger(PlatformGoodsTimeJob.class);

    public static void main(String[] args) {
        log.info("### PlatformGoodsTimeJob start ......");
        try {
            // 获取参数配置
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                log.info("### PlatformGoodsTimeJob parameterTool is null");
                return;
            }

            // 设置执行环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 查询PolarDB
            DataStreamSource<PlatformGoods> timeSource = env.addSource(new PlatformGoodsSource())
                    .setParallelism(1);

            timeSource.addSink(ESSinkUtil.getSink(timeSource,
                    new PlatformGoodsSink(),
                    new PlatformRetryRequestFailureHandler(),
                    parameterTool))
                    .setParallelism(2)
                    .name("sink-elastic");

            env.execute("Platform Goods Time Job " + parameterTool.get(FLINK_COLLECT_VERSION));
        } catch (Exception e) {
            log.error("### PlatformGoodsTimeJob error:{}", e);
        }
    }
}
