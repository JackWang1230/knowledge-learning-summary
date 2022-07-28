package cn.wr.flatmap;

import cn.wr.hbase.HbaseService;
import cn.wr.model.StockData;
import cn.wr.model.StockGoods;
import cn.wr.utils.HbaseUtil;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.util.Objects;

import static cn.wr.constants.PropertiesConstants.HOR_LINE;


/**
 * @author RWang
 * @Date 2022/7/27
 */

public class DelayDataCompareFlatMap extends RichFlatMapFunction<Tuple2<StockData, Long>, StockData> {
    private static final long serialVersionUID = 5648831688271795466L;
    private static final Logger logger = LoggerFactory.getLogger(DelayDataCompareFlatMap.class);

    private HbaseService hbaseService;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        hbaseService = new HbaseService(HbaseUtil.getConnection(parameterTool));
    }


    @Override
    public void flatMap(Tuple2<StockData, Long> value, Collector<StockData> out) throws Exception {

        if (Objects.isNull(value.f1)) {
            return ;
        }
//        System.out.println("key:"+value.f0+" value:"+value.f1);
        String stockNo = value.f0.getMerchantId()+HOR_LINE+value.f0.getStoreId()+HOR_LINE+value.f0.getInternalId();
        StockGoods stockGoods = hbaseService.queryStockGoods(stockNo);
        // 校验字段值
        computeStock(value, stockGoods,out);
    }


    @Override
    public void close() throws Exception {
        super.close();
        if (null != hbaseService) {
            hbaseService.closeConnection();
        }
        logger.info(" ### CompareStockDataFlatMap close hbaseService");
    }

    /**
     * 校验更新时间是否在 5min 内
     * @param value
     * @param stockGoods
     * @return
     */
    public void computeStock(Tuple2<StockData, Long> value,StockGoods stockGoods,Collector<StockData> out){

        try {

            if (Objects.isNull(stockGoods)){
               out.collect(value.f0);
            }
            long gmtUpdated =  stockGoods.getGmtUpdated().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            // min=5min
            if (Math.abs(value.f1-gmtUpdated)>=600000 && value.f1<gmtUpdated){
                out.collect(value.f0);
            }
         /*   if (Math.abs(value.f1-gmtUpdated)<=300000){
                out.collect(value.f0);
            }*/
        } catch (Exception e){
            logger.error("compute stock delay error: {}",value.f0.toString());
        }


    }


}
