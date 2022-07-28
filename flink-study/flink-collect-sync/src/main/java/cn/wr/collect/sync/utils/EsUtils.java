package cn.wr.collect.sync.utils;

import cn.wr.collect.sync.constants.PropertiesConstants;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class EsUtils {
    private static final Logger log = LoggerFactory.getLogger(EsUtils.class);

    /**
     * 初始化客户端
     * @param tool
     * @return
     */
    public static RestHighLevelClient initClient(ParameterTool tool) {
        try {
            URL url = new URL(tool.get(PropertiesConstants.ELASTICSEARCH_HOSTS));
            RestClientBuilder builder = RestClient.builder(new HttpHost(url.getHost(), url.getPort()));
            if (tool.getBoolean(ESSinkUtil.ES_SECURITY_ENABLE)) {
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(tool.get(ESSinkUtil.ES_SECURITY_USERNAME),
                                tool.get(ESSinkUtil.ES_SECURITY_PASSWORD)));
                builder.setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider));
            }
            return new RestHighLevelClient(builder);
        }
        catch (Exception e) {
            log.error("EsUtils Exception:{}", e);
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
            log.error("EsUtils IOException:{}", e);
        }
    }
}
