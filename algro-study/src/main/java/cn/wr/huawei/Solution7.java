package cn.wr.huawei;

import java.util.Scanner;

/**
 *
 *
 * 给你一串未加密的字符串str，通过对字符串的每一个字母进行改变来实现加密，
 * 加密方式是在每一个字母str[i]偏移特定数组元素a[i]的量，数组a前三位已经赋值：a[0]=1,a[1]=2,a[2]=4。
 * 当i>=3时，数组元素a[i]=a[i-1]+a[i-2]+a[i-3]，
 * 例如：原文 abcde 加密后 bdgkr，其中偏移量分别是1,2,4,7,13。
 *
 * 示例1
 * 输入
 * 1
 * xy
 *
 * 输出
 * ya
 *
 * 说明
 * 第一个字符x偏移量是1，即为y，第二个字符y偏移量是2，即为a
 *
 * 示例2
 * 输入
 * 2
 * xy
 * abcde
 *
 * 输出
 * ya
 * bdgkr
 *
 * 说明
 * 第二行输出字符偏移量分别为1、2、4、7、13
 *
 *
 * @author : WangRui
 * @date : 2022/10/12
 */

public class Solution7 {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()){
            int n = Integer.parseInt(sc.nextLine());
            String[] arr = new String[n];
            // 偏移数组大小
            int max =4;
            for (int i = 0; i < n; i++) {
                arr[i] = sc.nextLine();
                max = Math.max(max,arr[i].length());
            }

            // 递推求出任意项的值
            int[] offsetArr = new int[max];
            offsetArr[0] = 1;
            offsetArr[1] = 2;
            offsetArr[2] = 4;
            for (int i = 3; i < max; i++) {
                offsetArr[i] = offsetArr[i - 1] + offsetArr[i - 2] + offsetArr[i - 3];
            }

            // 公式 就是 基于下标获取当前的字节值 获取偏移量 之后 取余获取实际值
            for (int i = 0; i < n; i++) {
                String str = arr[i];
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < str.length(); j++) {
                    // 获取字符串 以及 偏移的位置
                    char c = str.charAt(j);
                    int offset = offsetArr[j];
                    // 26个字母从97-122 (a-z) 取余，避免溢出
                    // 26取余的原因就是因为 每26个最进行循环一次
                    int res = (c-'a'+offset) % 26 + 'a';
                    sb.append((char) res);
                }
                System.out.println(sb);
            }

        }
    }
}
