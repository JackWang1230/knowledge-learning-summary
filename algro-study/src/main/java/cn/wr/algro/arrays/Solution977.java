package cn.wr.algro.arrays;

import java.util.Arrays;

/**
 * @author RWang
 * @Date 2022/3/29
 * 给你一个按 非递减顺序 排序的整数数组 nums，返回 每个数字的平方 组成的新数组，要求也按 非递减顺序 排序。
 *
 * 示例 1： 输入：nums = [-4,-1,0,3,10] 输出：[0,1,9,16,100] 解释：平方后，数组变为 [16,1,0,9,100]，排序后，数组变为 [0,1,9,16,100]
 *
 * 示例 2： 输入：nums = [-7,-3,2,3,11] 输出：[4,9,9,49,121]
 */

public class Solution977 {
    // 双指针方法
    // 新建一个新的数组存储数据
    // 从两端遍历，比较后从尾部插入数据
    public int[] sortedSquares(int[] nums) {

       int[] newNums = new int[nums.length];
       int l =0;
       int r =nums.length-1;
       int index = nums.length-1;
       while (l<=r){
           if (nums[r]*nums[r]>=nums[l]*nums[l]){
               newNums[index--] = nums[r]*nums[r];
               r--;
           }else if (nums[r]*nums[r]<nums[l]*nums[l]){
               newNums[index--] = nums[l]*nums[l];
               l++;
           }
       }
       return newNums;
    }

    public static int[] sortedSquareV3(int[] arr){

        int l = 0;
        int r = arr.length-1;
        int i = arr.length-1;
        int[] newArr = new int[arr.length];
        while (l<=r){

            if (arr[l]*arr[l]>arr[r]*arr[r]){
                newArr[i] = arr[l]*arr[l];
                i--;
                l++;
            }else {
                newArr[i] = arr[r]*arr[r];
                i--;
                r--;
            }
        }
        return newArr;
    }

    public static int[] sortedSquareV2(int[] arr){

        int l = 0;
        int r = arr.length-1;
        int[] newArr = new int[arr.length];
        int i=arr.length-1;
        while (l<=r){

           if (arr[l]*arr[l]>=arr[r]*arr[r]){

               newArr[i]= arr[l]*arr[l];
               i--;
               l++;
           }else {
               newArr[i]= arr[r]*arr[r];
               i--;
               r--;
           }

        }
        return newArr;

    }

    public static int[] sortedSquaresV1(int[] arr){

        int [] newArr = new int[arr.length];
        int l = 0;
        int r = arr.length-1;
        int index = arr.length-1;
        while (l<=r){
            if (arr[l]*arr[l]<=arr[r]*arr[r]){
                newArr[index] = arr[r]*arr[r];
                index--;
                r--;
            }
            if (arr[l]*arr[l]>arr[r]*arr[r]){
                newArr[index]= arr[l]*arr[l];
                index--;
                l++;
            }
        }
        return newArr;
    }

    public static void main(String[] args) {
        int[] nums ={-7,-3,2,3,11};

        int[] ints = sortedSquareV3(nums);
        System.out.println(Arrays.toString(ints));
//        Solution977 solution977 = new Solution977();
//        solution977.sortedSquares(nums);
    }

}
