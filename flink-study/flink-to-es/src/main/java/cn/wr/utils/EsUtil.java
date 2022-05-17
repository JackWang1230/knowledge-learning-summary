package cn.wr.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import static cn.wr.constants.PropertiesConstants.*;

/**
 * @author RWang
 * @Date 2022/5/17
 */

@Slf4j
public class EsUtil {

    public static RestHighLevelClient initClient(ParameterTool tool){

        try {
            URL url = new URL(tool.get(ELASTICSEARCH_HOSTS));
            RestClientBuilder builder = RestClient.builder(new HttpHost(url.getHost(), url.getPort()));
            if (tool.getBoolean(ELASTICSEARCH_SECURITY_ENABLE)) {
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(tool.get(ELASTICSEARCH_SECURITY_USERNAME),
                                tool.get(ELASTICSEARCH_SECURITY_PASSWORD)));
                builder.setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider));
            }
            return new RestHighLevelClient(builder);

        } catch (MalformedURLException e) {
            log.error("EsUtil Exception:{}", e);
        }
        return null;
    }

    /**
     * 关闭客户端
     * @param client
     */
    public static void closeClient(RestHighLevelClient client) {
        if (Objects.isNull(client)) {
            return;
        }
        try {
            client.close();
        } catch (IOException e) {
            log.error("EsUtil IOException:{}", e);
        }
    }


}
