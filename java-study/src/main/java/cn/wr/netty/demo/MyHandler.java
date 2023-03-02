package cn.wr.netty.demo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * @author : WangRui
 * @date : 2023/3/1
 */

public class MyHandler extends ChannelInboundHandlerAdapter {

    private int count = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        ByteBuf byteBuf = (ByteBuf) msg;
        // 判断报文长度
        int bufLen = byteBuf.readableBytes();
        //当前处理的是第几个字节开始的报文
        System.out.println("pos:" + count);
        //统计已处理的所有报文字节长度
        count += bufLen;

        System.out.println("msg:" + bufLen);

        //业务的报文处理

        //必须释放，如果继承simplechannelinboundhandler会自动释放，但是报文处理写在channelRead0
        ReferenceCountUtil.release(msg);
    }

}
