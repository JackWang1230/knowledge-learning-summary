package cn.wr.sink;

import cn.wr.hbase.HbaseService;
import cn.wr.model.price.PriceListDetails;
import cn.wr.model.price.PriceListDetailsInitialEvent;
import cn.wr.utils.HbaseUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * price_list_details 数据批量写入hbase
 * @author RWang
 * @Date 2022/8/16
 */

public class InitialPriceListDetailsSink extends RichSinkFunction<PriceListDetailsInitialEvent> {
    private static final long serialVersionUID = -2720780564412644275L;

    private static final Logger Logger = LoggerFactory.getLogger(InitialPriceListDetailsSink.class);

    private HbaseService hbaseService;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        hbaseService = new HbaseService(HbaseUtil.getConnection(parameterTool));
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void invoke(PriceListDetailsInitialEvent priceListDetailsInitialEvent, Context context) throws Exception {

        String tableName = priceListDetailsInitialEvent.getTableName();
        List<PriceListDetails> priceListDetailsList = priceListDetailsInitialEvent.getPriceListDetailsList();
        hbaseService.batchPutPriceListDetails(tableName,priceListDetailsList);

    }
}
