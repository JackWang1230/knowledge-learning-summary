package cn.wr.bloomfilter;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import redis.clients.jedis.Jedis;

import java.sql.Timestamp;

/**
 * @author : WangRui
 * @date : 2022/10/10
 */

public class UserVisitorWindowFunc extends ProcessWindowFunction<UserBehavior,UserVisitorCount,String, TimeWindow> {

    //声明Redis连接
    private Jedis  jedis;

    //声明布隆过滤器
    private MyBloomFilter myBloomFilter;

    //声明每个窗口总人数的key
    private String hourUVCountKey;


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        jedis = new Jedis("hadoop102",6379);
        hourUVCountKey = "HourUV";
        myBloomFilter = new MyBloomFilter(1 << 30); //2^30
    }

    @Override
    public void process(String s, Context context, Iterable<UserBehavior> elements, Collector<UserVisitorCount> out) throws Exception {

        // 取出数据
        UserBehavior userBehavior = elements.iterator().next();
        //2.提取窗口信息
        String windowEnd = new Timestamp(context.window().getEnd()).toString();

        //3.定义当前窗口的BitMap Key
        String bitMapKey = "BitMap_" + windowEnd;
        //4.查询当前的UID是否已经存在于当前的bitMap中
        long offset = myBloomFilter.getOffset(userBehavior.getUserId().toString());
        Boolean exists = jedis.getbit(bitMapKey, offset);

        //5.根据数据是否存在做下一步操作
        if (!exists){
            //将对应offset位置改为1
            jedis.setbit(bitMapKey,offset,true);
            //累加当前窗口的综合
            jedis.hincrBy(hourUVCountKey,windowEnd,1);
        }
        //输出数据
        String hget = jedis.hget(hourUVCountKey, windowEnd);
        out.collect(new UserVisitorCount("UV",windowEnd,Integer.parseInt(hget)));
    }
}
