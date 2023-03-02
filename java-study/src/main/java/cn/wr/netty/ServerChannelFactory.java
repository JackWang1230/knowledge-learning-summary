package cn.wr.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * @author : WangRui
 * @date : 2023/3/1
 */

public class ServerChannelFactory {

    public Channel createAcceptorChannel(int serverNo) throws Exception {


        ServerInfo serverInfo = ServerList.getServerInfo(serverNo);

        ServerBootstrap serverBootStrap = ServerBootstrapFactory.createServerBootStrap(serverNo);

        ServerBootstrap serverBootstrap = serverBootStrap.childHandler(new DefaultTcpServerInitializer(serverNo));

        try {
            ChannelFuture channelFuture = null;
            channelFuture = serverBootstrap.bind(serverInfo.getPort()).sync();
            channelFuture.awaitUninterruptibly();

            if (channelFuture.isSuccess()) {
                return channelFuture.channel();
            } else {
                String errMsg = "failed to open socket! cannot bind to port: ";
                throw new Exception(errMsg);
            }
        } catch (Exception e){
            throw new Exception();
        }

    }
}
