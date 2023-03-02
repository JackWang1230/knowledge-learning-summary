package cn.wr.netty;

import io.netty.channel.ChannelHandler;

import java.util.List;

public interface IServer {

    public void start(int serverNo) throws Exception;

    public void stop(int serverNo) throws Exception;

    public void restart(int serverNo) throws Exception;
}
