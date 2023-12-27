package cn.wr.algro.arrays;

/**
 * @author RWang
 * @Date 2022/3/29
 * 给你一个数组 nums 和一个值 val，你需要 原地 移除所有数值等于 val 的元素，并返回移除后数组的新长度。
 *
 * 不要使用额外的数组空间，你必须仅使用 $O(1)$ 额外空间并原地修改输入数组。
 *
 * 元素的顺序可以改变。你不需要考虑数组中超出新长度后面的元素。
 *
 * 示例 1: 给定 nums = [3,2,2,3], val = 3, 函数应该返回新的长度 2, 并且 nums 中的前两个元素均为 2。 你不需要考虑数组中超出新长度后面的元素。
 *
 * 示例 2: 给定 nums = [0,1,2,2,3,0,4,2], val = 2, 函数应该返回新的长度 5, 并且 nums 中的前五个元素为 0, 1, 3, 0, 4。
 */

public class Solution27 {
    public int removeElement(int[] nums, int val) {

        int fastIndex = 0;
        int slowIndex;
        for (slowIndex = 0; fastIndex < nums.length; fastIndex++) {
            if (nums[fastIndex] != val) {
                nums[slowIndex] = nums[fastIndex];
                slowIndex++;
            }
        }
        return slowIndex;

    }


    public static int removeElementV5(int[] arr,int val){

        // 两个索引搞定
        int fastIndex=0;
        int slowIndex=0;
        for ( fastIndex = 0; fastIndex < arr.length; fastIndex++) {

            if (val != arr[fastIndex]){
                arr[slowIndex] = arr[fastIndex];
                slowIndex++;
            }
        }
        return slowIndex;

    }
    public static int removeElementV4(int[] arr,int val){

        // 双指针的思路解决
        int minIndex=0;
        for (int i = 0; i < arr.length; i++) {

            if (val != arr[i]){
                arr[minIndex]= arr[i];
                minIndex++;
            }
        }
        return minIndex;

    }
    public static int removeElementV3(int[] arr,int val){

        int index2=0;
        for (int i = 0; i < arr.length; i++) {

            if (arr[i] != val){
                arr[index2]=arr[i];
                index2++;
            }
        }
        return index2;
    }

/*    public static int  removeElementV1(int[] arr,int val){

        int i =0;
        int length = arr.length;
        while (i<arr.length){

            if (val==arr[i]){
                length--;
            }
            i++;
        }
        return length;
    }*/

    public static int removeElementV2(int[] arr,int target){

        // 这道题可以理解成一个双指针的问题
        int fastIndex=0;
        int slowIndex=0;
        while (fastIndex<arr.length){
            if (arr[fastIndex] != target){

                arr[slowIndex] = arr[fastIndex];
                slowIndex++;
            }
            fastIndex++;


        }
        return slowIndex;

    }

    public static void main(String[] args) {

        int[] nums = {1,2,3,4,2,3};
        int val = 2;
        int i = removeElementV5(nums, val);
        System.out.println(i);
//        Solution27 solution27 = new Solution27();
//        int i = solution27.removeElement(nums, val);
//        int i1 = removeElementV1(nums, val);
//        System.out.println(i1);

    }
}
