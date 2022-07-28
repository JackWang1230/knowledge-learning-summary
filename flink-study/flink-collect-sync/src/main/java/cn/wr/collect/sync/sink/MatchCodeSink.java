package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.dao.chain.MatchCodeDAO;
import cn.wr.collect.sync.dao.partner.PartnersDAO;
import cn.wr.collect.sync.model.chain.MatchCode;
import cn.wr.collect.sync.model.chain.MatchCodeDTO;
import cn.wr.collect.sync.model.partner.Partners;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.RedisPoolUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static cn.wr.collect.sync.constants.CommonConstants.CHAIN_DB_START;

public class MatchCodeSink extends RichSinkFunction<MatchCodeDTO> {
    private static final long serialVersionUID = -5406259259376943521L;
    private static final Logger log = LoggerFactory.getLogger(MatchCodeSink.class);

    private ParameterTool tool = null;
    private RedisService redisService = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        redisService = new RedisService(tool);
    }

    @Override
    public void close() throws Exception {
        super.close();
        RedisPoolUtil.closePool();
    }

    @Override
    public void invoke(MatchCodeDTO dto, Context context) throws Exception {
        log.info("MatchCodeJob Kafka:{}", JSON.toJSONString(dto));
        if (Objects.isNull(dto) || StringUtils.isBlank(dto.getBarcode()) || StringUtils.isBlank(dto.getSkuNo())) {
            log.error("MatchCodeSink dto is not valid: {}", JSON.toJSONString(dto));
            return;
        }
        // 查询连锁信息
        Integer merchantId = this.subMerchantId(dto.getSkuNo());
        PartnersDAO partnersDAO = new PartnersDAO(tool);
        Partners partners = partnersDAO.findByMerchantId(merchantId);

        if (Objects.isNull(partners)) {
            log.error("MatchCodeSink partners is null: {}", JSON.toJSONString(dto));
            return;
        }

        // 判断是否dtp
        Boolean isDtp = redisService.checkIsDtpStore(this.subDbId(partners.getDbname()));
        if (!isDtp) {
            log.error("MatchCodeSink is not dtp: {}", JSON.toJSONString(dto));
            return;
        }

        // 数据写入
        MatchCode matchCode = new MatchCode(partners.getType(), partners.getDbname(),
                this.subGoodsInternalId(dto.getSkuNo()), dto.getBarcode());
        MatchCodeDAO matchCodeDAO = new MatchCodeDAO(tool);
        matchCodeDAO.updateByMatchCode(matchCode);
    }

    /**
     * 切割 dbname 获取 db_id
     * @param dbname
     * @return
     */
    private Integer subDbId(String dbname) {
        if (StringUtils.isBlank(dbname) || !dbname.startsWith(CHAIN_DB_START)) {
            return null;
        }
        String dbId = dbname.substring(CHAIN_DB_START.length());
        try {
            return Integer.valueOf(dbId);
        } catch (Exception e) {
            log.error("MatchCode parse error, dbName:{}, Exception:{}", dbname, e);
        }
        return null;
    }

    /**
     * 切割 skuNo 获取 连锁id
     * @param skuNo
     * @return
     */
    private Integer subMerchantId(String skuNo) {
        if (StringUtils.isBlank(skuNo) || skuNo.indexOf(SymbolConstants.HOR_LINE) <= 0) {
            return null;
        }
        try {
            return Integer.valueOf(skuNo.substring(0, skuNo.indexOf(SymbolConstants.HOR_LINE)));
        } catch (Exception e) {
            log.error("MatchCode parse error, skuNo:{}, Exception:{}", skuNo, e);
        }
        return null;
    }

    /**
     * 切割 skuNo 获取 商品内码
     * @param skuNo
     * @return
     */
    private String subGoodsInternalId(String skuNo) {
        if (StringUtils.isBlank(skuNo) || skuNo.indexOf(SymbolConstants.HOR_LINE) <= 0) {
            return null;
        }
        return skuNo.substring(skuNo.indexOf(SymbolConstants.HOR_LINE) + 1);
    }


}
