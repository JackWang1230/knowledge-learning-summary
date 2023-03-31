package com.wr.constant;

/**
 * @author : WangRui
 * @date : 2023/3/27
 */

public class ConstantPool {

    /**
     * 随机生成uuid的key名
     */
    public static final String USER_UUID_KEY = "user_uuid";
    /**
     * 发送指令：连接
     */
    public static final String WEBSSH_OPERATE_CONNECT = "connect";
    /**
     * 发送指令：命令
     */
    public static final String WEBSSH_OPERATE_COMMAND = "command";
    /**
     * 发送指令：断开连接
     */
    public static final String WEBSSH_OPERATE_DISCONNECT = "disconnect";
    /**
     * 发送指令：心跳
     */
    public static final String WEBSSH_OPERATE_HEARTBEAT = "heartbeat";
    /**
     * 发送指令：重连
     */
    public static final String WEBSSH_OPERATE_RECONNECT = "reconnect";

}
