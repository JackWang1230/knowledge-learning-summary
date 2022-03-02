package cn.wr.spi;

/**
 * @author RWang
 * @Date 2022/3/2
 */

public class Spark implements PersonFactory {
    @Override
    public void eat() {
        System.out.println("spark eat");
    }
}
