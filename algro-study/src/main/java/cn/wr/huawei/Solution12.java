package cn.wr.huawei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 *
 *  给定一个字符串
 *     只包含大写字母
 *     求在包含同一字母的子串中
 *     长度第K长的子串
 *     相同字母只取最长的子串
 *
 *     输入
 *      第一行 一个子串 1<len<=100
 *      只包含大写字母
 *      第二行为k的值
 *
 *      输出
 *      输出连续出现次数第k多的字母的次数
 *
 *      例子：
 *      输入
 *              AABAAA
 *              2
 *      输出
 *              1
 *        同一字母连续出现最多的A 3次
 *        第二多2次  但A出现连续3次
 *
 *     输入
 *
 *     AAAAHHHBBCDHHHH
 *     3
 *
 *     输出
 *     2
 *
 * //如果子串中只包含同一字母的子串数小于k
 *
 * 则输出-1
 *
 * @author : WangRui
 * @date : 2022/10/12
 */

public class Solution12 {

    public static void main(String[] args) {

        //  AABAAA
        //    2
        // a 2 a 3 b 1
        // 排序 输出1
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        int k = Integer.parseInt(sc.nextLine());
        char[] chars = s.toCharArray();
        HashMap<Character, Integer> map = new HashMap<>();

        // 设定默认值
        char currValue = chars[0];
        // 设定计数值
        int count = 0;
        for (int i = 0; i < chars.length; i++) {
            if (currValue == chars[i]){
                count++;
            }else {
                // 当不等以后
                currValue = chars[i];
                count = 1;
            }
            // 将数据存入map集合
            map.put(currValue,map.containsKey(currValue)?
                    Math.max(map.get(currValue),count):count);
        }

        // 将数据存入数组中
        ArrayList<Map.Entry<Character, Integer>> entries = new ArrayList<>(map.entrySet());
        // 降序排列
        entries.sort((o1,o2)->o2.getValue()-o1.getValue());
        if (k> entries.size()){
            System.out.println("-1");
        }else {
            System.out.println(entries.get(k-1).getValue());
        }
    }
}
