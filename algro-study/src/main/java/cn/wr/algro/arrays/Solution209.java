package cn.wr.algro.arrays;

/**
 * @author RWang
 * @Date 2022/3/31
 *
 * 给定一个含有 n 个正整数的数组和一个正整数 s ，找出该数组中满足其和 ≥ s 的长度最小的 连续 子数组，并返回其长度。如果不存在符合条件的子数组，返回 0。
 *
 * 示例：
 *
 * 输入：s = 7, nums = [2,3,1,2,4,3] 输出：2 解释：子数组 [4,3] 是该条件下的长度最小的子数组。
 */

public class Solution209 {




    public int minSubArrayLens(int s,int [] nums){
        // 通过双指针 滑动窗口解决
        // 窗口大小需要满足大于等于s
        // 双指针如何移动呢？
        int left =0;
        int sum=0;
        int result = Integer.MAX_VALUE;
        for (int right = 0; right < nums.length; right++) {

            //首先需要先开始找窗口
            sum+=nums[right];
            // 当窗口满足大于s
            while (sum>=s){
                // 开始查找在该窗口下最小的长度
                result = Math.min(result,right-left+1);
                // 此时开始减去起始位置数据后，再判断是否满足大于s
                sum-=nums[left];
                // 减完以后，左侧窗口开始移动一个位置,相当于缩小一个位置后再去判断是否满足s
                left++;
            }
        }
        // 最终完成后，返回数据
        return result==Integer.MAX_VALUE?0:result;


    }


    // 双指针思路
    public int minSubArrayLen(int s, int[] nums) {
        // 双指针思路 一般有一个 left,right。result
        int left=0;
        int sum =0;
        int result= Integer.MAX_VALUE; // 长度
        for (int right = 0; right < nums.length; right++) {
            // sum 代表窗口的长度
            sum+=nums[right];
            // 当窗口的大小满足大于等于s
            while (sum>=s){
                // 计算长度
                result=Math.min(result,right-left+1);
                // 然后将和减去第一个值
                sum-=nums[left];
                left++;
            }
        }
        return result==Integer.MAX_VALUE?0:result;
    }




}
