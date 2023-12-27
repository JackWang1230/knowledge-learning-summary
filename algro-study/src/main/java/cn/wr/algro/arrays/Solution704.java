package cn.wr.algro.arrays;

/**
 * @author RWang
 * @Date 2022/3/29
 * 给定一个 n 个元素有序的（升序）整型数组 nums 和一个目标值 target  ，写一个函数搜索 nums 中的 target，如果目标值存在返回下标，否则返回 -1
 * 输入: nums = [-1,0,3,5,9,12], target = 9
 * 输出: 4
 * 解释: 9 出现在 nums 中并且下标为 4
 */

public class Solution704 {

    public static void main(String[] args) {

        int[] nums = {-1, 0, 3, 5, 9, 12};
        int[] nums1 = {1,2,3,6,9,10};
        int target = 4;
        int target1 = 10;
        int i = searchV6(nums1, target1);
        System.out.println(i);
    }

    public int search(int[] nums, int target) {

        if (target < nums[0] || target > nums[nums.length - 1]) {
            return -1;
        }
        int left = 0, right = nums.length - 1;
        while (left <= right) {

            int mid = left + ((right - left) >> 1);
            if (target == nums[mid]) {
                return mid;
            } else if (target > nums[mid]) {
                left = mid + 1;
            } else if (target < nums[mid]) {
                right = mid - 1;
            }
        }
        return -1;
    }

    /**
     * 要求是二分法处理
     *
     * @param nums
     * @param target
     * @return
     */
    public static int searchV1(int[] nums, int target) {

        //首先判断这个值是否在这个有序数组的范围内 如果不在范围内 直接跳出
        if (nums[0] > target || nums[nums.length - 1] < target) {
            return -1;
        }

        // 因为是二分法 所以需要找到中轴值
        int left = 0;
        int right = nums.length - 1;

        //  int[] nums1 = {1,2,3,6,9,10};
        //  int target1 = 7;

        while (left <= right) {

            int index = (left+right)>>1;
            int pivot = nums[(left + right) >> 1]; // >>1 相当于除以2 , >>2 相当于除以2^2
                                                   // <<1 相当于乘以2,  <<2 相当于乘以2^2
            if (target > pivot) {
                left = index+1;
            } else if (target < pivot) {
                right = index-1;
            } else if (target == pivot) {
                return index;
            }
        }

        return -1;

    }

    public static int searchV2(int[] arr,int target){

        if (target<arr[0] || target>arr[arr.length-1]){
            return -1;

        }

        // 二分查找
        int left = 0;
        int right = arr.length-1;
        while (left<=right){

            int index = (left+right)>>1;
            int pivot = arr[index];
            if (pivot>target){
                right = index-1;
            }else if (pivot<target){
                left = index+1;
            }else {
                return index;
            }

        }
        return -1;

    }

    public static int searchV3(int[] arr,int target){

        if (target<arr[0] || target>arr[arr.length-1]){
            return -1;
        }

        int left = arr[0];
        int right = arr[arr.length-1];

        while (left<=right){

            int index = (left+right)>>1;
            int pivot = arr[(left+right)>>1];
            if (target>pivot){
                left = index+1;
            }else if (target<pivot){
                right = index-1;
            }else {
                return index;
            }

        }
        return -1;
    }

    public static int searchV4(int[] arr,int target){

        if (arr[0]>target || arr[arr.length-1]<target){
            return -1;
        }

        int left = 0;
        int right = arr.length-1;
        int index = (left+right)>>1;
        int pivot = arr[index];

        while (left<=right){
            if (target>pivot){
                left = index+1;
                index = (left+right)>>1;
                pivot = arr[index];
            }else if (target<pivot){

                right = index-1;
                index = (left+right)>>1;
                pivot = arr[index];
            }else {
                return index;
            }

        }
        return -1;
    }

    public static int searchV5(int[] arr,int target){

        if (arr[0]>target || arr[arr.length-1]<target){
            return -1;
        }
        int left =0;
        int right = arr.length-1;
        while (left<=right){
            int index = (left+right)>>1;
            int pivot = arr[index];
            if (pivot>target){
                right= index-1;
            }else if (pivot<target){
                left = index+1;
            }else {
                return index;
            }
        }
        return -1;
    }

    public static int searchV6(int[] arr,int target){
        //  nums = [-1,0,3,5,9,12], target = 9
        // 需要基于二分法解决

        if (arr[0]>target || arr[arr.length-1]<target){
            return -1;
        }
        int left = 0;
        int right = arr.length-1;
        while (left<=right){
            // 二分核心找中轴值
            int index = (left+right)>>1;
            int pivot = arr[index];
            if (pivot>target){
                right= index-1;
//                index = (left+right)>>1;
//                pivot = arr[index];
            }else if (pivot<target){
                left = index+1;
//                index = (left+right)>>1;
//                pivot = arr[index];
            }else {
                return index;
            }
        }
        return -1;
    }
}
