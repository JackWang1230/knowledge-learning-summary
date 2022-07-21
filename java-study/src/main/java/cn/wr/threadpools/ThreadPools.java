package cn.wr.threadpools;


import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author RWang
 * @Date 2022/3/4
 */

public class ThreadPools {

    @Test
    public void testThreadPools(){
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(3);
        pool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                System.out.println("dddsds");
            }
        }, 0, 3000, TimeUnit.MILLISECONDS);

    }


    public static void main(String[] args) {

        ArrayList<String> last = new ArrayList<>();
        ScheduledExecutorService service = Executors.newScheduledThreadPool(4);
        service.scheduleAtFixedRate(()->{
            ArrayList<String> current = new ArrayList<>();
            current.add("ab");
            current.add("bc");

            last.removeAll(current);
            if (last.size()>0){
                // 有数据
                for (String s : last) {
                    System.out.println(s+" 任务挂了");
                }
                last.clear();
                last.addAll(current);
                current.clear();
                System.out.println("eewew");
            }else {
                last.addAll(current);
                current.clear();
                System.out.println("无异常");
            }
            System.out.println(Thread.currentThread().getName());
        }, 0, 1, TimeUnit.MINUTES);




    }

}
