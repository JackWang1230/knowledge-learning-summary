package cn.wr.huawei;

import java.util.Scanner;

/**
 *
 * 给定字符串 s 和 t ，判断 s 是否为 t 的子序列。
 *
 * 字符串的一个子序列是原始字符串删除一些（也可以不删除）字符而不改变剩余字符相对位置形成的新字符串。
 * （例如，"ace"是"abcde"的一个子序列，而"aec"不是）
 *
 * // 利用双指针解决
 *
 * @author : WangRui
 * @date : 2022/10/11
 */

public class Solution3 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.next();
        String t = scanner.next();
        int n = s.length();
        int m = t.length();
        int a = 0;
        int b = 0;
        // acme  abcde
        while (a<n && b<m){
            if (s.charAt(a)==t.charAt(b)){
                a++;
            }
            b++;
        }
        if (a==n){
            System.out.println("true");
        }

    }
}
