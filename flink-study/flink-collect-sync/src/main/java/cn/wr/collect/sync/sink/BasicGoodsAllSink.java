package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.constants.RedisConstant;
import cn.wr.collect.sync.constants.SymbolConstants;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.goodsall.GoodsAllData;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.LocationValidUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.elasticsearch.common.geo.GeoHashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.wr.collect.sync.constants.CommonConstants.*;

public class BasicGoodsAllSink extends RichSinkFunction<BasicModel<List<GoodsAllData>>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicGoodsAllSink.class);

    private ParameterTool parameterTool = null;
    private RedisService redisService;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        redisService = new RedisService(parameterTool);
    }

    @Override
    public void invoke(BasicModel<List<GoodsAllData>> metricItem, Context context) throws Exception {
        switch (metricItem.getOperate()) {
            case OPERATE_INSERT:
                List<GoodsAllData> goodsAllDatas = metricItem.getData();
                Map<String, List<String>> map = Maps.newHashMap();
                goodsAllDatas.stream().forEach(goodsAllData -> {
                    String locationHash = GeoHashUtils.stringEncode(Double.parseDouble(goodsAllData.getLongitude()), Double.parseDouble(goodsAllData.getLatitude()), 11);
                    List<String> locationHashs = map.get(goodsAllData.getTradeCode());
                    String storeLocationHash = goodsAllData.getMerchantId() + SymbolConstants.HOR_LINE + goodsAllData.getStoreId() + SymbolConstants.HOR_LINE + locationHash;
                    if (CollectionUtils.isNotEmpty(locationHashs)) {
                        locationHashs.add(storeLocationHash);
                        map.put(RedisConstant.REDIS_GOODSALL_PREFIX + goodsAllData.getTradeCode(), locationHashs);
                    } else {
                        map.put(RedisConstant.REDIS_GOODSALL_PREFIX + goodsAllData.getTradeCode(), Lists.newArrayList(storeLocationHash));
                    }
                });
                for (Map.Entry<String, List<String>> entryUser : map.entrySet()) {
                    redisService.addSet(entryUser.getKey(), entryUser.getValue());
                }
                break;
            case OPERATE_DELETE:
                // 判断是删除条码 还是删除条码里的门店
                List<GoodsAllData> goodsAllDatass = metricItem.getData();
                List<String> delTradeCodes = Lists.newArrayList();
                Map<String, List<String>> map1 = Maps.newHashMap();
                goodsAllDatass.stream().forEach(goodsAllData -> {
                    // 删除条码
                    if (Objects.isNull(goodsAllData.getStoreId())) {
                        delTradeCodes.add(RedisConstant.REDIS_GOODSALL_PREFIX +goodsAllData.getTradeCode());
                        // 跳出当前循环
                        return;
                    }

                    String locationHash = GeoHashUtils.stringEncode(Double.parseDouble(goodsAllData.getLongitude()), Double.parseDouble(goodsAllData.getLatitude()), 11);
                    List<String> locationHashs = map1.get(goodsAllData.getTradeCode());
                    String storeLocationHash = goodsAllData.getMerchantId() + SymbolConstants.HOR_LINE + goodsAllData.getStoreId() + SymbolConstants.HOR_LINE + locationHash;
                    if (CollectionUtils.isNotEmpty(locationHashs)) {
                        locationHashs.add(storeLocationHash);
                        map1.put(RedisConstant.REDIS_GOODSALL_PREFIX + goodsAllData.getTradeCode(), locationHashs);
                    } else {
                        map1.put(RedisConstant.REDIS_GOODSALL_PREFIX + goodsAllData.getTradeCode(), Lists.newArrayList(storeLocationHash));
                    }
                });

                if (CollectionUtils.isNotEmpty(delTradeCodes)) {
                    // 批量删除条码
                    List<List<String>> partitionTradeCodes = Lists.partition(delTradeCodes, 1000);
                    for (int i = 0; i < partitionTradeCodes.size(); i++) {
                        List<String> partitionTradeCode = partitionTradeCodes.get(i);
                        String[] arrDelTradeCodes = partitionTradeCode.toArray(new String[partitionTradeCode.size()]);
                        redisService.delete(arrDelTradeCodes);
                    }
                }

                for (Map.Entry<String, List<String>> entryUser : map1.entrySet()) {
                    redisService.deleteSet(entryUser.getKey(), entryUser.getValue());
                }
                break;

            case OPERATE_UPDATE:
                // 更新 redis set value里的值
                if (PGC_STORE_INFO_INCREMENT.equals(metricItem.getTableName())) {
                    List<GoodsAllData> goodsAllDatasss = metricItem.getData();
                    goodsAllDatasss.stream().forEach(goodsAllData -> {
                        // 删除老的门店坐标数据
                        String tradeCode = RedisConstant.REDIS_GOODSALL_PREFIX + goodsAllData.getTradeCode();
                        boolean isOldLocation = LocationValidUtil.isLA(goodsAllData.getOldLatitude()) && LocationValidUtil.isLONG(goodsAllData.getOldLongitude());
                        String oldLocationHash = isOldLocation ? GeoHashUtils.stringEncode(Double.parseDouble(goodsAllData.getOldLongitude()), Double.parseDouble(goodsAllData.getOldLatitude()), 11) : StringUtils.EMPTY;
                        String oldStoreLocationHash = StringUtils.isNotBlank(oldLocationHash) ? goodsAllData.getMerchantId() + SymbolConstants.HOR_LINE + goodsAllData.getStoreId() + SymbolConstants.HOR_LINE  + oldLocationHash : goodsAllData.getMerchantId() + SymbolConstants.HOR_LINE + goodsAllData.getStoreId();
                        redisService.deleteSet(tradeCode, oldStoreLocationHash);

                        // 新增新的门店坐标数据
                        boolean isNewLocation = LocationValidUtil.isLA(goodsAllData.getLatitude()) && LocationValidUtil.isLONG(goodsAllData.getLongitude());
                        String newLocationHash = isNewLocation ? GeoHashUtils.stringEncode(Double.parseDouble(goodsAllData.getLongitude()), Double.parseDouble(goodsAllData.getLatitude()), 11) : StringUtils.EMPTY;
                        String newStoreLocationHash = StringUtils.isNotBlank(newLocationHash) ? goodsAllData.getMerchantId() + SymbolConstants.HOR_LINE + goodsAllData.getStoreId() + SymbolConstants.HOR_LINE + newLocationHash : goodsAllData.getMerchantId() + SymbolConstants.HOR_LINE + goodsAllData.getStoreId();
                        redisService.addSet(tradeCode, newStoreLocationHash);
                    });
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
        LOGGER.info("### BasicGoodsAllSink close");
    }
}
