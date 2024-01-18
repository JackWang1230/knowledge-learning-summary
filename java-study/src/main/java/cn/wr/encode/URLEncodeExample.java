package cn.wr.encode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author : WangRui
 * @date : 2024/1/10
 */

public class URLEncodeExample {

    public static void main(String[] args)  {

        try {

            String originalString = "茅台酒53度飞天";
            String encode = URLEncoder.encode(originalString, "UTF-8");
            System.out.println(encode);
        }catch (UnsupportedEncodingException e){
            System.out.println(e.getMessage());
        }
    }
}
