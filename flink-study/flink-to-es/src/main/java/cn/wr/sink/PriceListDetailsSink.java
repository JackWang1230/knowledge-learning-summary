package cn.wr.sink;

import cn.wr.hbase.HbaseService;
import cn.wr.model.BasicModel;
import cn.wr.model.price.PriceListDetails;
import cn.wr.utils.HbaseUtil;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static cn.wr.constants.PropertiesConstants.*;

/**
 * price_list_details 数据实时变更记录同步hbase
 * @author RWang
 * @Date 2022/8/16
 */

public class PriceListDetailsSink extends RichSinkFunction<BasicModel<PriceListDetails>> {
    private static final long serialVersionUID = -1091079171848788100L;

    private static final Logger logger = LoggerFactory.getLogger(PriceListDetailsSink.class);
    private static final String HBASE_PREFIX = "hbase_";

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
    public void invoke(BasicModel<PriceListDetails> value, Context context) throws Exception {

        try {
            if(Objects.nonNull(value)){
                dealHbase(value);
            }
        }
        catch (Exception e){
            logger.error("write price_list_details into hbase error:{} ",e);
        }


    }

    /**
     *  数据实时写入hbase
     * @param value value
     */
    public void dealHbase(BasicModel<PriceListDetails> value){

        switch (value.getOperate().toUpperCase()){
            case DELETE:
            case UPDATE_DELETE:
                hbaseService.delete(HBASE_PREFIX+value.getTableName(),value.getData().getDetailKey());
                break;
            case INSERT:
            case UPDATE:
                hbaseService.put(HBASE_PREFIX+value.getTableName(),value.getData().getDetailKey(),
                        JSON.toJSONString(value.getData()));
        }


    }
}
