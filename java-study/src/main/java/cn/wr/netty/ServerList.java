package cn.wr.netty;

import java.util.List;

/**
 * @author : WangRui
 * @date : 2023/3/1
 */

public class ServerList {

    private static List<ServerInfo> servers;

    public static ServerInfo getServerInfo(int serverNo){
        return  servers.get(serverNo);
    }

    /**
     * 关闭服务
     * @param serverNo
     * @return
     */
    public static void shutDownGraceFully(int serverNo) {
        servers.get(serverNo).shutDownGraceFully();
    }
}
