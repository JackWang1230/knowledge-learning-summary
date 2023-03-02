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

    public abstract long print(T element);

    @Override
    public final long print(T dd, long aa) {
        long print = print(dd);
        if (print > aaa) {
            aaa = print;
        }
        return print;
    }
}
