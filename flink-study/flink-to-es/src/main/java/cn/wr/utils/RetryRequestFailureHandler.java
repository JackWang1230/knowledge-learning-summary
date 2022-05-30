package cn.wr.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.connectors.elasticsearch.ActionRequestFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.util.ExceptionUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.bytes.BytesReference;
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
    public void onFailure(ActionRequest actionRequest, Throwable throwable, int restStatusCode, RequestIndexer requestIndexer) throws Throwable {

        // 异常1: ES队列满了(Reject异常)，放回队列
        if (ExceptionUtils.findThrowable(throwable, EsRejectedExecutionException.class).isPresent()) {
            log.error("### RetryRequestFailureHandler EsRejectedExecutionException actionRequest:{}, onFailure:{}",
                    actionRequest.toString(), throwable);
            requestIndexer.add(new ActionRequest[]{actionRequest});

            // todo 新版本使用案例 待验证
           /* IndexRequest indexAction = (IndexRequest)actionRequest;
            String index = indexAction.index();
            String type = indexAction.type();
            String id = indexAction.id();
            BytesReference source = indexAction.source();
            IndexRequest newIndex = Requests.indexRequest().index(index).type(type).id(id).source(source);
            requestIndexer.add(newIndex);*/

        }
        else if (ExceptionUtils.findThrowable(throwable, ElasticsearchException.class).isPresent()) {
            log.error("### RetryRequestFailureHandler ElasticsearchException actionRequest:{}, onFailure:{}",
                    actionRequest.toString(), throwable);
            // 批量请求失败 例如(批量插入不存在的的文档(index),会出现死锁现象v7.3.0版本之前
            // Scheduler只配置了1个工作线程，该线程可能在Flush方法中被阻塞。Flush的run方法内加了BulkProcessor.this同步锁，
            // 但internalAdd方法也被同一个对象锁定。可能发生的情况是，大量请求通过但internalAdd来获得了锁，
            // 但发送这批请求时遇到了错误，因此重试逻辑开始了。由于scheduler线程阻塞在Flush方法里，
            // 导致internalAdd被同步锁定，因此现在当Retry尝试安排重试时无法进行重试，由于Flush正在阻塞scheduler的唯一工作线程。
            // 进一步导致这里Flush由于正在等待internalAdd完成而无法继续，并且internalAdd无法结束因为他在等待Retry，
            // 但是Retry也无法结束因为他在等待schedule线程，该线程无法获得，因为它正在等待Flush完成

            // 此时返回的状态404 在这种情况下, 这种数据直接舍弃
            if( 404 != restStatusCode){
                requestIndexer.add(new ActionRequest[]{actionRequest});
            }

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
