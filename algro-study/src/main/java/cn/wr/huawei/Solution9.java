package cn.wr.huawei;

import java.util.Scanner;

/**
 *
 * 1.输入字符串s输出s中包含所有整数的最小和，
 * 说明：1字符串s只包含az,AZ,+,-，
 * 2.合法的整数包括正整数，一个或者多个0-9组成，如：0,2,3,002,102
 * 3.负整数，负号开头，数字部分由一个或者多个0-9组成，如-2,-012,-23,-00023
 * 输入描述：包含数字的字符串
 * 输出描述：所有整数的最小和
 * 示例：
 * 输入：
 * bb1234aa
 * 　输出
 * 10
 * 　输入：
 * bb12-34aa
 * 　输出：
 * -31
 * 说明：1+2-(34)=-31
 *
 * @author : WangRui
 * @date : 2022/10/12
 */

public class Solution9 {

    public static void main(String[] args) {

        // 首先根据逻辑判断 如何才能最小
        // 如果是负数 开头 则直接拼接 出来的最小
        // 如果是正数 那么 把每个数字加起来 比拼接的要小
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        // 先将数字转换成字节进行判断
        char[] chars = s.toCharArray();

        // 定义最小和
        int sum =0;
        for (int i = 0; i < chars.length; i++) {

            // bb12-34aa
            if (Character.isDigit(chars[i])){
                sum += chars[i];
            }
            if (chars[i]=='-'){
                // 如果后面跟的是数字则直接截取
                i++;
                int startIndex = i;
                // bb12-34aa
                while (i<s.length() && Character.isDigit(chars[i])){
                    // i++ 会自增 因此截取的时候会出现多一位 最后需要减一位
                    i++;
                }
                //
                String substring = s.substring(startIndex, i);
                if (substring.length()>0){
                    sum -=Integer.parseInt(substring);
                }
                // substring截取的时候 多了一位 需要减一位
                i = i-1;
            }

        }
        System.out.println(sum);

    }
}
