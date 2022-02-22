package cn.wr.algro.dynamic;

import org.junit.Test;

/**
 * @author RWang
 * @Date 2022/2/22
 */

public class DynamicDemo {


    @Test
    // 斐波拉契数列 f[n]=f[n-1]+f[n-2]
    // 利用动态规划 存储f[n-1]和 f[n-2]
    public int fib(int n){
        if(n==0){
            return 0;
        }
        if (n==1){
            return 1;
        }
        int first=0;
        int second=1;
        int rs=1;
        for (int i = 2; i < n; i++) {
             // 0 1 2 3
            rs = second+first;
            first = second;
            second =  rs;
        }
        return rs;
    }

    @Test // 动态规划
    public  int fib1(int n) {
        if(n == 0) return 0;
        int[] dp = new int[n + 1];
        dp[0] = 0;
        dp[1] = 1;
        for(int i = 2; i <= n; i++){
            dp[i] = dp[i-1] + dp[i-2];
            dp[i] %= 1000000007;
        }
        return dp[n];
    }


}
