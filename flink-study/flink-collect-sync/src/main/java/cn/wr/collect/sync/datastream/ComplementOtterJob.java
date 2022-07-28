package cn.wr.collect.sync.datastream;

import cn.wr.collect.sync.datasource.MysqlSource;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.sink.MysqlSink;
import cn.wr.collect.sync.utils.ExecutionEnvUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Deprecated
public class ComplementOtterJob {
    private static final Logger log = LoggerFactory.getLogger(ComplementOtterJob.class);

    public static void main(String[] args) {
        try {
            // 初始化参数
            String proFilePath = ParameterTool.fromArgs(args).get("conf");
            final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(proFilePath);
            if (null == parameterTool) {
                log.info("parameterTool is null");
                return;
            }
            StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);
            DataStreamSource<Model> dataSource = env.addSource(new MysqlSource()).setParallelism(1);
            dataSource.addSink(new MysqlSink()).name("sink-polardb").setParallelism(1);
            env.execute("Complement Otter Job");
        }
        catch (Exception e) {
            log.info("ComplementOtterJob Exception: {}", e);
        }
    }
}
