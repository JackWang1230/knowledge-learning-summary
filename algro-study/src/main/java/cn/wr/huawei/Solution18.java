package cn.wr.huawei;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/**
 *
 * 根据警察和线人的约定，为了隐蔽，该时间是修改过的，
 * 解密规则为：利用当前出现过的数字，构造下一个距离当前时间最近的时刻，则该时间为可能的犯罪时间。每个出现数字都可以被无限次使
 *
 * " 输入描述：形如HH:SS的字符串，表示原始输入 输出：形如HH:SS的字符串，表示推理出来的犯罪时间
 * 输入
 * 18:52
 *
 * 输出
 * 18:55
 *
 * @author : WangRui
 * @date : 2022/10/14
 */

public class Solution18 {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        ArrayList<Integer> nums = new ArrayList<>();
        char[] chars = s.toCharArray();
        for (char aChar : chars) {
            if (aChar != ':') {
                nums.add(aChar-'0');
            }
            }
        String[] times = s.split(":");
        
        // 获取当前的小时和分钟
        int H = Integer.parseInt(times[0]);
        int M = Integer.parseInt(times[1]);

        // 将所有可能出现的可能性列举出来
        ArrayList<Integer> integers = new ArrayList<>();
        for (Integer i : nums) {
            for (Integer j : nums) {
                // 需要将分钟级别上大于5的值排除
                if (i <=5){
                    integers.add(i*10+j);
                }
            }
        }
        // 升序排序
        integers.sort(Comparator.comparing(o->o));

        // 当天时间内 仅仅改变分钟就可以得到最近值
        for (Integer integer : integers) {
            if (integer<=M){
                continue;
            }
            // 只有integer 大于M 才会执行下面的sout
            System.out.println(H+":"+integer);
        }
        // 如果小于23点
        if ( H != 23 ){
            for (Integer integer : integers) {
                if (integer<=H) {
                    continue;
                }
                // 22:56 这种逻辑上 则没办法改变最近时间
                // 所以需要加integer<23 的逻辑判断
                if ( integer<=23 ){

                    // 12:16 => 16:11
                    // 11 12 16 21
                    System.out.println( integer+":"+integers.get(0));
                }
            }
        }

        // 无法改变的特殊情况
        System.out.println(integers.get(0)+":"+integers.get(0));
    }

}
