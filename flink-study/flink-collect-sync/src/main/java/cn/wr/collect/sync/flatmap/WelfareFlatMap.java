package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.constants.CommonConstants;
import cn.wr.collect.sync.constants.PropertiesConstants;
import cn.wr.collect.sync.model.welfare.WelfareChannel;
import cn.wr.collect.sync.model.welfare.WelfareElastic;
import cn.wr.collect.sync.model.welfare.WelfareGoods;
import cn.wr.collect.sync.utils.DingTalkUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;

public class WelfareFlatMap extends RichFlatMapFunction<String, WelfareElastic> {
    private static final long serialVersionUID = 1105009469266659832L;
    private static final Logger LOGGER = LoggerFactory.getLogger(WelfareFlatMap.class);
    private static String DING_WELFARE_GOODS_BOT_URL = null;
    private static String DING_WELFARE_GOODS_BOT_TITLE = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        DING_WELFARE_GOODS_BOT_URL = parameterTool.get(PropertiesConstants.WELFARE_GOODS_BOT_URL);
        DING_WELFARE_GOODS_BOT_TITLE = parameterTool.get(PropertiesConstants.WELFARE_GOODS_BOT_TITLE);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void flatMap(String json, Collector<WelfareElastic> collector) throws Exception {
        LOGGER.info("WelfareFlatMap json: {}", json);
        if (StringUtils.isBlank(json)) {
            LOGGER.info("WelfareFlatMap json is null");
            return;
        }
        WelfareGoods welfareGoods;
        try {
            welfareGoods = JSON.parseObject(json, WelfareGoods.class);
        }
        catch (Exception e) {
            LOGGER.info("WelfareFlatMap flatMap json:{} Exception:{}", json, e);
            return;
        }
        if (Objects.isNull(welfareGoods)) {
            LOGGER.info("WelfareFlatMap welfareGoods is null");
            return;
        }
        if (CollectionUtils.isEmpty(welfareGoods.getChannelList())) {
            LOGGER.info("WelfareFlatMap welfareGoods channelList is empty, welfareGooods: {}", json);
            return;
        }
        Set<Integer> channelSet = new HashSet<>();
        boolean flag = true;
        for (WelfareChannel channel : welfareGoods.getChannelList()) {
            if (Objects.isNull(channel)) {
                continue;
            }
            if (CommonConstants.WELFARE_CHANNEL_7.equals(channel.getChannel())
                    || CommonConstants.WELFARE_CHANNEL_8.equals(channel.getChannel())) {
                flag = false;
                collector.collect(this.transform(welfareGoods, channel));
            }
            else if (flag) {
                channelSet.add(channel.getChannel());
            }
        }
        // 发送钉钉告警
        // channel不包含7/8渠道
        if (flag) {
            this.sendDingTalk(channelSet, welfareGoods.getSkuNo());
        }
    }

    /**
     * 对象转换
     * @param welfareGoods
     * @return
     */
    private WelfareElastic transform(WelfareGoods welfareGoods, WelfareChannel channel) {
        WelfareElastic welfareElastic = new WelfareElastic();

        welfareElastic.setUniqueId(channel.getChannel() + "-" + welfareGoods.getSkuNo());
        welfareElastic.setChannel(channel.getChannel());
        welfareElastic.setGoodsNo(welfareGoods.getSkuNo());
        welfareElastic.setTitle(welfareGoods.getTitle());
        welfareElastic.setSubTitle(welfareGoods.getSubTitle());
        welfareElastic.setSpecName(welfareGoods.getSpecName());
        welfareElastic.setPrice(channel.getSalePrice());
        welfareElastic.setGoodsType(welfareGoods.getGoodsType());
        welfareElastic.setGoodsSubType(welfareGoods.getGoodsSubType());
        if (Objects.nonNull(channel.getRedisGoodsDetails()) && StringUtils.isNotBlank(channel.getRedisGoodsDetails().getCoverImg())) {
            welfareElastic.setImg(channel.getRedisGoodsDetails().getCoverImg());
        }
        else if (Objects.nonNull(welfareGoods.getRedisGoodsDetails())) {
            welfareElastic.setImg(welfareGoods.getRedisGoodsDetails().getCoverImg());
        }
        welfareElastic.setUrl(channel.getBuyLink());
        welfareElastic.setStatus(channel.getSaleState());
        welfareElastic.setForm(welfareGoods.getSpecName());
        if (Objects.nonNull(welfareGoods.getRedisGoodsSpu()) && Objects.nonNull(welfareGoods.getRedisGoodsSpu().getRedisManual())) {
            welfareElastic.setManufacturer(welfareGoods.getRedisGoodsSpu().getRedisManual().getManufacturer());
            welfareElastic.setIndications(welfareGoods.getRedisGoodsSpu().getRedisManual().getIndications());
        }
        welfareElastic.setOriginPrice(welfareGoods.getOriginPrice());
        welfareElastic.setSyncDate(LocalDateTime.now());
        return welfareElastic;
    }

    /**
     * 发送钉钉
     * @param channelSet
     * @param skuNo
     */
    private void sendDingTalk(Set<Integer> channelSet, String skuNo) {
        if (StringUtils.isBlank(DING_WELFARE_GOODS_BOT_URL) || StringUtils.isBlank(DING_WELFARE_GOODS_BOT_TITLE)) {
            LOGGER.info("WelfareFlatMap ding talk config is null, url:{}, title:{}",
                    DING_WELFARE_GOODS_BOT_URL, DING_WELFARE_GOODS_BOT_TITLE);
            return;
        }
        // 请求的JSON数据，这里我用map在工具类里转成json格式
        Map<String, Object> json = new HashMap<>(16);
        Map<String, Object> text = new HashMap<>(16);
        json.put("msgtype", "text");
        text.put("content", DING_WELFARE_GOODS_BOT_TITLE + "\n"
                + "Channels: " + channelSet.toString() + "\n" + "SkuNo: " + skuNo);
        json.put("text", text);
        // 发送post请求
        LOGGER.info("WelfareFlatMap ding talk, json:{}", json);
        String result = DingTalkUtil.sendPostByMap(DING_WELFARE_GOODS_BOT_URL, json);
        LOGGER.info("WelfareFlatMap ding talk, url:{}, json:{} result:{}", DING_WELFARE_GOODS_BOT_URL, json, result);
    }

}
