package cn.wr.algro.arrays;

/**
 * @author RWang
 * @Date 2022/3/29
 给定一个 n 个元素有序的（升序）整型数组 nums 和一个目标值 target  ，写一个函数搜索 nums 中的 target，如果目标值存在返回下标，否则返回 -1
 * 输入: nums = [-1,0,3,5,9,12], target = 9
 * 输出: 4
 * 解释: 9 出现在 nums 中并且下标为 4
 *
 */

public class Solution704 {
    public int search(int[] nums,int target){

        if (target < nums[0] || target > nums[nums.length - 1]) {
            return -1;
        }
        int left = 0, right = nums.length - 1;
        while (left<=right){

            int mid = left+((right-left)>>1);
            if (target==nums[mid]){
                return mid;
            } else if (target>nums[mid]){
                left = mid+1;
            }else if(target<nums[mid]){
                right = mid -1;
            }
        }
        return -1;
    }

    public static void main(String[] args) {

    }
}
