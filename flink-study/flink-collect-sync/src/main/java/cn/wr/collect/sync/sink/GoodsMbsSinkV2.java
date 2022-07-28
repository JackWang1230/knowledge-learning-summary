package cn.wr.collect.sync.sink;

import cn.wr.collect.sync.model.ElasticO2O;
import cn.wr.collect.sync.model.basic.BasicModel;
import cn.wr.collect.sync.model.mbs.GoodsReqDTO;
import cn.wr.collect.sync.model.mbs.PublishReqDTO;
import cn.wr.collect.sync.utils.ConvertUtils;
import cn.wr.collect.sync.utils.HttpUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.UUID;

import static cn.wr.collect.sync.constants.CommonConstants.*;
import static cn.wr.collect.sync.constants.PropertiesConstants.*;

public class GoodsMbsSinkV2 extends RichSinkFunction<BasicModel<ElasticO2O>> {
    private static final long serialVersionUID = -5718602542274456641L;
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsMbsSinkV2.class);
    private static boolean SEND_MBS = false;
    private static String MBS_TOPIC = null;
    private static String MBS_TAG = null;
    private static String MBS_ADDR = null;


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        SEND_MBS = parameterTool.getBoolean(SINK_MBS);
        MBS_TOPIC = parameterTool.get(MBS_GOODS_TOPIC);
        MBS_TAG = parameterTool.get(MBS_GOODS_MGC_TAG);
        MBS_ADDR = parameterTool.get(MBS2_SERVICE_ADDR);
    }

    @Override
    public void invoke(BasicModel<ElasticO2O> value, Context context) throws Exception {
        this.sendMbs(value);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    /**
     * 发送mbs
     * 1. 对应的partner_store_all 表中baidu_online 值为1
     * 2. es中图片字段(img)不为空
     * 3. 商品名称中不含酒精的
     *
     * 4. DTP商品不发送百度mbs
     * @param model
     */
    private void sendMbs(BasicModel<ElasticO2O> model) {
        if (!SEND_MBS) {
            return;
        }
        ElasticO2O elasticO2O = model.getData();

        // 条件过滤
        if (null == elasticO2O.getBaiduOnline() || !BAIDU_ONLINE.equals(elasticO2O.getBaiduOnline())) {
            return;
        }
        if (StringUtils.isBlank(elasticO2O.getImg())) {
            return;
        }
        if (StringUtils.isNotBlank(elasticO2O.getCommonName())
                && elasticO2O.getCommonName().contains(COMMON_NAME_CONTAINS_ALCOHOL)) {
            return;
        }
        if (IS_DTP_TRUE.equals(elasticO2O.getIsDtp())) {
            return;
        }

        PublishReqDTO publishReqDTO = new PublishReqDTO();
        try {
            // 参数赋值
            publishReqDTO.setTopic(MBS_TOPIC);
            publishReqDTO.setTag(MBS_TAG);
            publishReqDTO.setMessage(new GoodsReqDTO(getChangeType(model.getOperate()),
                    Collections.singletonList(elasticO2O.getSkuCode())));
            publishReqDTO.setMsgKey(UUID.randomUUID().toString());
            publishReqDTO.setReqNo(UUID.randomUUID().toString());

            // 请求地址
            String url = MBS_ADDR + "/topic/publish";

            // 发送mbs
            long start = System.currentTimeMillis();
            String result = HttpUtils.doPost(url, ConvertUtils.objectToMap(publishReqDTO));
            long end = System.currentTimeMillis();
            LOGGER.info("### GoodsElasticSink sendMbs reqDTO:{}, result:{}, time:{}",
                    JSON.toJSONString(publishReqDTO), result, (end - start));
        }
        catch (Exception e) {
            LOGGER.error("### GoodsElasticSink sendMbs reqDTO:{} Exception:{}",
                    JSON.toJSONString(publishReqDTO), e);
        }
    }

    /**
     * 获取 mbs changeType
     * @param operate
     * @return
     */
    private Integer getChangeType(String operate) {
        switch (operate) {
            case OPERATE_INSERT:
                return OPERATE_MBS_INSERT;
            case OPERATE_DELETE:
                return OPERATE_MBS_DELETE;
            case OPERATE_UPDATE:
                return OPERATE_MBS_UPDATE;
            default:
                return null;
        }
    }
}
