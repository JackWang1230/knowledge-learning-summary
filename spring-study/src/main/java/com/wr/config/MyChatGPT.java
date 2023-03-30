package com.wr.config;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.api.Api;
import com.plexpt.chatgpt.entity.BaseResponse;
import com.plexpt.chatgpt.entity.billing.CreditGrantsResponse;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.exception.ChatException;
import io.reactivex.Single;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.math.BigDecimal;
import java.net.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author : WangRui
 * @date : 2023/3/30
 */

@Slf4j
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyChatGPT {

    private String apiKey;

    private List<String> apiKeyList;
    /**
     * 自定义api host使用builder的方式构造client
     */
    @Builder.Default
    private String apiHost = Api.DEFAULT_API_HOST;
    private Api apiClient;
    private OkHttpClient okHttpClient;
    /**
     * 超时 默认300
     */
    @Builder.Default
    private long timeout = 300;
    /**
     * okhttp 代理
     */
    @Builder.Default
    private Proxy proxy = Proxy.NO_PROXY;


    /**
     * 初始化
     */
    public MyChatGPT init() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(chain -> {
            Request original = chain.request();
            String key = apiKey;
            if (apiKeyList != null && !apiKeyList.isEmpty()) {
                key = RandomUtil.randomEle(apiKeyList);
            }

            Request request = original.newBuilder()
                    .header(Header.AUTHORIZATION.getValue(), "Bearer " + key)
                    .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        });
//                .addInterceptor(chain -> {
//            Request original = chain.request();
//            Response response = chain.proceed(original);
//            if (!response.isSuccessful()) {
//                String errorMsg = response.body().string();
//
//                log.error("请求异常：{}", errorMsg);
//                BaseResponse baseResponse = JSON.parseObject(errorMsg, BaseResponse.class);
//                if (Objects.nonNull(baseResponse.getError())) {
//                    log.error(baseResponse.getError().getMessage());
//                    throw new ChatException(baseResponse.getError().getMessage());
//                }
//                throw new ChatException("error");
//            }
//            return response;
//        });

//        client.connectTimeout(timeout, TimeUnit.SECONDS);
//        client.writeTimeout(timeout, TimeUnit.SECONDS);
//        client.readTimeout(timeout, TimeUnit.SECONDS);
//        client.hostnameVerifier((hostname, session) -> true);
        if (Objects.nonNull(proxy)) {
            client.proxy(proxy);
        }
        OkHttpClient httpClient = client.build();
        this.okHttpClient = httpClient;


        this.apiClient = new Retrofit.Builder()
                .baseUrl(this.apiHost)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(Api.class);

        return this;
    }


    /**
     * 最新版的GPT-3.5 chat completion 更加贴近官方网站的问答模型
     *
     * @param chatCompletion 问答参数
     * @return 答案
     */
    public ChatCompletionResponse chatCompletion(ChatCompletion chatCompletion) {
        Single<ChatCompletionResponse> chatCompletionResponse =
                this.apiClient.chatCompletion(chatCompletion);
        return chatCompletionResponse.blockingGet();
    }

    /**
     * 简易版
     *
     * @param messages 问答参数
     */
    public ChatCompletionResponse chatCompletion(List<Message> messages) {
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(messages).build();
        return this.chatCompletion(chatCompletion);
    }

    /**
     * 直接问
     */
    public String chat(String message) {
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(Arrays.asList(Message.of(message)))
                .build();
        ChatCompletionResponse response = this.chatCompletion(chatCompletion);
        return response.getChoices().get(0).getMessage().getContent();
    }

    /**
     * 余额查询
     *
     * @return
     */
    public CreditGrantsResponse creditGrants() {
        Single<CreditGrantsResponse> creditGrants = this.apiClient.creditGrants();
        return creditGrants.blockingGet();
    }


    /**
     * 余额查询
     *
     * @return
     */
    public BigDecimal balance() {
        Single<CreditGrantsResponse> creditGrants = apiClient.creditGrants();
        CreditGrantsResponse response = creditGrants.blockingGet();

        return response.getTotalAvailable();
    }

    /**
     * 余额查询
     *
     * @return
     */
    public static BigDecimal balance(String key) {
        ChatGPT chatGPT = ChatGPT.builder()
                .apiKey(key)
                .build()
                .init();

        return chatGPT.balance();
    }

    public void streamChatCompletion(ChatCompletion chatCompletion,
                                     EventSourceListener eventSourceListener) {

        chatCompletion.setStream(true);

        try {
            EventSource.Factory factory = EventSources.createFactory(okHttpClient);
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(chatCompletion);
            String key = apiKey;
            if (apiKeyList != null && !apiKeyList.isEmpty()) {
                key = RandomUtil.randomEle(apiKeyList);
            }


            Request request = new Request.Builder()
                    .url(apiHost + "v1/chat/completions")
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()),
                            requestBody))
                    .header("Authorization", "Bearer " + key)
                    .build();
            factory.newEventSource(request, eventSourceListener);

        } catch (Exception e) {
            log.error("请求出错：{}", e);
        }
    }

    /**
     * 流式输出
     */
    public void streamChatCompletion(List<Message> messages,
                                     EventSourceListener eventSourceListener) {
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(messages)
                .stream(true)
                .build();
        streamChatCompletion(chatCompletion, eventSourceListener);
    }

}
