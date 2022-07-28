package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.datasource.SearchKeywordsInitSource;
import cn.wr.collect.sync.flatmap.SearchKeywordsFlatMap;
import cn.wr.collect.sync.model.ElasticSearchKeywords;
import cn.wr.collect.sync.model.MetricEvent;
import cn.wr.collect.sync.sink.SearchKeywordsSink;
import cn.wr.collect.sync.utils.ESSinkUtil;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class SearchKeywordsInitJob {
    private static final Logger LOG = LoggerFactory.getLogger(SearchKeywordsInitJob.class);

    /**
     * main run
     */
    public static void main(String[] args) {
        LOG.info("### SearchKeywordsInitJob start ......");
        try {
            // 获取参数配置
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                LOG.info("### SearchKeywordsInitJob parameterTool is null");
                return;
            }

            // 设置执行环境
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

            // 接收解析binlog基础数据
            DataStreamSource<MetricEvent> data = env.addSource(new SearchKeywordsInitSource())
                    .setParallelism(1);

            // 拆分
            SingleOutputStreamOperator<ElasticSearchKeywords> assembledData = data.flatMap(new SearchKeywordsFlatMap())
                    .setParallelism(1);

            // 写入es
            ESSinkUtil.addSink(assembledData, new SearchKeywordsSink(), parameterTool, 1);

            env.execute("Search Keywords Init Job");
        }
        catch (Exception e) {
            LOG.error("SearchKeywordsInitJob error:{}", e);
        }
    }
}
