package cn.wr.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import java.util.List;
import java.util.Map;

/**
 * @author RWang
 * @Date 2022/8/3
 */

public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

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
            logger.error("### HttpUtils doGet ClientProtocolException:{0}", e);
        }
        catch (IOException e) {
            logger.error("### HttpUtils doGet IOException:{0}", e);
        }
        finally {
            close(httpClient, response);
        }
        return result;
    }

    /**
     * Post
     * @param url url
     * @param paramMap paramMap
     * @return String
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
        // 封装post请求参数
        if (null != paramMap && paramMap.size() > 0) {
            // 为httpPost设置封装好的请求参数
            try {
                StringEntity entity = new StringEntity(JSON.toJSONString(paramMap, SerializerFeature.WriteMapNullValue));
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");//发送json数据需要设置contentType
                httpPost.setEntity(entity);
            }
            catch (UnsupportedEncodingException e) {
                logger.error("### HttpUtils doPost UnsupportedEncodingException:{0}", e);
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
            logger.error("### HttpUtils doPost ClientProtocolException:{0}", e);
        }
        catch (IOException e) {
            logger.error("### HttpUtils doPost IOException:{0}", e);
        }
        finally {
            close(httpClient, httpResponse);
        }
        return result;
    }

    public static String doPost(String url , List<Map<String,Object>> listParamMap){

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
        // 封装post请求参数
        if (null != listParamMap && listParamMap.size() > 0) {
            // 为httpPost设置封装好的请求参数
            JSONArray jsonArray = new JSONArray();
            listParamMap.forEach(json->{
                JSONObject bodyObj = new JSONObject(json);
                jsonArray.add(bodyObj);
            });
            StringEntity entity = new StringEntity(jsonArray.toString(),"utf-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");//发送json数据需要设置contentType
            // new UrlEncodedFormEntity(new ArrayList<>())
            httpPost.setEntity(entity);
        }
        try {
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
        }
        catch (ClientProtocolException e) {
            logger.error("### HttpUtils doPost ClientProtocolException:{0}", e);
        }
        catch (IOException e) {
            logger.error("### HttpUtils doPost IOException:{0}", e);
        }
        finally {
            close(httpClient, httpResponse);
        }
        return result;
    }

    /**
     * 关闭连接
     * @param httpClient httpClient
     * @param response response
     */
    private static void close(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        // 关闭资源
        if (null != response) {
            try {
                response.close();
            } catch (IOException e) {
                logger.error("### HttpUtils close IOException:{0}", e);
            }
        }
        if (null != httpClient) {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error("### HttpUtils close IOException:{0}", e);
            }
        }
    }
}
