package cn.wr.builder;

/**
 * 借鉴好的设计模式
 *
 * @author RWang
 * @Date 2022/1/19
 */

public class B {

    public static A builder() {
        return new A();
    }

    public static void main(String[] args) {
        A builder = B.builder();
    }
}
