package cn.wr.algro.others;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author : WangRui
 * @date : 2023/2/3
 */

public class GreedyAlgorithm {

    /**
     * 构建广播电台 与 覆盖的地区
     * @return
     */
    public Map<String, Set<String>> buildBroadcasts(){

        HashMap<String, Set<String>> broadcasts = new HashMap<>();
        HashSet<String> k1 = new HashSet<>();
        k1.add("北京");
        k1.add("上海");
        k1.add("天津");
        Set<String> k2 = new HashSet<>();
        k2.add("广州");
        k2.add("北京");
        k2.add("深圳");
        Set<String> k3 = new HashSet<>();
        k3.add("成都");
        k3.add("上海");
        k3.add("杭州");
        Set<String> k4 = new HashSet<>();
        k4.add("上海");
        k4.add("天津");
        Set<String> k5 = new HashSet<>();
        k5.add("杭州");
        k5.add("大连");

        broadcasts.put("k1", k1);
        broadcasts.put("k2", k2);
        broadcasts.put("k3", k3);
        broadcasts.put("k4", k4);
        broadcasts.put("k5", k5);

        return broadcasts;
    }

    public Set<String> greedy(Map<String,Set<String>> broadcasts){

        Set<String> allAreas = new HashSet<>();
        broadcasts.forEach((k,v)->allAreas.addAll(v));

        System.out.println("需要覆盖的地区: " + allAreas);

        // 存放已选择的电台
        Set<String> selects = new HashSet<>();

        // 当所有需要覆盖的地区还有时，则可以继续选择
        String maxKey = null; // 当次覆盖地区最多的电台
        int maxKeyCoverNum = 0; // maxKey 覆盖的数量
        Set<String> temp = new HashSet<>();  // 临时变量，用于计算电台中的覆盖地区：在要未覆盖地区中  覆盖的数量
        while (!allAreas.isEmpty()) {
            // 选择出当次还未选择中：覆盖地区最多的电台
            for (String key : broadcasts.keySet()) {
                Set<String> areas = broadcasts.get(key);
                temp.addAll(areas);
                temp.retainAll(allAreas);
                // 如果：当前尝试选择的电台，覆盖数量比 maxKey 还大，则把它设置为 maxKey
                if (temp.size() > 0 && temp.size() > maxKeyCoverNum) {
                    maxKey = key;
                    maxKeyCoverNum = temp.size();
                }
                temp.clear();
            }
            if (maxKey == null) {
                continue;
            }
            // 循环完成后，找到了本轮的 maxKey
            // 添加到已选择列表中，并且从 未覆盖列表 中删除已经覆盖过的地区
            selects.add(maxKey);
            allAreas.removeAll(broadcasts.get(maxKey));
            // 清空临时变量，方便下次查找
            maxKey = null;
            maxKeyCoverNum = 0;
        }
        return selects;
    }

    @Test
    public void fun() {
        Map<String, Set<String>> broadcasts = buildBroadcasts();
        System.out.println("电台列表" + broadcasts);
        Set<String> greedy = greedy(broadcasts);
        System.out.println("选择好的电台列表：" + greedy);
    }
}
