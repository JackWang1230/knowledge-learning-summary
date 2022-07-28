package cn.wr.collect.sync.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class DingTalkUtil {
    private static Logger log = LoggerFactory.getLogger(DingTalkUtil.class);
    /**
     * 发送POST方法的请求
     *
     * @param url
     * @param param
     * @return
     */
    public static String sendPost(String url, Map<String, Object> param, Map<String, String> headParam) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性 请求头
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Fiddler");

            if (headParam != null) {
                for (Map.Entry<String, String> entry : headParam.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(JSON.toJSONString(param));
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.error("DingTalkUtil.sendPost: " + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                log.error("DingTalkUtil.sendPost: " + ex);
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 发送POST请求
     *
     * @param url
     * @param mapParam
     * @return
     */
    public static String sendPostByMap(String url, Map<String, Object> mapParam) {
        Map<String, String> headParam = new HashMap<String, String>();
        headParam.put("Content-type", "application/json;charset=UTF-8");
        return sendPost(url, mapParam, headParam);
    }

    /**
     * 启动任务
     * @return
     */
    public static void operateJob(String url, String job, String env, String operate) {
        // 请求的JSON数据，这里我用map在工具类里转成json格式
        Map<String, Object> json = new HashMap<>(16);
        Map<String, Object> text = new HashMap<>(16);
        json.put("msgtype", "text");
        text.put("job", job + "\n"
                + "env: " + env + "\n"
                + "operate: " + operate );
        json.put("text", text);
        log.info("DingTalkUtil startJob url:{}, params:{}", url, json);
        String result = sendPostByMap(url, json);
        log.info("DingTalkUtil startJob url:{}, params:{}, result:{}", url, json, result);
    }

    /**
     * 加密方式发送钉钉消息
     * @param postUrl
     * @param secret
     * @param title
     * @param msg
     */
    public static void sendDingDingWithSecret(String postUrl, String secret, String title, String msg) {
        try {
            Map<String, Object> text = new HashMap<>(16);
            text.put("title", title);
            text.put("text", msg);

            Map<String, Object> json = new HashMap<>();
            json.put("msgtype", "markdown");
            json.put("markdown", text);

            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");

            // 发送post请求
            log.info("DingTalkUtil ding talk, json:{}", json);
            String url = String.format(postUrl, timestamp, sign);
            String result = DingTalkUtil.sendPostByMap(url, json);
            log.info("DingTalkUtil ding talk, url:{}, json:{} result:{}", url, json, result);
        }
        catch (Exception e) {
            log.error("DingTalkUtil Exception:{}", e);
        }
    }

}
