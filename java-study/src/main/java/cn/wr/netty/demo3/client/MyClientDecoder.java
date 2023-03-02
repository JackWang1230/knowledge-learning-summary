package cn.wr.netty.demo3.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Base64;
import java.util.List;

/**
 * @author : WangRui
 * @date : 2023/3/2
 */

public class MyClientDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes()>0){

            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            System.out.println("### before decode:"+ new String(bytes));
            String decodeMsg =new String(Base64.getDecoder().decode(bytes));
            System.out.println("after decode:"+ decodeMsg);
            out.add(decodeMsg);
        }
    }
}
