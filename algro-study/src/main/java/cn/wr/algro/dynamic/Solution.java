package cn.wr.algro.dynamic;

/**
 * @author RWang
 * @Date 2022/2/18
 */

import java.util.Scanner;

/**
 *           5
 *         8   4
 *      3    6    9
 *   7    2     9   5
 *
 *   数塔取数问题 第一层一个数  第二层二个数 依次往下
 *
 *   状态定义：F[i][j]是第i行j列项最大取数和，求第n行F[n][m]（0<m<n）中的最大值
 *   状态转移方程： F[i][j] = max{F[i-1][j-1],F[i-1][j]}+A[i][j]
 */
public class Solution {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // 输入层数
        int n = in.nextInt();
        long max = 0;
        // 定义存储的结构
        int[][] dp = new int[n][n];
        dp[0][0] = in.nextInt();

        for (int i = 1; i < n; i++) {
            // 此时的i代码的是二维数组的行数(即第几行)
            for (int j = 0; j <=i ; j++) {

                int num = in.nextInt();
                if (j == 0)
                    dp[i][j] = dp[i - 1][j] + num;
                else
                    dp[i][j] = Math.max(dp[i - 1][j - 1], dp[i - 1][j]) + num;
                max = Math.max(dp[i][j], max);
            }
        }
        System.out.println(max);

    }
}
