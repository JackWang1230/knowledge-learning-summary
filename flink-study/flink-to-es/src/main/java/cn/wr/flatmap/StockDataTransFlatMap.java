package cn.wr.flatmap;

import cn.wr.model.StockData;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static cn.wr.constants.PropertiesConstants.HOR_LINE;

/**
 * 区分接收的数据是上下架还是 数量
 * @author RWang
 * @Date 2022/7/20
 */

public class StockDataTransFlatMap extends RichFlatMapFunction<StockData, Tuple2<String,StockData>> {
    private static final Logger logger = LoggerFactory.getLogger(StockDataTransFlatMap.class);
    private static final long serialVersionUID = -8762693711226269951L;

    @Override
    public void flatMap(StockData value, Collector<Tuple2<String, StockData>> collector) {

        if (Objects.isNull(value) || StringUtils.isBlank(value.getInternalId())
              || value.getStoreId()==0 || value.getMerchantId()== 0) {
            logger.info("stock Data is error:{}",value.toString());
            return;
        }
        // 为了区分是库存数量 和 库存上下架状态 单独判断 状态字段
//        String flag= "";
        // 推送过来是上下架状态
//        if (StringUtils.isBlank(value.getQuantity())){
//            flag = "state";
//        }else {
//            flag= "quantity";
//        }
        String flag = StringUtils.isBlank(value.getQuantity())? "state":"quantity";

        String stockNo= value.getMerchantId()+HOR_LINE+value.getStoreId()+HOR_LINE+value.getInternalId()+HOR_LINE+flag;
        collector.collect(Tuple2.of(stockNo,value));
    }
}
