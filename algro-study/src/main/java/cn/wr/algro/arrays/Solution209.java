package cn.wr.algro.arrays;

/**
 * @author RWang
 * @Date 2022/3/31
 * <p>
 * 给定一个含有 n 个正整数的数组和一个正整数 s ，找出该数组中满足其和 ≥ s 的长度最小的 连续 子数组，并返回其长度。如果不存在符合条件的子数组，返回 0。
 * <p>
 * 示例：
 * <p>
 * 输入：s = 7, nums = [2,3,1,2,4,3,9] 输出：2 解释：子数组 [4,3] 是该条件下的长度最小的子数组。
 */

public class Solution209 {

    public static int minSubArrayLenV3(int s,int[] arr){

        int fastIndex =0;
        int slowIndex = 0;
        int sum=0;
        int result = Integer.MAX_VALUE;
        for (fastIndex = 0; fastIndex < arr.length ; fastIndex++) {

            sum +=arr[fastIndex];
            while (sum>=s){
                result= Math.min(fastIndex-slowIndex+1,result);
                sum-=arr[slowIndex];
                slowIndex++;
            }
        }
        return result==Integer.MAX_VALUE?0:result;
    }

    public static int minSubArrayLenV2(int s, int[] arr) {

        int sum = 0;
        int right = 0;
        int result = Integer.MAX_VALUE;
        for (int i = 0; i < arr.length; i++) {

            sum += arr[i];
            while (sum >= s) {
                result = Math.min(i - right + 1, result); // i-right+1是核心
                sum -= arr[right];
                right++;
            }

        }
        return result != Integer.MAX_VALUE ? result : 0;
    }

    public static int minSubArrayLensV1(int s, int[] arr) {

        int left = 0;
        int i;
        int sum = 0;
        int result = Integer.MAX_VALUE;
        for (i = 0; i < arr.length; i++) {

            sum += arr[i];
            while (sum >= s) {
                // 计算最小结果值
                result = Math.min(result, i - left + 1);

                sum -= arr[left];
                left++;
            }
        }

        return result == Integer.MAX_VALUE ? 0 : result;
    }

    public int minSubArrayLens(int s, int[] nums) {
        // 通过双指针 滑动窗口解决
        // 窗口大小需要满足大于等于s
        // 双指针如何移动呢？
        int left = 0;
        int sum = 0;
        int result = Integer.MAX_VALUE;
        for (int right = 0; right < nums.length; right++) {

            //首先需要先开始找窗口
            sum += nums[right];
            // 当窗口满足大于s
            while (sum >= s) {
                // 开始查找在该窗口下最小的长度
                result = Math.min(result, right - left + 1);
                // 此时开始减去起始位置数据后，再判断是否满足大于s
                sum -= nums[left];
                // 减完以后，左侧窗口开始移动一个位置,相当于缩小一个位置后再去判断是否满足s
                left++;
            }
        }
        // 最终完成后，返回数据
        return result == Integer.MAX_VALUE ? 0 : result;


    }


    // 双指针思路
    public int minSubArrayLen(int s, int[] nums) {
        // 双指针思路 一般有一个 left,right。result
        int left = 0;
        int sum = 0;
        int result = Integer.MAX_VALUE; // 长度
        for (int right = 0; right < nums.length; right++) {
            // sum 代表窗口的长度
            sum += nums[right];
            // 当窗口的大小满足大于等于s
            while (sum >= s) {
                // 计算长度
                result = Math.min(result, right - left + 1);
                // 然后将和减去第一个值
                sum -= nums[left];
                left++;
            }
        }
        return result == Integer.MAX_VALUE ? 0 : result;
    }


    public static void main(String[] args) {

        int[] nums = {2, 3, 1, 2, 4, 3,9};
        int[] num1 = {1, 2, 3, 4, 5};
        int[] num2 = {5,1,3,5,10,7,4,9,2,8};

        int i = minSubArrayLenV3(15, num2);
        System.out.println(i);
    }


}
