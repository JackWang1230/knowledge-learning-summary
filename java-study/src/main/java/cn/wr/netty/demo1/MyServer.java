package cn.wr.netty.demo1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author : WangRui
 * @date : 2023/3/1
 */

public class MyServer {

    private final int port;
    private static final Logger logger = LoggerFactory.getLogger(MyServer.class);

    public MyServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.option(ChannelOption.SO_BACKLOG, 1024);
            sb.group(group, bossGroup) // 绑定线程池
                    .channel(NioServerSocketChannel.class) // 指定使用的channel
                    .localAddress(this.port)// 绑定监听端口
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            logger.info("-------------有客户端连接--------------");
                            logger.info("IP:" + ch.localAddress().getHostName());
                            logger.info("Port:" + ch.localAddress().getPort());

                            ch.pipeline().addLast(new MyDecoder());  //数据解析
                            ch.pipeline().addLast(new MyServerHandler());  //业务处理
                        }
                    });
            ChannelFuture cf = sb.bind().sync(); // 服务器异步创建绑定
            logger.info("socket服务端启动成功，监听端口： " + cf.channel().localAddress());
            System.out.println("socket服务端启动成功，监听端口： " + cf.channel().localAddress());
            cf.channel().closeFuture().sync(); // 关闭服务器通道
        } finally {
            group.shutdownGracefully().sync(); // 释放线程池资源
            bossGroup.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        new MyServer(8888).start(); // 启动
    }
}
