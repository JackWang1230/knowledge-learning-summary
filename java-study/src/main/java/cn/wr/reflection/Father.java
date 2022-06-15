package cn.wr.reflection;

import lombok.Data;

/**
 * @author RWang
 * @Date 2022/6/7
 */
@Data
public class Father {

    private String name;
    private String sex;
    private int age;
    public String job;

    public void getSon(){
        System.out.println("born boy");
    }
}
