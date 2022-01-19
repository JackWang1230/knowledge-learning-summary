package cn.wr.builder;

import java.util.Properties;

/**
 * @author RWang
 * @Date 2022/1/19
 */

public class A {

    private String a;
    private Properties props;

    public A setProps(Properties props){
        this.props=props;
        return this;
    }

    public A setA(String a){
        this.a = a;
        return this;
    }
    public void getNums(){
        System.out.println("11");
    }


    public static void main(String[] args) {
        A a = new A();
        A s = a.setA("s");
    }
}
