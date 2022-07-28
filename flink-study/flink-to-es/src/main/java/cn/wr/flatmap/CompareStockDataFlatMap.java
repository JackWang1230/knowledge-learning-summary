package cn.wr.flatmap;

import cn.wr.hbase.HbaseService;
import cn.wr.model.StockData;
import cn.wr.model.StockGoods;
import cn.wr.utils.HbaseUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Objects;

import static cn.wr.constants.PropertiesConstants.HOR_LINE;

/**
 * 对比库存的 数量及上下架数据和商品中心数据是否一致
 *
 * @author RWang
 * @Date 2022/7/20
 */

public class CompareStockDataFlatMap extends RichFlatMapFunction<Tuple2<String, StockData>, Tuple3<String,StockData,Long>> {

    private static final long serialVersionUID = -5835437497977722175L;

    private static final Logger logger = LoggerFactory.getLogger(CompareStockDataFlatMap.class);

    private HbaseService hbaseService;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        hbaseService = new HbaseService(HbaseUtil.getConnection(parameterTool));
    }

    @Override
    public void flatMap(Tuple2<String, StockData> value, Collector<Tuple3<String,StockData, Long>> out) throws Exception {

        if (Objects.isNull(value.f1)) {
            return;
        }
//        System.out.println("key:"+value.f0+" value:"+value.f1);
        // 基于hbase 查询的库存数据和 当前flink 接收到的数据进行比较
        String stockNo = value.f0.substring(0, value.f0.lastIndexOf(HOR_LINE));
//        String stockNo="dsds";
        StockGoods stockGoods = hbaseService.queryStockGoods(stockNo);
//        StockGoods stockGoods = null;
        // 校验字段值
        computeQuantity(value, stockGoods, out);
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
     * 校验kakfa接收到的数据 和 数据库中数据是否一致
     *
     * @param stockData
     * @param stockGoods
     * @param out
     */
    public void computeQuantity(Tuple2<String, StockData> stockData, StockGoods stockGoods, Collector<Tuple3<String,StockData,Long>> out) {

        // 1.数据库不存在该条记录
        if (Objects.isNull(stockGoods)) {
            out.collect(Tuple3.of(stockData.f0,stockData.f1,System.currentTimeMillis()));
        }else {
            // 2.数据库中的库存和实际库存不一致
            // 2.1 数据只推送了库存数据
            if (StringUtils.isBlank(stockData.f1.getSaleState()) && StringUtils.isNotBlank(stockData.f1.getQuantity())) {
                // 将两者同时转换成int 之后进行比较
                String kafkaQuantity = new BigDecimal(stockData.f1.getQuantity()).stripTrailingZeros().toPlainString();
                String hbaseQuantity = Objects.isNull(stockGoods.getQuantity())? "null":
                        stockGoods.getQuantity().stripTrailingZeros().toPlainString();
                if (!kafkaQuantity.equals(hbaseQuantity)) {
                    out.collect(Tuple3.of(stockData.f0,stockData.f1,System.currentTimeMillis()));
                }
            }

            // 2.2 数据只推送了上下架状态
            if (StringUtils.isNotBlank(stockData.f1.getSaleState()) && StringUtils.isBlank(stockData.f1.getQuantity())) {
                int saleState = Objects.isNull(stockData.f1.getSaleState())? 0: Integer.parseInt(stockData.f1.getSaleState());
                int hbaseSaleState = Objects.isNull(stockGoods.getSaleState()) ? 0 : stockGoods.getSaleState();
                if (!(saleState == hbaseSaleState)) {
                    out.collect(Tuple3.of(stockData.f0,stockData.f1,System.currentTimeMillis()));
                }
            }
        }

    }


}
