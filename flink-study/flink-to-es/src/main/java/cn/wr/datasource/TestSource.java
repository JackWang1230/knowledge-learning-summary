package cn.wr.datasource;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

/**
 * @author : WangRui
 * @date : 2023/2/1
 */

public class TestSource extends RichParallelSourceFunction<String> {

    private int indexOfThisSubtask=0;
    private int maxId;
    private int minId;
    private int lengthq;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
         indexOfThisSubtask = getRuntimeContext().getIndexOfThisSubtask();
         int para=5;
         lengthq =10;
         maxId=10000;
         minId=10;
    }

    @Override
    public void run(SourceContext<String> ctx) throws Exception {


        // 根据当前task对应的任务并行度
    }

    @Override
    public void cancel() {

    }
}
