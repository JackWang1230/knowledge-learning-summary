package cn.wr.collect.sync.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.connectors.elasticsearch.ActionRequestFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkBase;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static cn.wr.collect.sync.constants.PropertiesConstants.*;

@Slf4j
public class ESSinkUtil {
    //es security constant
    public static final String ES_SECURITY_ENABLE = "es.security.enable";
    public static final String ES_SECURITY_USERNAME = "es.security.username";
    public static final String ES_SECURITY_PASSWORD = "es.security.password";

    /**
     *
     * @param data
     * @param func
     * @param parameterTool
     * @param parallelism
     * @param <T>
     * @throws Exception
     */
    public static <T> void addSink(SingleOutputStreamOperator<T> data, ElasticsearchSinkFunction<T> func,
                                   ParameterTool parameterTool, Integer parallelism) throws Exception {
        // 从配置文件中读取并行 sink 数，这个也是性能调优参数，特别提醒，这样才能够更快的消费，防止 kafka 数据堆积
        int parallelisms = null == parallelism || 0 == parallelism ? parameterTool.getInt(STREAM_SINK_PARALLELISM, 5) : parallelism;
        data.addSink(getSink(data, func, new RetryRequestFailureHandler(), parameterTool))
                .setParallelism(parallelisms).name("sink-elastic");
    }

    /**
     * es sink
     *
     * @param data  数据
     * @param func
     * @param <T>
     */
    public static <T> ElasticsearchSink getSink(SingleOutputStreamOperator<T> data, ElasticsearchSinkFunction<T> func, ActionRequestFailureHandler failureHandler,
                                                ParameterTool parameterTool) throws Exception {

        // 从配置文件中读取 es 的地址
        List<HttpHost> hosts = getEsAddresses(parameterTool.get(ELASTICSEARCH_HOSTS));
        // 从配置文件中读取 bulk flush size，代表一次批处理的数量，这个可是性能调优参数，特别提醒
        int bulkFlushMaxActions = parameterTool.getInt(ELASTICSEARCH_BULK_FLUSH_MAX_ACTIONS, 1000);
        // 从配置文件中读取 bulk flush interval ms，代表一次批处理的时间，这个可是性能调优参数，特别提醒
        long bulkFlushInterval = parameterTool.getInt(ELASTICSEARCH_BULK_FLUSH_INTERVAL_MS, -1);


        ElasticsearchSink.Builder<T> esSinkBuilder = new ElasticsearchSink.Builder<>(hosts, func);
        esSinkBuilder.setBulkFlushMaxActions(bulkFlushMaxActions);
        if (bulkFlushInterval > 0) {
            esSinkBuilder.setBulkFlushInterval(bulkFlushInterval);
        }

        // 用来表示是否开启重试机制
        esSinkBuilder.setBulkFlushBackoff(parameterTool.getBoolean(ELASTICSEARCH_BULK_FLUSH_BACKOFF_ENABLE));
        // 重试策略，有两种：EXPONENTIAL 指数型（表示多次重试之间的时间间隔按照指数方式进行增长）、CONSTANT 常数型（表示多次重试之间的时间间隔为固定常数）
        String type = parameterTool.get(ELASTICSEARCH_BULK_FLUSH_BACKOFF_TYPE);
        if (StringUtils.equals(type, ElasticsearchSinkBase.FlushBackoffType.CONSTANT.name()))
            esSinkBuilder.setBulkFlushBackoffType(ElasticsearchSinkBase.FlushBackoffType.CONSTANT);
        else if (StringUtils.equals(type, ElasticsearchSinkBase.FlushBackoffType.EXPONENTIAL.name()))
            esSinkBuilder.setBulkFlushBackoffType(ElasticsearchSinkBase.FlushBackoffType.EXPONENTIAL);
        // 进行重试的时间间隔
        esSinkBuilder.setBulkFlushBackoffDelay(parameterTool.getLong(ELASTICSEARCH_BULK_FLUSH_BACKOFF_DELAY));
        // 失败重试的次数
        esSinkBuilder.setBulkFlushBackoffRetries(parameterTool.getInt(ELASTICSEARCH_BULK_FLUSH_BACKOFF_RETRIES));

        esSinkBuilder.setFailureHandler(failureHandler);
        if (parameterTool.getBoolean(ES_SECURITY_ENABLE)) {
            esSinkBuilder.setRestClientFactory((restClientBuilder)-> {
                final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(parameterTool.getRequired(ES_SECURITY_USERNAME),
                                parameterTool.getRequired(ES_SECURITY_PASSWORD)));
                // 设置自定义http客户端配置
                restClientBuilder.setHttpClientConfigCallback((httpClientBuilder) -> {
                        httpClientBuilder.disableAuthCaching();
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                });
            });
        }
        // data.addSink(esSinkBuilder.build()).setParallelism(parallelisms).name("sink-elastic");
        return esSinkBuilder.build();
    }

    /**
     * 解析配置文件的 es hosts
     *
     * @param hosts
     * @return
     * @throws MalformedURLException
     */
    public static List<HttpHost> getEsAddresses(String hosts) throws MalformedURLException {
        String[] hostList = hosts.split(",");
        List<HttpHost> addresses = new ArrayList<>();
        for (String host : hostList) {
            if (host.startsWith("http")) {
                URL url = new URL(host);
                addresses.add(new HttpHost(url.getHost(), url.getPort()));
            } else {
                String[] parts = host.split(":", 2);
                if (parts.length > 1) {
                    addresses.add(new HttpHost(parts[0], Integer.parseInt(parts[1])));
                } else {
                    throw new MalformedURLException("invalid elasticsearch hosts format");
                }
            }
        }
        return addresses;
    }
}
