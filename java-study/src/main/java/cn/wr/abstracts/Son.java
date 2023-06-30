package cn.wr.abstracts;

import org.apache.flink.api.common.time.Time;

/**
 * @author RWang
 * @Date 2022/2/11
 */

public abstract class Son<T> implements Parent<T> {

    private long aaa;

    public Son(Time ss) {
        this.aaa = Long.MIN_VALUE + ss.toMilliseconds();
    }

    /***
     * 子类继承的父类为抽象类 必须重写其抽象方法
     * @param element
     * @return
     */
    public abstract long print(T element);


    /**
     * final 修饰的方法 不能被重写
     * @param dd
     * @param aa
     * @return
     */
    @Override
    public final long print(T dd, long aa) {
        long print = print(dd);
        if (print > aaa) {
            aaa = print;
        }
        return print;
    }
}
