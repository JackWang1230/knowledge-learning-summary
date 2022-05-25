package cn.wr.spring;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Future;

/**
 * @author RWang
 * @Date 2022/5/25
 */

@EnableAsync
public class AsyncTest {


    @Async
    public Future<String> sayHello() throws InterruptedException{

        int thinking=2;
        return new AsyncResult<String>("d");
    }
}
