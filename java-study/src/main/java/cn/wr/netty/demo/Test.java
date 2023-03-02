package cn.wr.netty.demo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author : WangRui
 * @date : 2023/3/1
 */

public class Test {


    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(10086);

        while (true){
            Socket socket = serverSocket.accept();
            System.out.println("accept");

//            byte[] bytes = ("ddddd").getBytes();
            byte[] resp = ("a@$@bbbbb@$@cccccccccc@$@ddddddddddddddddddddddddddddddddddddddd@$@33333333333@$@4              v@$@").getBytes();
//                InputStream inputStream = new FileInputStream(new File("D:/proj/netty/tcpradio.dat"));
//            InputStream inputStream = new FileInputStream(new File("tcpradio.dat"));
            OutputStream outputStream = socket.getOutputStream();
            byte[] readBuf = new byte[10240];
            int readLen = resp.length;
//            readLen= inputStream.read(readBuf,0,10240);
//            System.out.println(readLen);
            outputStream.write(resp,0,readLen);
//            inputStream.close();
            outputStream.close();
        }
    }
}
