package cn.wr;

import java.io.File;

/**
 * @author : WangRui
 * @date : 2022/1/12
 */

public class JavaHelloWorld {
    public static void main(String[] args) {

        String index = "http://192.168.1.124:8080/register";

        int register = index.indexOf("register");
        System.out.println(register);


        System.out.println("hello world");

        // File f = new File("src/conf/abc.txt");
        File f = new File("/Users/wangrui/Documents/coding/knowledge-learning-summary/java-study/src/test/java/cn/wr/abc.txt");
        System.out.println("是");
    }
}
