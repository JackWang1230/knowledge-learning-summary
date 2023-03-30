package com.wr.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;


public interface WebSSHService {

    /**
     * 初始化连接
     * @param webSocketSession
     */
    public void initConnection(WebSocketSession webSocketSession);


    /**
     * 关闭连接
     * @param webSocketSession
     */
    public void closeConnection(WebSocketSession webSocketSession);

    /**
     * 发送消息
     * @param webSocketSession
     * @param buffer
     */
    public void sendMessage(WebSocketSession webSocketSession, byte[] buffer) throws IOException;

    /**
     * 接收消息
     * @param webSocketSession
     * @param buffer
     */
    public void receiveMessage(WebSocketSession webSocketSession, String buffer);

    public void close(WebSocketSession session);

}
