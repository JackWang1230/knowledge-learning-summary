package cn.wr.netty.demo3.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

/**
 *
 * 将解码完成后的数据 进行处理
 * @author : WangRui
 * @date : 2023/3/2
 */

public class MyServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("received"+msg);
        ctx.writeAndFlush("response date:"+ new Date()); // 收到消息给客户端发回一条到encode方法进行加密

        //  byteBuf.writeBytes(encodeMsg); 代表发送客户端
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("+Active:"+ ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("+Inactive:" + ctx);
    }
}
