package cn.wr.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.connectors.elasticsearch.ActionRequestFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.util.ExceptionUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.common.util.concurrent.EsRejectedExecutionException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Optional;

/**
 * @author RWang
 * @Date 2022/5/13
 */

@Slf4j
public class RetryRequestFailureHandler implements ActionRequestFailureHandler {

    private static final long serialVersionUID = -2566485770857427178L;

    public RetryRequestFailureHandler() {
    }

    @Override
    public void onFailure(ActionRequest actionRequest, Throwable throwable, int i, RequestIndexer requestIndexer) throws Throwable {

        // 异常1: ES队列满了(Reject异常)，放回队列
        if (ExceptionUtils.findThrowable(throwable, EsRejectedExecutionException.class).isPresent()) {
            log.error("### RetryRequestFailureHandler EsRejectedExecutionException actionRequest:{}, onFailure:{}",
                    actionRequest.toString(), throwable);
            requestIndexer.add(new ActionRequest[]{actionRequest});
        }
        else if (ExceptionUtils.findThrowable(throwable, ElasticsearchException.class).isPresent()) {
            log.error("### RetryRequestFailureHandler ElasticsearchException actionRequest:{}, onFailure:{}",
                    actionRequest.toString(), throwable);
            requestIndexer.add(new ActionRequest[]{actionRequest});
        }
        else {
            // 异常2: ES超时异常(timeout异常)，放回队列
            if (ExceptionUtils.findThrowable(throwable, SocketTimeoutException.class).isPresent()) {
                log.error("### RetryRequestFailureHandler SocketTimeoutException actionRequest:{}, onFailure:{}",
                        actionRequest.toString(), throwable);
                requestIndexer.add(new ActionRequest[]{actionRequest});
                return;
            }
            // 异常3: ES语法异常，丢弃数据，记录日志
            else {
                Optional<IOException> exp = ExceptionUtils.findThrowable(throwable, IOException.class);
                if (exp.isPresent()) {
                    IOException ioExp = exp.get();
                    if (ioExp.getMessage() != null && ioExp.getMessage().contains("max retry timeout")) {
                        log.error("### RetryRequestFailureHandler IOException actionRequest:{}, onFailure:{}",
                                actionRequest.toString(), ioExp.getMessage());
                        requestIndexer.add(new ActionRequest[]{actionRequest});
                        return;
                    }
                }
                log.error("### RetryRequestFailureHandler UnHandleException, actionRequest:{}, error:{}",
                        actionRequest.toString(), throwable);
            }
            throw throwable;
        }
    }
}
