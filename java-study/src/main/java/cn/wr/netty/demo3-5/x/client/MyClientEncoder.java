package cn.wr.netty.demo3.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author : WangRui
 * @date : 2023/3/2
 */

public class MyClientEncoder extends MessageToByteEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {

        if (msg != null){
            System.out.println("### before encode:"+ msg);
            msg = "["+msg+"]";
            System.out.println("after encode:"+ msg);
            out.writeBytes(msg.getBytes());
        }
    }
}
