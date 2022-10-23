package cn.wr.huawei;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 *  统计射击比赛成绩
 *
 * 示例一
 *       输入:
 *         13
 *         3,3,7,4,4,4,4,7,7,3,5,5,5
 *         53,80,68,24,39,76,66,16,100,55,53,80,55
 *       输出:
 *         5,3,7,4
 *
 * @author : WangRui
 * @date : 2022/10/11
 */

public class Solution2 {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        int n = Integer.parseInt(in.nextLine());
        List<Integer> ids = toIntList(in.nextLine());
        List<Integer> scores = toIntList(in.nextLine());
        in.close();

        HashMap<Integer, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            Integer id = ids.get(i);
            Integer score = scores.get(i);
            // getOrDefault语法是如果有这个key 就使用这个key的value 否则使用默认值
            List<Integer> list = map.getOrDefault(id, new LinkedList<>());
            list.add(score);
            map.put(id,list);
        }

        StringBuilder builder = new StringBuilder();

        // sorted o1是最后面的元素，o2是倒数第二个元素
        // 如果返回正数 就是顺序排 如果是 返回负数 就是逆序排
        // 总结 1)如果降序的化 o2-o1
        //     2)如果升序的化 o1-o2
        map.entrySet().stream().filter(a->a.getValue().size()>=3)
                // o1是最后的元素 o2是倒数第二个元素 从后往前
                .sorted(((o1, o2) -> {
                    Integer sum1 = sumT3(o1.getValue());
                    Integer sum2 = sumT3(o2.getValue());
                    if (sum1.equals(sum2)){
                        // 如果o1=5 ，o2=3
                        // 因为 o2-o1 是负数 所以逆序排 所以
                        // o1被排在了前面
                     return o2.getKey()-o1.getKey();
                    }else {
                      return   sum2-sum1;
                    }
                })).map(Map.Entry::getKey)
                .forEach(x->builder.append(x).append(","));
        System.out.println(builder.substring(0, builder.length() - 1));

    }

    /**
     * 字符串转换成数组
     * @param str
     * @return
     */
    public static List<Integer> toIntList(String str){
        return Arrays.stream(str.split(",")).map(Integer::getInteger)
                .collect(Collectors.toList());
    }

    public static Integer sumT3(List<Integer> list){

        // 先排序 之后取成绩最高的三个成绩
        list.sort(Integer::compareTo);
        int sum=0;
        // 取出最后三个
        for (int i = list.size()-1; i >list.size()-3 ; i--) {
            sum+=list.get(i);
        }
        return sum;
    }
}
