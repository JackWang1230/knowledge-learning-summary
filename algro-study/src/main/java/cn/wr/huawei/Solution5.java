package cn.wr.huawei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 现在有多组整数数组，需要将他们合并成一个新的数组，合并规则从每个数组里按顺序取出固定长度的内容
 合并到新的数组，取完的内容会删除掉，如果改行不足固定长度，或者已经为空，则直接取出剩余部分的内容放到新的数组中继续下一行

 输入描述
 第一 行每次读取的固定长度，长度0<len<10
 第二行是整数数组的数目，数目 0<num<10000
 第3~n行是需要合并的数组，不同的数组用换行分割，元素之间用逗号分割，最大不超过100个元素

 输出描述
 输出一个新的数组，用逗号分割

 示例1
 输入
 3
 2
 2,5,6,7,9,5,7
 1,7,4,3,4
 输出
 2,5,6,1,7,4,7,9,5,3,4,7
 *
 * @author : WangRui
 * @date : 2022/10/11
 */

public class Solution5 {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        int n = Integer.parseInt(in.nextLine());
        int m = Integer.parseInt(in.nextLine());

        // 数据存储
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        int sum=0;
        for (int i = 0; i < m; i++) {
            String[] split = in.nextLine().split(",");
            sum += split.length;
            ArrayList<String> list1 = new ArrayList<>(Arrays.asList(split));
            list.add(list1);
        }

        // 处理数据
        ArrayList<String> res = new ArrayList<>();
        while (res.size()!= sum){
            // 因此m个序列
            for (ArrayList<String> s : list) {
                if(s.size()==0) continue;
                int times = Math.min(s.size(),n);
                while (times>0){
                    res.add(s.remove(0));
                    times--;
                }
            }
        }

        // 组装数据
        StringBuilder sb = new StringBuilder();
        for (String re : res) {
            sb.append(re).append(",");
        }
        System.out.println(sb.substring(0,sb.toString().length()-1));


    }
}
