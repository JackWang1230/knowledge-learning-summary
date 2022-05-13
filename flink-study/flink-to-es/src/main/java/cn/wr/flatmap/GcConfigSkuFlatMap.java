package cn.wr.flatmap;


import cn.wr.model.CanalDataModel;
import cn.wr.model.GcConfigSkuStar;
import cn.wr.utils.DataBasesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.Collector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.wr.constants.SqlConstants.MANUFACTURER_SQL;


/**
 * 组装数据 拼接连锁实体商品星级数据
 * @author RWang
 * @Date 2022/5/12
 */

public class GcConfigSkuFlatMap extends RichFlatMapFunction<CanalDataModel, GcConfigSkuStar> {

    private static final long serialVersionUID = 6472986397086050699L;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static String UPDATE ="UPDATE";
    private final static String INSERT ="INSERT";
    private final static int ZERO = 0;
    private final static int ONE = 1;
    private final static String APPROVAL_NUMBER_REGEX="(^[\u4e00-\u9fa5]+[A-Za-z]+[0-9]+$)|(^[A-Za-z]+[0-9]+$)|(^[\u4e00-\u9fa5]+[0-9]+$)";
    private final static String TRADE_CODE_REGEX="([0-9]{8,10})|([0-9]{12,14})";

    @Override
    public void flatMap(CanalDataModel value, Collector<GcConfigSkuStar> collector) throws Exception {

        if (value.getType().toUpperCase().equals(UPDATE) || value.getType().toUpperCase().equals(INSERT)){
            List<Object> dataList = value.getData();
            for (Object data : dataList) {
                GcConfigSkuStar gcConfigSkuStar = objectMapper.convertValue(data, GcConfigSkuStar.class);
                if (gcConfigSkuStar.getGoodsType()== ONE){
                    gcConfigSkuStar.setIsGoodsName(StringUtils.isBlank(gcConfigSkuStar.getSourceName())?ZERO:ONE);
                    gcConfigSkuStar.setIsApprovalNumber(isApprovalNumber(gcConfigSkuStar.getApprovalNumber())?ONE:ZERO);
                    gcConfigSkuStar.setIsTradecode(isTradeCode(gcConfigSkuStar.getBarcode())?ONE:ZERO);
                    gcConfigSkuStar.setIsSpecName(StringUtils.isBlank(gcConfigSkuStar.getSpecName())?ZERO:ONE);
                    gcConfigSkuStar.setIsManufacturer(isManufacturer(gcConfigSkuStar.getSkuNo())?ONE:ZERO);
                    // System.out.println(gcConfigSkuStar.toString());
                    collector.collect(gcConfigSkuStar);

                }

            }
        }

    }


    /**
     * 校验批准文号
     * @param approvalNumber
     * @return
     */
    public boolean isApprovalNumber(String approvalNumber){

        if (StringUtils.isBlank(approvalNumber) ||
                approvalNumber.length()>16 ||
                approvalNumber.length()<13 ){
            return false;
        }
        Pattern compile = Pattern.compile(APPROVAL_NUMBER_REGEX);
        Matcher matcher = compile.matcher(approvalNumber);
        while (matcher.find()){
            return true;
        }
        return false;
    }
    /**
     * 校验条码号
     * @param tradeCode
     * @return
     */
    public boolean isTradeCode(String tradeCode){

        if (StringUtils.isBlank(tradeCode)){
            return false;
        }
        Pattern compile = Pattern.compile(TRADE_CODE_REGEX);
        Matcher matcher = compile.matcher(tradeCode);
        while (matcher.find()){
            return true;
        }
        return false;
    }

    /**
     * 校验生产厂家
     * @param skuNo
     * @return
     */
    public boolean isManufacturer(String skuNo) {
        Connection connection=null;
        PreparedStatement ps=null;

        try {
            ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
            connection = DataBasesUtil.getMysqlConnection(parameterTool);
            ps = connection.prepareStatement(MANUFACTURER_SQL);
            ps.setString(1, skuNo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String string = rs.getString(1);
                return !StringUtils.isBlank(string);
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            DataBasesUtil.close(connection,ps);
        }
        return false;

    }
}
