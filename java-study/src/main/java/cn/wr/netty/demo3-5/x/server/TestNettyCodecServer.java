package cn.wr.netty.demo3.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author : WangRui
 * @date : 2023/3/1
 */

public class TestNettyCodecServer {


    public static void main(String[] args) {

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        serverBootstrap.group(bossGroup,workGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.option(ChannelOption.SO_KEEPALIVE,true);
        serverBootstrap.option(ChannelOption.TCP_NODELAY,true);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                ChannelPipeline p = channel.pipeline();
                p.addLast(new cn.wr.netty.demo3.server.MyServerDecoder());
                p.addLast(new cn.wr.netty.demo3.server.MyServerEncoder());
                p.addLast(new cn.wr.netty.demo3.server.MyServerHandler());
            }
        });
        try {
            ChannelFuture future = serverBootstrap.bind(1234).sync();
            if (future.isSuccess()){
                System.out.println("服务端启动成功");
            }
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }
}
