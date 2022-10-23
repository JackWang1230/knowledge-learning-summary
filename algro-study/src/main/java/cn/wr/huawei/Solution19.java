package cn.wr.huawei;

import java.util.Scanner;

/**
 *
 *
 * 【求满足条件的最长子串的长度】
 *
 * 给定一个字符串，只包含字母和数字，按要求找出字符串中的最长（连续）子串的长度，字符串本身是其最长的子串，子串要求：
 *
 * 1、只包含1个字母(a~z, A~Z)，其余必须是数字；
 *
 * 2、字母可以在子串中的任意位置；
 *
 * 如果找不到满足要求的子串，如全是字母或全是数字，则返回-1。
 *
 * 输入描述：
 *
 * 字符串(只包含字母和数字)
 *
 * 输出描述：
 *
 * 子串的长度
 *
 * 示例1：
 *
 * 输入
 *
 * abC124ACb
 *
 * 输出
 *
 * 4
 *
 * @author : WangRui
 * @date : 2022/10/15
 */

public class Solution19 {

    public static boolean check(String str){

        String re = str.replaceAll("[0-9]", "");
        // 替换后 长度不一致  并且只有一个字母存在
        return re.length() != str.length() && re.length() <=1;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();

        // 考虑用双指针解决
        int length = s.length();
        int right =1;
        int left = 0;
        int max = -1;
        while (right< length && left <length){
            // 考虑第一个指针自增
            right ++;
            System.out.println("right:"+right);
            String substring = s.substring(left, right);
            if (check(substring)){
                max = Math.max(max,substring.length());
            }
            else {
                left ++;
                System.out.println("left:" + left);
            }
        };
        System.out.println(max);

       /* char[] chars = s.toCharArray();
        int maxLen = 0;
        int currLen= 0;
        for (char aChar : chars) {
            if (Character.isDigit(aChar)){
                currLen++;
            }
            if ()
        }*/
    }
}
