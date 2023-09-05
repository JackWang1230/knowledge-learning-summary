package cn.wr.leetcode.easy;

/**
 *
 * 给你一个非负整数 x ，计算并返回 x 的 算术平方根 。
 *
 * 由于返回类型是整数，结果只保留 整数部分 ，小数部分将被 舍去 。
 *
 * 注意：不允许使用任何内置指数函数和算符，例如 pow(x, 0.5) 或者 x ** 0.5 。
 *
 * 示例 1：
 *
 * 输入：x = 4
 * 输出：2
 * 示例 2：
 *
 * 输入：x = 8
 * 输出：2
 * 解释：8 的算术平方根是 2.82842..., 由于返回类型是整数，小数部分将被舍去。
 *
 * @author : WangRui
 * @date : 2023/6/25
 */

public class Solution69 {

    public int mySqrt(int x) {

        if (x==0) return 0;
        if (x==1) return 1;

        // 利用二分查找直接解决
        // 一个数的算数平方根 小于一个数的一半
        int left =1;
        int right = x/2;
        // 直接在区间[left,right]之间定位
        while (left<right){
            int mid = left+(right-left+1)/2;
            // 判断此时中间的mid 平方是否大于 x
            if(mid*mid>x){
                // 说明mid的平方是大于x
                // 区间范围缩小[left,mid-1]
                right = mid-1;
            }else {
                // 所以mid的平方是小于等于x
                // 区间范围变成[mid,right]
                left = mid;
            }
        }
        return left;
    }
}
