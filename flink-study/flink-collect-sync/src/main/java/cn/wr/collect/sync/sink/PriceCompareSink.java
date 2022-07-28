package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.model.pricecompare.PriceCompare;
import cn.wr.collect.sync.utils.MysqlUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Objects;

import static cn.wr.collect.sync.constants.CommonConstants.SCHEMA_GOODS_CENTER_TIDB;

public class PriceCompareSink extends RichSinkFunction<List<PriceCompare>> {
    private static final long serialVersionUID = -7636189588141221300L;
    private static final Logger log = LoggerFactory.getLogger(PriceCompareSink.class);
    private ParameterTool parameterTool = null;
    private static final String UPDATE_SQL = "INSERT INTO " + SCHEMA_GOODS_CENTER_TIDB + ".`price_compare` " +
            "(`id`, `merchant_id`, `store_id`, `internal_id`, `price_sale_price`, `price_base_price`," +
            "`partner_sale_price`, `partner_base_price`) " +
            "VALUES (?,?,?,?,?,?,?,?)";

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void invoke(List<PriceCompare> priceCompares, Context context) throws Exception {
        long start = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            if (priceCompares.size() <= 0) {
                return;
            }

            connection = MysqlUtils.getConnection(parameterTool);
            ps = connection.prepareStatement(UPDATE_SQL);

            for (int i=0; i< priceCompares.size(); i++) {
                PriceCompare priceCompare = priceCompares.get(i);

                if (Objects.nonNull(priceCompare.getId())) {
                    ps.setLong(1, priceCompare.getId());
                } else {
                    ps.setNull(1, Types.NULL);
                }
                if (Objects.nonNull(priceCompare.getMerchantId())) {
                    ps.setLong(2, priceCompare.getMerchantId());
                } else {
                    ps.setNull(2, Types.NULL);
                }
                if (Objects.nonNull(priceCompare.getStoreId())) {
                    ps.setLong(3, priceCompare.getStoreId());
                } else {
                    ps.setNull(3, Types.NULL);
                }
                if (Objects.nonNull(priceCompare.getInternalId())) {
                    ps.setString(4, priceCompare.getInternalId());
                } else {
                    ps.setNull(4, Types.NULL);
                }
                if (Objects.nonNull(priceCompare.getPriceSalePrice())) {
                    ps.setBigDecimal(5, priceCompare.getPriceSalePrice());
                } else {
                    ps.setNull(5, Types.NULL);
                }
                if (Objects.nonNull(priceCompare.getPriceBasePrice())) {
                    ps.setBigDecimal(6, priceCompare.getPriceBasePrice());
                } else {
                    ps.setNull(6, Types.NULL);
                }
                if (Objects.nonNull(priceCompare.getPartnerSalePrice())) {
                    ps.setBigDecimal(7, priceCompare.getPartnerSalePrice());
                } else {
                    ps.setNull(7, Types.NULL);
                }
                if (Objects.nonNull(priceCompare.getPartnerBasePrice())) {
                    ps.setBigDecimal(8, priceCompare.getPartnerBasePrice());
                } else {
                    ps.setNull(8, Types.NULL);
                }
                ps.addBatch();

                if (i % 500 == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }

            ps.executeBatch();
            ps.clearBatch();

            log.info("PriceCompareSink sink num:{}", priceCompares.size());
        } catch (Exception e) {
            log.error("PriceCompareSink sink Exception:{}", e);
        }
        finally {
            MysqlUtils.close(connection, ps, null);
        }

        log.info("数据总量：{}, 插入用时：" + (System.currentTimeMillis() - start)+"【单位：毫秒】", priceCompares.size());
    }
}
