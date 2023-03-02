package cn.wr.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * @author : WangRui
 * @date : 2023/3/1
 */

public class ServerInfo {

    /**
     * 底层通讯协议类型 TCP、UDP
     */
    private String protocolType;

    /**
     * 服务网关代理产品类型
     */
    private String productType;

    /**
     * 服务网关代理地址
     */
    private String ip;

    /**
     * 服务网关代理端口
     */
    private Integer port;

    /**
     * spring 应用配置上下文
     */
    private ApplicationContext ctx;

    private List<ChannelHandler> handlerList;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    public List<ChannelHandler> getHandlerList() {
        return handlerList;
    }

    public void setHandlerList(List<ChannelHandler> handlerList) {
        this.handlerList = handlerList;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public void setBossGroup(EventLoopGroup bossGroup) {
        this.bossGroup = bossGroup;
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public void setWorkerGroup(EventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
    }

    public void shutDownGraceFully() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
