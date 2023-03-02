package cn.wr.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author RWang
 * @Date 2022/3/2
 */

public class TestSpi {

    /**
     * SPI Service Provide Interface(flink connector中大量使用该模式)
     *
     * @param args
     */
    public static void main(String[] args) {
        ServiceLoader<PersonFactory> load = ServiceLoader.load(PersonFactory.class);
        Iterator<PersonFactory> iterator = load.iterator();
        while (iterator.hasNext()) {
//            PersonFactory next = iterator.next();

            iterator.next().eat();
        }
    }
}
