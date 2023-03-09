package cn.wr.leetcode.easy;

import java.util.Arrays;

/**
 * @author : WangRui
 * @date : 2023/3/9
 */
//给定一个由 整数 组成的 非空 数组所表示的非负整数，在该数的基础上加一。
//
// 最高位数字存放在数组的首位， 数组中每个元素只存储单个数字。
//
// 你可以假设除了整数 0 之外，这个整数不会以零开头。
//
//
//
// 示例 1：
//
//
//输入：digits = [1,2,3]
//输出：[1,2,4]
//解释：输入数组表示数字 123。
//
//
// 示例 2：
//
//
//输入：digits = [4,3,2,1]
//输出：[4,3,2,2]
//解释：输入数组表示数字 4321。
//
//
// 示例 3：
//
//
//输入：digits = [0]
//输出：[1]
//
//
//
//
// 提示：
//
//
// 1 <= digits.length <= 100
// 0 <= digits[i] <= 9
//
//
// Related Topics 数组 数学 👍 1186 👎 0

public class Solution66 {
    public int[] plusOne(int[] digits) {

        // 思路 当尾部为9 的时候 会需要递增
        // 9999->10000
        // 因此考虑对10进行取模处理
        // 数据本身需要进行对每一个数都自增校验是否等于9
        int length = digits.length-1;
        for (int i = length; i >=0; i--) {
            // 先将该值先自增
            digits[i]++; // digits[i] = digits[i]+1
            // 自增完成后, 将该值进行与10取余(取模)
            digits[i]%=10; //  digits[i] = digits[i]%10
            if (digits[i] !=0){
                return digits;
            }
        }
        // 否则digists 该位置的数置为0
        int[] ints = new int[digits.length+1];
        ints[0]=1;
        return ints;

    }

    public static void main(String[] args) {

        int[] a ={1,3,5,6};
        int[] ints = new Solution66().plusOne(a);
    }
}
