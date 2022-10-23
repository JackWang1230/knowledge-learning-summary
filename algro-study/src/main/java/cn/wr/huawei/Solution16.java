package cn.wr.huawei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 *
 *
 * 停车场有一横排车位，0代表没有停车，1代表有车。输入数据中，至少停了一辆车在车位上，也至少有一个空位没有停车。
 * 为了防止剐蹭，需要为停车人找到一个车位，使得距离停车人的车最近的车辆距离是最大的，返回此时的最大距离。
 *
 * 输入描述：
 * 一个用半角逗号分隔的停车标识字符串，停车标识为0或1，0标识空位，1标识已停车。
 * 停车位最多100个
 * 输出描述：
 * 输出一个整数记录最大距离
 *
 * 示例1：
 * 输入
 * 1,0,0,0,0,1,0,0,1,0,1
 * 输出
 * 2
 * 说明
 * 当车停在第3个位置上时，离其最近的车距为2（1到3）
 * 当车停在第4个位置上时，离其最近的车间为2（4到6）
 * 其他位置距离为1.
 * 因此最大距离为2
 * @author : WangRui
 * @date : 2022/10/13
 */

public class Solution16 {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        String substring = s.substring(0, 1);
        String substring1 = s.substring(s.length() - 1);
        // List<Integer> collect = Arrays.stream(split).map(Integer::parseInt).collect(Collectors.toList());
        // 先判断开头是不是 1 以及结尾是不是0
        ArrayList<Integer> list = new ArrayList<>();
        String s1 = s.replaceAll(",", "");
        String[] split = s1.split("1");
        if (substring=="0" && substring1!="0"){
            list.add(split[0].length());
            for (int i = 1; i < split.length; i++) {
                list.add((split[i].length()+1)/2);
            }
        }
        if (substring1=="0" && substring!="0"){
            int a = split.length-1;
            list.add(split[a].length());
            for (int i = 0; i < split.length-1; i++) {
                list.add((split[i].length()+1)/2);
            }
        }
        // 其他情况
        if (substring != "0" && substring1 != "0"){
            for (int i = 0; i < split.length; i++) {
                list.add((split[i].length()+1)/2);
            }
        }
        Integer integer = list.stream().sorted((o1, o2) -> o2 - o1).max(Integer::compareTo).get();
        System.out.println(integer);


    }
}
