package cn.wr.sink;

import cn.wr.model.GcConfigSkuStar;
import cn.wr.utils.DataBasesUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Objects;

import static cn.wr.constants.SqlConstants.UPSERT_GOODS_SKU_STAR_SQL;
import static cn.wr.constants.SqlConstants.UPSERT_GOODS_SKU_STAR_SQL1;


/**
 *
 * 连锁实物商品星级数据写入polardb
 * @author RWang
 * @Date 2022/5/12
 */

public class GoodsSkuStarSink extends RichSinkFunction<GcConfigSkuStar> {

    private static final long serialVersionUID = 7842337175975364439L;

    private static final Logger log = LoggerFactory.getLogger(GoodsSkuStarSink.class);

    private ParameterTool parameterTool = null;

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
    public void invoke(GcConfigSkuStar value, Context context) throws Exception {

        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = DataBasesUtil.getPolarConnection(parameterTool);
            ps = connection.prepareStatement(UPSERT_GOODS_SKU_STAR_SQL1);
            if (Objects.nonNull(value.getSkuNo())) {
                ps.setString(1, value.getSkuNo());
            } else {
                ps.setNull(1, Types.NULL);
            }
            ps.setLong(2,value.getMerchantId());
            ps.setInt(3,value.getIsGoodsName());
            ps.setInt(4,value.getIsApprovalNumber());
            ps.setInt(5,value.getIsTradecode());
            ps.setInt(6,value.getIsSpecName());
            ps.setInt(7,value.getIsManufacturer());
//            ps.setLong(8,value.getMerchantId());
//            ps.setInt(9,value.getIsGoodsName());
//            ps.setInt(10,value.getIsApprovalNumber());
//            ps.setInt(11,value.getIsTradecode());
//            ps.setInt(12,value.getIsSpecName());
//            ps.setInt(13,value.getIsManufacturer());
            int i = ps.executeUpdate();
            log.info("Polardb sink num:{}", i);

        }catch (Exception e){
            log.error("Polardb sink Exception:{}", e);
        }finally {
            DataBasesUtil.close(connection,ps);
        }
    }
}
