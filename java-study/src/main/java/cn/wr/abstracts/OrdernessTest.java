package cn.wr.abstracts;

import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * @author RWang
 * @Date 2022/2/11
 */

public class OrdernessTest extends BoundedOutOfOrdernessTimestampExtractor<String> {


    public OrdernessTest(Time maxOutOfOrderness) {
        super(Time.seconds(200));
    }

    @Override
    public long extractTimestamp(String element) {
        return 0;
    }

    public static void main(String[] args) {
        BoundedOutOfOrdernessTimestampExtractor<String> sss = new BoundedOutOfOrdernessTimestampExtractor<String>(Time.seconds(200)) {
            private static final long serialVersionUID = -2180480552089128364L;

            @Override
            public long extractTimestamp(String element) {
                return 0;
            }
        };
    }
}
