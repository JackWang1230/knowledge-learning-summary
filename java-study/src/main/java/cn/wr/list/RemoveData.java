package cn.wr.list;

import org.junit.Test;

import java.util.ArrayList;

/**
 * @author RWang
 * @Date 2022/3/4
 */

public class RemoveData {


    @Test
    public void removeElems() throws InterruptedException {

        ArrayList<String> last = new ArrayList<>();
        while (true) {
            ArrayList<String> current = new ArrayList<>();
            // 最开始的时候 是没有值的
            // 第一次之后，数据会被先存入current ,之后进行比较last的是否有值
            // 如果在last中有值，说明上一个节点检测的数据，在当前节点不存在了，此时报警
            // 之后将last的数据清空，之后再将current的数据存入last中，并清空curent中数据
            // 当第二次来到的时候，依旧是先将新的数据存入current,之后比较last中的数据
            // 后续逻辑一致
            current.add("ab");
            current.add("bc");

//        last.add("ab");
//        last.add("bc");
//        last.add("aba");

            last.removeAll(current);
            if (last.size() > 0) {
                // 有数据
                for (String s : last) {
                    System.out.println(s + " 任务挂了");
                }
                last.clear();
                last.addAll(current);
                current.clear();
                System.out.println("eewew");
            } else {
                last.addAll(current);
                current.clear();
                System.out.println("dd");
            }

            Thread.sleep(5000);
        }

    }
}
