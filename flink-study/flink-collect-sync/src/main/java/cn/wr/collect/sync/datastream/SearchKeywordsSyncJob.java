package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.flatmap.*;
import cn.wr.collect.sync.model.*;
import cn.wr.collect.sync.sink.SearchKeywordsSink;
import cn.wr.collect.sync.utils.ESSinkUtil;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import cn.wr.collect.sync.utils.KafkaConfigUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class SearchKeywordsSyncJob {
    private static final Logger LOG = LoggerFactory.getLogger(SearchKeywordsSyncJob.class);

    /**
     * main run
     */
    public static void main(String[] args) {
        LOG.info("### SearchKeywordsSyncJob start ......");
        try {
            // 获取参数配置
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                LOG.info("### SearchKeywordsSyncJob parameterTool is null");
                return;
            }

            // 设置执行环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 接收解析binlog基础数据
            DataStreamSource<MetricEvent> data = KafkaConfigUtil.buildSearchSourceConsumer(env)
                    .setParallelism(1);

            // 拆分
            SingleOutputStreamOperator<ElasticSearchKeywords> assembledData = data.flatMap(new SearchKeywordsFlatMap())
                    .setParallelism(1);

            // 写入es
            ESSinkUtil.addSink(assembledData, new SearchKeywordsSink(), parameterTool, 1);

            env.execute("Search Keywords Binlog Sync Job");
        }
        catch (Exception e) {
            LOG.error("SearchKeywordsSyncJob error:{}", e);
        }
    }
}
