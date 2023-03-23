package cn.wr.netty.demo3.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 *
 * 接收客户端数据并解码
 * @author : WangRui
 * @date : 2023/3/2
 */

public class MyServerDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {

        if (in.readableBytes()>0){
            System.out.println(">>>my server decoder");
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            System.out.println(">>>"+ new String(bytes));
            String decodeMsg = new String(bytes).toUpperCase(); // 收到客户端的消息字母转大写
            System.out.println(">>>decodeMsg:"+decodeMsg);
            list.add(decodeMsg);
        }
    }
}
