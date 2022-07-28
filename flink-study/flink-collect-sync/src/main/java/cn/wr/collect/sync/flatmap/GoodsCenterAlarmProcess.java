package cn.wr.collect.sync.flatmap;

import cn.wr.collect.sync.model.alarm.GoodsCenterAlarm;
import cn.wr.collect.sync.model.gc.OrganizeBase;
import cn.wr.collect.sync.redis.RedisService;
import cn.wr.collect.sync.utils.DateUtils;
import cn.wr.collect.sync.utils.DingTalkUtil;
import cn.wr.collect.sync.utils.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Objects;

import static cn.wr.collect.sync.constants.CommonConstants.*;

public class GoodsCenterAlarmProcess extends ProcessWindowFunction<Tuple2<String, GoodsCenterAlarm>, String, String, TimeWindow> {
    private static final long serialVersionUID = -3118787100869390164L;
    private static final Logger log = LoggerFactory.getLogger(GoodsCenterAlarmProcess.class);

    private RedisService redisService = null;

    // 字段名
    private static final String FIELD_BARCODE = "barcode";
    private static final String FIELD_TRADE_CODE = "trade_code";
    private static final String FIELD_APPROVAL_NUMBER = "approval_number";


    // 发送钉钉消息参数
    private static final String PROP_GOODS_CENTER_ALARM_URL = "goods.center.alarm.url";
    private static final String PROP_GOODS_CENTER_ALARM_SECRET = "goods.center.alarm.secret";
    private static final String PROP_GOODS_CENTER_ALARM_TITLE = "goods.center.alarm.title";

    // 全局配置
    private static String GOODS_CENTER_ALARM_URL = null;
    private static String GOODS_CENTER_ALARM_SECRET = null;
    private static String GOODS_CENTER_ALARM_TITLE = null;

    private static final String DINGDING_MSG = "%s 于 %s %s%s个药品信息待审核，请关注审核。";
    private static final String INSERT_STR = "新增";
    private static final String UPDATE_STR = "更新";

    // 钉钉消息体
    private static final String MSG_CONTENT_UPDATE = "> 变更来源: %s\n\n" +
            "> 操作类型: 更新\n\n" +
            "> 连锁ID: %s\n\n" +
            "> 连锁名称: %s\n\n" +
            "> 总数: %s\n\n"
            ;
    private static final String MSG_CONTENT_UPDATE_DETAIL_01 = " ---- \n\n >> 商品内码: %s\n\n";
    private static final String MSG_CONTENT_UPDATE_DETAIL_02 = ">> %s:【旧值: [%s] 新值: [%s]】\n\n";

    // private static final String MSG_CONTENT_INSERT = "> 变更来源: %s\n\n" +
    //         "> 操作类型: 新增\n\n" +
    //         "> 连锁ID: %s\n\n" +
    //         "> 连锁名称: %s \n\n" +
    //         "> 总数: %s \n\n" +
    //         "> 商品内码:【%s】";

    private static final String MSG_CONTENT_INSERT = "> 变更来源: %s\n\n" +
            "> 操作类型: 新增\n\n" +
            "> 连锁ID: %s\n\n" +
            "> 连锁名称: %s \n\n" +
            "> 总数: %s \n\n";

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        final ParameterTool tool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        GOODS_CENTER_ALARM_URL = tool.get(PROP_GOODS_CENTER_ALARM_URL);
        GOODS_CENTER_ALARM_SECRET = tool.get(PROP_GOODS_CENTER_ALARM_SECRET);
        GOODS_CENTER_ALARM_TITLE = tool.get(PROP_GOODS_CENTER_ALARM_TITLE);
        redisService = new RedisService(tool);
    }

    @Override
    public void close() throws Exception {
        super.close();
        RedisPoolUtil.closePool();
    }

    @Override
    public void process(String key, Context context, Iterable<Tuple2<String, GoodsCenterAlarm>> iterable, Collector<String> collector) throws Exception {
        if (key.startsWith(OPERATE_INSERT)) {
            handle(iterable, INSERT_STR);
        }
        else if (key.startsWith(OPERATE_UPDATE)) {
            handle(iterable, UPDATE_STR);
        }
        else {
            log.error("GoodsCenterAlarmProcess unknown key: {}", key);
        }
    }

    /**
     * 处理 insert
     * @param iterable
     * @return
     */
    private void handle(Iterable<Tuple2<String, GoodsCenterAlarm>> iterable, String operate) {
        // StringBuilder builder = new StringBuilder();
        Long merchantId = null;
        String merchantName = null;
        int cnt = 0;
        for (Tuple2<String, GoodsCenterAlarm> tuple2 : iterable) {
            GoodsCenterAlarm alarm = tuple2.f1;
            cnt ++;
            if (Objects.isNull(merchantId)) {
                merchantId = alarm.getMerchantId();
                if (Objects.nonNull(merchantId)) {
                    OrganizeBase organizeBase = redisService.queryOrganizeBasePartner(merchantId.intValue());
                    if (Objects.isNull(organizeBase) || !NET_TYPE_2.equals(organizeBase.getNetType())) {
                        return;
                    }
                    merchantName = organizeBase.getName();
                }
            }
        }
        if (checkNotValid(merchantName, cnt)) return;
        String content = String.format(DINGDING_MSG, merchantName, DateUtils.format(new Date()), operate, cnt);

        // 随机等待 (0-5)s
        // 钉钉消息同步限制1s内20条消息限制
        this.sendDingDing(content);
    }

    /**
     * 校验参数是否合法
     * @param merchantName
     * @param cnt
     * @return
     */
    private boolean checkNotValid(String merchantName, Integer cnt) {
        return StringUtils.isBlank(merchantName) || Objects.isNull(cnt) || cnt <= 0;
    }

    /**
     * 发送钉钉消息
     * @param content
     */
    private void sendDingDing(String content) {
        if (StringUtils.isBlank(content)) {
            return;
        }
        DingTalkUtil.sendDingDingWithSecret(GOODS_CENTER_ALARM_URL, GOODS_CENTER_ALARM_SECRET, GOODS_CENTER_ALARM_TITLE, content);
    }
}
