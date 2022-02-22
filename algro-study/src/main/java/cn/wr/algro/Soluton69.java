package cn.wr.algro;

/**
 * @author RWang
 * @Date 2022/2/22
 */

/**
 * 一个机器人位于一个 m x n 网格的左上角 （起始点在下图中标记为 “Start” ）。
 *
 * 机器人每次只能向下或者向右移动一步。机器人试图达到网格的右下角（在下图中标记为 “Finish” ）。
 *
 * 问总共有多少条不同的路径？
 */
public class Soluton69 {

    /**
     * 递归思路  1）从最左走到最右下 的（m,n）位置的话，在此之前 有两种可能 (m-1,n)和 (m,n-1)这两步都可以到达（m,n）
     *         2) 所以 f(m,n)=f(m-1,n)+f(m,n-1)的可能性之和
     * @param m
     * @param n
     * @return
     */
    public int uniquePaths(int m,int n){
        if (m==1 || n==1){
            return 1;
        }
        return uniquePaths(m-1,n)+uniquePaths(m,n-1);
    }


    /**
     * 动态规划思路  1） 状态转移方程 f(m,n)=f(m-1,n)+f(m,n-1)
     *             2) 最终走到最后的(m,n)位置之前 依旧是(m-1,n)及(m,n-1)的所有路线情况之和
     * @param m
     * @param n
     * @return
     */
    public int uniquePaths1(int m,int n){

        int[][] dp = new int[m][n];

        return 1;
    }

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        int i = Soluton69.class.newInstance().uniquePaths(3, 2);
        System.out.println(i);
    }
}
