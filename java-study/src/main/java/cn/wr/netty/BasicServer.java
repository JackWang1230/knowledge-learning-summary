package cn.wr.netty;

import io.netty.channel.Channel;

/**
 * @author : WangRui
 * @date : 2023/3/1
 */

public class BasicServer implements IServer{


    @Override
    public void start(int serverNo) throws Exception {

        try {
            Channel acceptorChannel = new ServerChannelFactory().createAcceptorChannel(serverNo);
            acceptorChannel.closeFuture().sync();
        }finally {

            //优雅退出，释放线程组资源
            ServerList.shutDownGraceFully(serverNo);
        }

    }

    @Override
    public void stop(int serverNo) throws Exception {

    }

    @Override
    public void restart(int serverNo) throws Exception {

    }
}
