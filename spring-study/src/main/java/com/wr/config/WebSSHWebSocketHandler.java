package com.wr.config;

import com.wr.service.WebSSHService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.logging.Logger;

/**
 * @author : WangRui
 * @date : 2023/3/27
 */
@Component
public class WebSSHWebSocketHandler implements WebSocketHandler {

    private Logger logger = Logger.getLogger(WebSSHWebSocketHandler.class.getName());


    @Autowired
    private WebSSHService webSSHService;


    /**
     * 用户链接上webSocket时触发回调
     * @param webSocketSession
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {

        webSSHService.initConnection(webSocketSession);
    }

    /**
     * 接收到消息时触发回调
     * @param webSocketSession
     * @param webSocketMessage
     * @throws Exception
     */
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {


        if (webSocketMessage instanceof TextMessage) {
            webSSHService.receiveMessage(webSocketSession, ((TextMessage) webSocketMessage).getPayload());
        }else if (webSocketMessage instanceof BinaryMessage) {

        }else if (webSocketMessage instanceof PongMessage) {

        }else {
            throw new IllegalStateException("Unexpected WebSocket message type: " + webSocketMessage);
        }
    }

    /**
     * 发生异常时触发回调
     * @param webSocketSession
     * @param throwable
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

        logger.info("handleTransportError");
    }

    /**
     * 关闭连接时触发回调
     * @param webSocketSession
     * @param closeStatus
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {

        webSSHService.closeConnection(webSocketSession);
    }

    /**
     * 是否分段发送消息
     * @return
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
