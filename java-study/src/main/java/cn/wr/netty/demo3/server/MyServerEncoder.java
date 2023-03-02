package cn.wr.netty.demo3.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Base64;

/**
 * @author : WangRui
 * @date : 2023/3/2
 */

public class MyServerEncoder extends MessageToByteEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, String s, ByteBuf byteBuf) throws Exception {

        if (s != null){

            System.out.println(">>>my server encoder");
            System.out.println(">>>"+ s);
            byte[] encodeMsg = Base64.getEncoder().encode(s.getBytes()); // 给客户端发消息用Base64编码
            System.out.println(">>>encodeMsg:"+ new String(encodeMsg));
            byteBuf.writeBytes(encodeMsg); // 代表发送客户端加密后数据
        }
    }
}
