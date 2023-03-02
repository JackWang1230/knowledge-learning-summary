package cn.wr.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author : WangRui
 * @date : 2023/3/1
 */

public class ServerBootstrapFactory {
    public ServerBootstrapFactory() {
    }

    public static ServerBootstrap createServerBootStrap(int serverNo ) {

        ServerInfo serverInfo = ServerList.getServerInfo(serverNo);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        serverBootstrap.group(bossGroup, workerGroup);
        serverInfo.setBossGroup(bossGroup);
        serverInfo.setWorkerGroup(workerGroup);

        serverBootstrap.channel(NioServerSocketChannel.class);
        //连接的最大队列长度。如果队列满时收到连接指示，则拒绝该连接，建议生产代码适当放大数值
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        return serverBootstrap;

    }
}
