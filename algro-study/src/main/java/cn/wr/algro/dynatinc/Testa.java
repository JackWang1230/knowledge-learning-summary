package cn.wr.algro.dynatinc;

import java.util.Scanner;

/**
 * @author RWang
 * @Date 2022/2/21
 */

public class Testa {

    public static void main(String[] args) {
        int[][] dp = new int[4][4];
        dp[0][0] = 1;
       //  System.out.println(dp);

        Scanner in = new Scanner(System.in);
        // 输入层数
        int n = in.nextInt();
        System.out.println(n);
        int m = in.nextInt();
        System.out.println(n);
        System.out.println(m);
    }
}
