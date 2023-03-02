package cn.wr.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.List;

/**
 * @author : WangRui
 * @date : 2023/3/1
 */

public class DefaultTcpServerInitializer extends ChannelInitializer<SocketChannel> {

    private  int serverNo;
    public DefaultTcpServerInitializer(int serverNo) {

        this.serverNo = serverNo;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {

        ChannelPipeline pipeline = channel.pipeline();

        List<ChannelHandler> channelHandlerList = ServerList.getServerInfo(serverNo).getHandlerList();

        for (ChannelHandler channelHandler : channelHandlerList) {
            pipeline.addLast(channelHandler);
        }
    }
}
