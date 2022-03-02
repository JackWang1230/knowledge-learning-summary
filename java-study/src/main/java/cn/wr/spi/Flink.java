package cn.wr.spi;

/**
 * @author RWang
 * @Date 2022/3/2
 */

public class Flink implements PersonFactory {
    @Override
    public void eat() {
        System.out.println("flink eat");
    }
}
