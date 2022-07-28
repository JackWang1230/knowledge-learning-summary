package cn.wr.collect.sync.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * http 请求工具类
 */
public class HttpUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * Get
     * @param url
     * @return
     */
    public static String doGet(String url) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            // 通过址默认配置创建一个httpClient实例
            httpClient = HttpClients.createDefault();
            // 创建httpGet远程连接实例
            HttpGet httpGet = new HttpGet(url);
            // 设置请求头信息，鉴权
            httpGet.setHeader("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = httpClient.execute(httpGet);
            // 通过返回对象获取返回数据
            HttpEntity entity = response.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            result = EntityUtils.toString(entity);
        }
        catch (ClientProtocolException e) {
            LOGGER.error("### HttpUtils doGet ClientProtocolException:{}", e);
        }
        catch (IOException e) {
            LOGGER.error("### HttpUtils doGet IOException:{}", e);
        }
        finally {
            close(httpClient, response);
        }
        return result;
    }

    /**
     * Post
     * @param url
     * @param paramMap
     * @return
     */
    public static String doPost(String url, Map<String, Object> paramMap) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        httpClient = HttpClients.createDefault();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);
        // 设置请求头
        // httpPost.addHeader("Content-Type", "application/json");
        // 封装post请求参数
        if (null != paramMap && paramMap.size() > 0) {
            /*List<NameValuePair> nvps = new ArrayList<>();
            // 通过map集成entrySet方法获取entity
            Set<Entry<String, Object>> entrySet = paramMap.entrySet();
            // 循环遍历，获取迭代器
            Iterator<Entry<String, Object>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Entry<String, Object> mapEntry = iterator.next();
                nvps.add(new BasicNameValuePair(mapEntry.getKey(), null == mapEntry.getValue() ? null : mapEntry.getValue().toString()));
            }*/

            // 为httpPost设置封装好的请求参数
            try {
                StringEntity entity = new StringEntity(JSON.toJSONString(paramMap, SerializerFeature.WriteMapNullValue));
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");//发送json数据需要设置contentType
                httpPost.setEntity(entity);
                // httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                LOGGER.error("### HttpUtils doPost UnsupportedEncodingException:{}", e);
            }
        }
        try {
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
        }
        catch (ClientProtocolException e) {
            LOGGER.error("### HttpUtils doPost ClientProtocolException:{}", e);
        }
        catch (IOException e) {
            LOGGER.error("### HttpUtils doPost IOException:{}", e);
        }
        finally {
            close(httpClient, httpResponse);
        }
        return result;
    }

    /**
     * 关闭连接
     * @param httpClient
     * @param response
     */
    private static void close(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        // 关闭资源
        if (null != response) {
            try {
                response.close();
            } catch (IOException e) {
                LOGGER.error("### HttpUtils close IOException:{}", e);
            }
        }
        if (null != httpClient) {
            try {
                httpClient.close();
            } catch (IOException e) {
                LOGGER.error("### HttpUtils close IOException:{}", e);
            }
        }
    }


    /*public static void main(String[] args) {
        ParameterTool parameterTool = ExecutionEnvUtil.PARAMETER_TOOL;
        try {
            Integer changeType = 1;
            List<String> ids = Collections.singletonList("1-1-1-1");
            // 发送mbs
            GoodsReqDTO goodsReqDTO = new GoodsReqDTO(changeType, ids);
            PublishReqDTO publishReqDTO = new PublishReqDTO();
            publishReqDTO.setTopic(parameterTool.get(MBS_GOODS_TOPIC));
            // publishReqDTO.setTag(parameterTool.get(MBS_GOODS_MGC_TAG));
            publishReqDTO.setMessage(goodsReqDTO);
            publishReqDTO.setMsgKey(UUID.randomUUID().toString());
            publishReqDTO.setReqNo(UUID.randomUUID().toString());

            String url = parameterTool.get(MBS2_SERVICE_ADDR) + "/topic/publish";
            System.out.println(url);
            String result = HttpUtils.doPost(url, ConvertUtils.objectToMap(publishReqDTO));
            // {"errno":0,"error":"请求成功","dataType":"OBJECT","data":{"messageId":"C0A803D02D823D71D5525A8FE29060C5"},"success":true,"fail":false}
            // String result = "{\"errno\":0,\"error\":\"请求成功\",\"dataType\":\"OBJECT\",\"data\":{\"messageId\":\"C0A803D02D823D71D5525A8FE29060C5\"},\"success\":true,\"fail\":false}";
            ResultDTO resultDTO = JSON.parseObject(result, ResultDTO.class);
            System.out.println(resultDTO);
            System.out.println(result);
            System.out.println(JSON.toJSONString(resultDTO));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}