package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.RedisConstant;
import cn.wr.collect.sync.model.annotations.Table;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.kafka.ElasticGoodsDTO;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.LocationValidUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.elasticsearch.common.geo.GeoHashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static cn.wr.collect.sync.constants.CommonConstants.*;
import static cn.wr.collect.sync.constants.SymbolConstants.COMMA_EN;
import static cn.wr.collect.sync.constants.SymbolConstants.HOR_LINE;

public class GoodsStoreSink extends RichSinkFunction<BasicModel<ElasticGoodsDTO>> {
    private static final long serialVersionUID = 5609394255337520340L;
    private static final Logger log = LoggerFactory.getLogger(GoodsStoreSink.class);
    private RedisService redisService;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        redisService = new RedisService(tool);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void invoke(BasicModel<ElasticGoodsDTO> model, Context context) throws Exception {
        ElasticGoodsDTO data = model.getData();
        // 更新redis数据状态
        switch (model.getOperate()) {
            case OPERATE_INSERT:
            case OPERATE_UPDATE:
                if (this.checkValid(data)) {
                    redisService.addSetGoodsAll(this.generateKey(data), this.generateVal(data));
//                    log.info("GoodsStoreSink add set: {}", JSON.toJSONString(model));
                } else {
                    redisService.deleteSetGoodsAll(this.generateKey(data), this.generateVal(data));
//                    log.info("GoodsStoreSink add delete set: {}", JSON.toJSONString(model));
                }
                break;

            case OPERATE_DELETE:
            case OPERATE_UPDATE_DELETE:
                if (StringUtils.equals(Table.BaseDataTable.gc_standard_goods_syncrds.name(), model.getTableName())
                        || StringUtils.equals(Table.BaseDataTable.gc_goods_spu_attr_syncrds.name(), model.getTableName())) {
                    redisService.deleteKeyGoodsAll(this.generateKey(data));
//                    log.info("GoodsStoreSink delete by key: {}", JSON.toJSONString(model));
                }
                else {
                    redisService.deleteSetGoodsAll(this.generateKey(data), this.generateVal(data));
//                    log.info("GoodsStoreSink delete set: {}", JSON.toJSONString(model));
                }

                break;

            default:
                log.info("GoodsStoreSink unknown operate:{}", JSON.toJSONString(model));
        }
    }

    /**
     * 生成key
     * @param data
     * @return
     */
    private String generateKey(ElasticGoodsDTO data) {
        return RedisConstant.REDIS_GOODS_ALL + data.getTradeCode();
    }

    /**
     * 生成val
     * @param data
     * @return
     */
    private String generateVal(ElasticGoodsDTO data) {
        return StringUtils.isNotBlank(data.getLocation())
                ? data.getMerchantId() + HOR_LINE + data.getStoreId() + this.getLocation(data.getLocation())
                : data.getMerchantId() + HOR_LINE + data.getStoreId();
    }

    /**
     * 获取location
     * @param location
     * @return
     */
    private String getLocation(String location) {
        String geohash = null;
        try {
            String[] split = location.split(COMMA_EN);
            if (split.length != 2) {
                return null;
            }
            if (LocationValidUtil.isLA(split[0]) && LocationValidUtil.isLONG(split[1])) {
                geohash = HOR_LINE + GeoHashUtils.stringEncode(Double.parseDouble(split[1]), Double.parseDouble(split[0]), 11);
            }
        }
        catch (Exception e) {
            log.error("GoodsStoreSink getLocation e:{}", e);
        }
        return geohash;
    }

    /**
     * 校验参数合法
     * 若 storeStatus = 1，isDtp <> 1，sEphedrine = false，isOffShelf = false，iswrOffShelf = false，
     * salePrice >=0.1 ，if (channel = 2) isOverweight = 0 , isStandard = true。全国搜索redis，将门店数据放到条码中
     * dtp门店 非dtp商品
     * @param data
     * @return
     */
    private boolean checkValid(ElasticGoodsDTO data) {
        return STORE_O2O_OPEN.equals(data.getStoreStatus()) /*&& !IS_DTP_TRUE.equals(data.getIsDtp())*/
                && Objects.nonNull(data.getIsEphedrine()) && !data.getIsEphedrine()
                && (IS_DTP_TRUE.equals(data.getIsDtp()) || (Objects.nonNull(data.getIsOffShelf()) && !data.getIsOffShelf()))
                && (!(IS_DTP_FALSE.equals(data.getIsDtp()) && IS_DTP_STORE_TRUE.equals(data.getIsDtpStore())))
                && Objects.nonNull(data.getIswrOffShelf()) && !data.getIswrOffShelf()
                && Objects.nonNull(data.getSalePrice()) && data.getSalePrice().compareTo(GOODS_PRICE_DIME) >= 0
                && StringUtils.isNotBlank(data.getChannel())
                && (StringUtils.equals(CHANNEL_O2O, data.getChannel()) && Objects.nonNull(data.getIsOverweight())
                    && !GOODS_OVERWEIGHT_STATUS_TRUE.equals(data.getIsOverweight())
                    || StringUtils.equals(CHANNEL_DS, data.getChannel()))
                && Objects.nonNull(data.getIsStandard()) && data.getIsStandard();
     }
}
