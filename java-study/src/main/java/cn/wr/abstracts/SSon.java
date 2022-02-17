package cn.wr.abstracts;

import org.apache.flink.api.common.time.Time;

/**
 * @author RWang
 * @Date 2022/2/11
 */

public class SSon<T> extends Son<T>{

    public SSon(Time ss) {
        super(ss);
    }

    @Override
    public long print(T element) {
        String s = element.toString();
        return 0;
    }

    public static void main(String[] args) {
        SSon<String> stringSSon = new SSon<>(Time.seconds(100));
        stringSSon.print("2000");

        Son<Long> son = new Son<Long>(Time.seconds(200)) {
            @Override
            public long print(Long element) {
                return element;
            }
        };
        son.print(100L,300);
    }


}
