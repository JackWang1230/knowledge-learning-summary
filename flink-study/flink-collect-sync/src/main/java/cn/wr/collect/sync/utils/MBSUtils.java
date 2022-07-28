package cn.wr.collect.sync.utils;

import cn.wr.collect.sync.model.mbs.GoodsReqDTO;
import cn.wr.collect.sync.model.mbs.PublishReqDTO;
import cn.wr.collect.sync.model.mbs.ResultDTO;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;


public class MBSUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MBSUtils.class);

    /**
     * 发送mbs
     * 1. 对应的partner_store_all 表中baidu_online 值为1
     * 2. es中图片字段(img)不为空
     * 3. 商品名称中不含酒精的
     * @param changeType
     * @param skuCode
     */
    public static void sendMbs(ParameterTool parameterTool, Integer changeType, String skuCode) {
        if (!parameterTool.getBoolean(SINK_MBS)) {
            return;
        }

        PublishReqDTO publishReqDTO = new PublishReqDTO();
        try {
            // 参数赋值
            publishReqDTO.setTopic(parameterTool.get(MBS_GOODS_TOPIC));
            publishReqDTO.setTag(parameterTool.get(MBS_GOODS_MGC_TAG));
            publishReqDTO.setMessage(new GoodsReqDTO(changeType,
                    Collections.singletonList(skuCode)));
            publishReqDTO.setMsgKey(UUID.randomUUID().toString());
            publishReqDTO.setReqNo(UUID.randomUUID().toString());

            // 请求地址
            String url = parameterTool.get(MBS2_SERVICE_ADDR) + "/topic/publish";

            // 发送mbs
            String result = HttpUtils.doPost(url, ConvertUtils.objectToMap(publishReqDTO));
            LOGGER.info("###  sendMbs reqDTO:{}, result:{}",
                    JSON.toJSONString(publishReqDTO), result);

            // 返回结果，异常打印
            ResultDTO resultDTO = JSON.parseObject(result, ResultDTO.class);
            if (null == resultDTO || resultDTO.getErrno() != 0) {
                LOGGER.error("###  sendMbs error reqDTO:{}, result:{}",
                        JSON.toJSONString(publishReqDTO), result);
            }
        }
        catch (Exception e) {
            LOGGER.error("###  sendMbs reqDTO:{} Exception:{}",
                    JSON.toJSONString(publishReqDTO), e);
        }
    }


    /**
     * 发送mbs
     * 1. 对应的partner_store_all 表中baidu_online 值为1
     * 2. es中图片字段(img)不为空
     * 3. 商品名称中不含酒精的
     * @param changeType
     * @param skuCodes
     */
    public static void sendMbs(ParameterTool parameterTool, Integer changeType, List<String> skuCodes) {
        if (!parameterTool.getBoolean(SINK_MBS)) {
            return;
        }

        PublishReqDTO publishReqDTO = new PublishReqDTO();
        try {
            // 参数赋值
            publishReqDTO.setTopic(parameterTool.get(MBS_GOODS_TOPIC));
            publishReqDTO.setTag(parameterTool.get(MBS_GOODS_MGC_TAG));
            publishReqDTO.setMessage(new GoodsReqDTO(changeType,skuCodes));
            publishReqDTO.setMsgKey(UUID.randomUUID().toString());
            publishReqDTO.setReqNo(UUID.randomUUID().toString());

            // 请求地址
            String url = parameterTool.get(MBS2_SERVICE_ADDR) + "/topic/publish";

            // 发送mbs
            String result = HttpUtils.doPost(url, ConvertUtils.objectToMap(publishReqDTO));
            LOGGER.info("###  sendMbs reqDTO:{}, result:{}",
                    JSON.toJSONString(publishReqDTO), result);

            // 返回结果，异常打印
            ResultDTO resultDTO = JSON.parseObject(result, ResultDTO.class);
            if (null == resultDTO || resultDTO.getErrno() != 0) {
                LOGGER.error("###  sendMbs error reqDTO:{}, result:{}",
                        JSON.toJSONString(publishReqDTO), result);
            }
        }
        catch (Exception e) {
            LOGGER.error("###  sendMbs reqDTO:{} Exception:{}",
                    JSON.toJSONString(publishReqDTO), e);
        }
    }
}
