package cn.wr.huawei;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 *
 * /*
 * 给定一个随机的整数数组(可能存在正整数和负整数)nums,
 * 请你在该数组中找出两个数，其和的绝对值(|nums[x]+nums[y]|)为最小值
 * 并返回这两个数(按从小到大返回)以及绝对值。
 * 每种输入只会对应一个答案。但是，数组中同一个元素不能使用两遍。
 *
 * 输入描述：
 *  一个通过空格空格分割的有序整数序列字符串，最多1000个整数，
 *  且整数数值范围是[-65535,65535]
 *
 * 输出描述：
 *   两个数和两数之和绝对值
 *
 *  示例一：
 *   输入
 *   -1 -3 7 5 11 15
 *   输出
 *   -3 5 2
 *
 * 说明：
 * 因为|nums[0]+nums[2]|=|-3+5|=2最小，
 * 所以返回-3 5 2
 *
 * @author : WangRui
 * @date : 2022/10/12
 */

public class Solution10 {


    public static void main(String[] args) {
        // 业务逻辑 取出最小值 所以需要定义一个最大值 然后把所有的可能和他进行比较

        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        int min = Integer.MAX_VALUE;
        String[] s1 = s.split(" ");
        TreeSet<Integer> integers = new TreeSet<>();
        List<Integer> collect = Arrays.stream(s1).map(Integer::parseInt)
                .distinct().collect(Collectors.toList());
        for (int i = 0; i < collect.size()-1; i++) {
            for (int j = i; j < collect.size(); j++) {
               // 求出最小值
                int value = Math.abs(collect.get(i)+collect.get(j));
                if (value<min){
                    min = value;
                    integers.clear();
                    integers.add(collect.get(i));
                    integers.add(collect.get(j));
                }
            }
        }

        if (integers.size()>0){
            for (Integer integer : integers) {
                System.out.println(integer+"");
            }
            System.out.println(min);
        }

    }
}
