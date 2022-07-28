package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.constants.PropertiesConstants;
import cn.wr.collect.sync.model.goodscenter.GoodsCenterDTO;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.CharacterUtil;
import cn.wr.collect.sync.utils.DingTalkUtil;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import static cn.wr.collect.sync.constants.OutputTagConst.OUT_PUT_TAG_DTP;
import static cn.wr.collect.sync.constants.OutputTagConst.OUT_PUT_TAG_NOT_DTP;


public class GoodsCenterSideOutFlatMap extends ProcessFunction<GoodsCenterDTO, GoodsCenterDTO> {
    private static final long serialVersionUID = 2216179129946757371L;
    private static RedisService redisService;
    private static String GOODS_TITLE = null;
    private static String GOODS_SECRET = null;
    private static String GOODS__URL = null;
    private static final String MSG_CONTENT = "#### %s \n\n > %s \n\n";

    @Override
    public void open(Configuration parameters) throws Exception {
        final ParameterTool tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        redisService = new RedisService(tool);
        GOODS_TITLE = tool.get(PropertiesConstants.GOODS_CENTER_BOT_TITLE);
        GOODS_SECRET = tool.get(PropertiesConstants.GOODS_CENTER_BOT_SECRET);
        GOODS__URL = tool.get(PropertiesConstants.GOODS_CENTER_BOT_URL);
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void processElement(GoodsCenterDTO dto, Context context, Collector<GoodsCenterDTO> collector) throws Exception {
        try {
            System.out.println("进入到这里进行拆分");
            Boolean exists = redisService.checkIsDtpStore(dto.getDbId());
            // String json = JSON.toJSONString(dto, SerializerFeature.WriteMapNullValue);

            if (CharacterUtil.isMessyCode(dto.getApprovalNumber())
                    || CharacterUtil.isMessyCode(dto.getCommonName())) {
                sendDingDing(dto);
            }

            if (exists) {
                context.output(OUT_PUT_TAG_DTP, dto);
            } else {
                context.output(OUT_PUT_TAG_NOT_DTP, dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送钉钉消息
     */
    private void sendDingDing(GoodsCenterDTO dto) {
        String content = String.format(MSG_CONTENT, GOODS_TITLE, JSON.toJSONString(dto));
        DingTalkUtil.sendDingDingWithSecret(GOODS__URL, GOODS_SECRET, GOODS_TITLE, content);
    }
}
