package cn.wr.algro.arrays;

import cn.wr.algro.Solution2195;

/**
 * @author RWang
 * @Date 2022/4/1
 *
 * 给定一个正整数 n，生成一个包含 1 到 n^2 所有元素，且元素按顺时针顺序螺旋排列的正方形矩阵。
 *
 * 示例:
 *
 * 输入: 3 输出: [ [ 1, 2, 3 ], [ 8, 9, 4 ], [ 7, 6, 5 ] ]
 */

public class Solution59 {


    public int[][] generateMatrix1(int n) {

        // 定义一个n行的正方形矩阵
        int[][] matrix = new int[n][n];
        // 循环次数
        int loop = n / 2;
        // 考虑左闭右开区间依次遍历
        int startX = 0;
        int startY = 0;
        int offset = 1;
        int count = 1;
        int mid = n / 2;
        while (loop > 0) {

            // 1(0,0)    2(0,1)   3(0,2)    4(0,3)
            // 12(1,0)  13(1,1)   14(1,2)   5(1,3)
            // 11(2,0)  16(2,1)   15(2,2)   6(2,3)
            // 10(3,0)   9(3,1)    8(3,2)   7(3,3)
            // 四次for循环

            int i = startX;
            int j = startY;
            // 第一次 是 上侧 从左往右
            for (j = 0; j < startY + n - offset; j++) {
                matrix[startX][j] = count++;
            }
            // 第二次 是 右侧 从上往下
            for (i = 0; i < startX + n - offset; i++) {
                matrix[i][j] = count++;
            }
            // 第三次是 下侧 从右往左
            for (; j > startX; j--) {
                matrix[i][j] = count++;
            }
            // 第四次 左侧 从下往上
            for (; i > startY; i--) {
                matrix[i][j] = count++;
            }
            loop--;
            startX += 1;
            startY += 1;
            offset += 2;
            if (n % 2 == 1) {
                matrix[mid][mid] = count;
            }

        }
        return matrix;

    }



    public int[][] generateMatrix(int n) {

        int[][] res = new int[n][n];

        // 循环次数
        int loop = n / 2;

        // 定义每次循环起始位置
        int startX = 0;
        int startY = 0;

        // 定义偏移量
        int offset = 1;

        // 定义填充数字
        int count = 1;

        // 定义中间位置
        int mid = n / 2;
        while (loop > 0) {
            int i = startX;
            int j = startY;

            // 模拟上侧从左到右
            for (; j<startY + n -offset; ++j) {
                res[startX][j] = count++;
            }

            // 模拟右侧从上到下
            for (; i<startX + n -offset; ++i) {
                res[i][j] = count++;
            }

            // 模拟下侧从右到左
            for (; j > startY; j--) {
                res[i][j] = count++;
            }

            // 模拟左侧从下到上
            for (; i > startX; i--) {
                res[i][j] = count++;
            }

            loop--;

            startX += 1;
            startY += 1;

            offset += 2;
        }

        if (n % 2 == 1) {
            res[mid][mid] = count;
        }

        return res;

    }


    public static void main(String[] args) {

        Solution59 solution59 = new Solution59();
        // int[][] ints = solution59.generateMatrix(3);
        // System.out.println(ints);

        int res=0;
        int count=0;
        for (int i = 0; i < 5; i++) {
            res = count++;
        }
        System.out.println(res);
        System.out.println(count);
    }
}
