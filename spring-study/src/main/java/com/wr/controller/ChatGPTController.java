package com.wr.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.ChatGPTStream;
import com.plexpt.chatgpt.api.Api;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.listener.ConsoleStreamListener;
import com.plexpt.chatgpt.listener.SseStreamListener;
import com.plexpt.chatgpt.util.Proxys;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.AuthenticationInterceptor;
import com.theokanning.openai.service.OpenAiService;
import com.wr.config.MyAuthenticationInterceptor;
import com.wr.config.MyChatGPT;
import com.wr.enums.StatusCode;
import com.wr.module.ResponseData;
import io.reactivex.Single;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.net.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author : WangRui
 * @date : 2023/3/30
 */

@RestController
public class ChatGPTController {


    private static final String BASE_URL = "https://api.openai.com/";


    private static final List<Message> listStream = new ArrayList<>();
    private static final List<Message> list = new ArrayList<>();
    String token ="sk-xxxxxxxx";

    @RequestMapping("/chat")
    public ResponseData<List<String>> chat(String message)  {


        OpenAiService service = new OpenAiService(token, Duration.ofSeconds(30L));

        CompletionRequest completionRequest = CompletionRequest.builder()
                .model("text-davinci-003")
                .prompt(message)
                .temperature(0.5)
                .maxTokens(2048)
                .topP(1.0)
                .frequencyPenalty(0.0)
                .presencePenalty(0.0)
                .build();

        ArrayList<String> dataList = new ArrayList<>();

        try {

            service.createCompletion(completionRequest).getChoices().forEach(choice -> {
                System.out.println(choice.getText());
                dataList.add(choice.getText());

            });
            return new ResponseData<>(StatusCode.SUCCESS.getCode(),StatusCode.SUCCESS.getMessage(), dataList);
        }
        catch (Exception e){
            dataList.add("请求超时");
            return new ResponseData<>(StatusCode.INTERNAL_SERVER_ERROR.getCode(),StatusCode.INTERNAL_SERVER_ERROR.getMessage(), dataList);
        }


    }

    /**
     * 测试 验证发现 调用该接口 由于本地已经配置了vpn
     * 如果在代码中配置了代理 会导致报错 405 无法执行
     * 源码中 如果不设置代理 默认走direct 也会无法链接
     * 因此需要将代理置为null 这个时候 代码会走本地默认的vpn
     */
    @RequestMapping("/chatTestV4")
    @CrossOrigin
    public void chatTestV4()  {

         Proxy proxy =Proxys.http("127.0.0.1", 9090);

//        ChatGPTStream chatGPTStream = ChatGPTStream.builder()
//                .timeout(600)
//                .apiKey(token)
//                 .proxy(proxy)
//                .apiHost("https://api.openai.com")
//                .build()
//                .init();

        MyChatGPT chatGPT = MyChatGPT.builder()
                .timeout(600)
                .apiKey(token)
                .proxy(null)
//                .proxy(proxy)
                .apiHost("https://api.openai.com/")
                .build()
                .init();

//        OkHttpClient okHttpClient = chatGPT.getOkHttpClient();

//
//        Message system = Message.ofSystem("你现在是一个诗人，专门写七言绝句");
//        Message message1 = Message.of("写一段七言绝句诗，题目是：火锅！");

        String chat = chatGPT.chat("写一段七言绝句诗，题目是：火锅！");
        System.out.println(chat);

//        ChatCompletion chatCompletion = ChatCompletion.builder()
//                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
//                .messages(Arrays.asList(system, message1))
//                .maxTokens(3000)
//                .temperature(0.9)
//                .build();

//        ChatCompletionResponse response = chatGPTStream.chatCompletion(chatCompletion);
//        Message res = response.getChoices().get(0).getMessage();
//        System.out.println(res);

       /* SseEmitter sseEmitter = new SseEmitter(-1L);

        SseStreamListener listener = new SseStreamListener(sseEmitter);
        Message mes = Message.of(message);
        ChatCompletion chatCompletion = ChatCompletion.builder()
                 .model(ChatCompletion.Model.GPT_4.getName())
                .messages(Arrays.asList(mes))
                .build();

        listener.setOnComplate(msg -> {
            // System.out.println(msg);
            // sseEmitter.complete();
        });

//        chatGPTStream.streamChatCompletion(Arrays.asList(mes),listener);
        chatGPTStream.streamChatCompletion(chatCompletion,listener);*/

        // return sseEmitter;
    }


    /**
     * 流式情况下 进行上下文交互
     * @param prompt
     * @return
     */
    @RequestMapping("/chat_v4")
    @CrossOrigin
    public SseEmitter sseEmitter(String prompt){


        Proxy proxy =Proxys.http("127.0.0.1", 9090);

//        ChatGPTStream chatGPTStream = ChatGPTStream.builder()
//                .timeout(600)
//                .apiKey(token)
//                .proxy(null)
//                .apiHost("https://api.openai.com")
//                .build()
//                .init();

        MyChatGPT chatGPTStream = MyChatGPT.builder()
                .timeout(600)
                .apiKey(token)
                .proxy(null)
//                .proxy(proxy)
                .apiHost("https://api.openai.com/")
                .build()
                .init();

        SseEmitter sseEmitter = new SseEmitter(-1L);

        SseStreamListener listener = new SseStreamListener(sseEmitter);

        Message message = Message.of(prompt);

        listStream.add(message);
        listener.setOnComplate(msg -> {
            System.out.println(msg);
            if (!msg.equals("")){
                Message mes = Message.of(msg);
                listStream.add(mes);
            }
//             sseEmitter.send();
        });

        chatGPTStream.streamChatCompletion(listStream,listener);

        return sseEmitter;
    }

    /**
     * 该接口 非流式情况下 结合上下文进行交互
     * @param prompt
     * @return
     */
    @RequestMapping("/chat_v5")
    public ResponseData<String> getChatData (String prompt){

        MyChatGPT chatGPTStream = MyChatGPT.builder()
                .timeout(600)
                .apiKey(token)
                .proxy(null)
//                .proxy(proxy)
                .apiHost("https://api.openai.com/")
                .build()
                .init();

        Message messageQ = Message.of(prompt);
        list.add(messageQ);
        ChatCompletionResponse chatCompletionResponse = chatGPTStream.chatCompletion(list);

        Message message = chatCompletionResponse.getChoices().get(0).getMessage();
        String content = message.getContent();
        System.out.println(content);
        list.add(message);

        return new ResponseData<>(StatusCode.SUCCESS.getCode(),StatusCode.SUCCESS.getMessage(), content);


    }


    public static void main(String[] args) {

        String token ="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";
        Proxy proxy =Proxys.http("127.0.0.1", 9090);

//        ChatGPT chatGPT = ChatGPT.builder()
//                .timeout(600)
//                .apiKey(token)
////                .proxy(proxy)
//                .apiHost("https://api.openai.com/")
//                .build()
//                .init();


        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                .messages(Arrays.asList(Message.of("你好")))
                .build();

//        chatGPT.chatCompletion(chatCompletion);



        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new MyAuthenticationInterceptor(token))
                .connectionPool(new ConnectionPool(5, 1, TimeUnit.SECONDS))
                .readTimeout(Duration.ofSeconds(10).toMillis(), TimeUnit.MILLISECONDS)
                .build();



     /*   this.apiClient = new Retrofit.Builder()
                .baseUrl(this.apiHost)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(Api.class);*/

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);


    /*    ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(Arrays.asList(Message.of(message)))
                .build();
        ChatCompletionResponse response = this.chatCompletion(chatCompletion);
        return response.getChoices().get(0).getMessage().getContent();
        */
        Single<ChatCompletionResponse> chatCompletionResponseSingle = api.chatCompletion(chatCompletion);
        chatCompletionResponseSingle.blockingGet().getChoices().forEach(choice -> {
            System.out.println(choice.getMessage());
        });
        }



}
