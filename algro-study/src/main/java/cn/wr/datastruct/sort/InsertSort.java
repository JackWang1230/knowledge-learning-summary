package cn.wr.datastruct.sort;

import java.util.Arrays;

/**
 * 有序数组和所选元素进行比较
 *
 * @author : WangRui
 * @date : 2023/8/15
 */

public class InsertSort {

    public static void main(String[] args) {

        int[] arr = {11, 34, 2, 8, 5}; // 2 11 34 8 5
        insertSortV12(arr);
        System.out.println(Arrays.toString(arr));
    }


    /**
     * 插入排序实则就是分为一个左边有序数组 和一个右边无序数组
     * <p>
     * 每次从无序数组的第一个值 并记录下该值 和左边的有序数组一次比较
     * 只要左边的有序数组比右边的这个无序数组的第一个元素大
     * 那么左边的有序数组就需要一次往右边平移 直到找到满足左边数组比右边数组的第一个元素小的值 把最开始标记的那个无序数组第一个元素进行替换到该位置
     *
     * @param arr
     */
    public static void insertSort(int[] arr) {

        for (int i = 1; i < arr.length; i++) {

            int insertVal = arr[i];
            int insertIndex = i - 1;
            while (insertIndex >= 0 && insertVal < arr[insertIndex]) {
                // 依次往右移动 实现逻辑 其实是 保留数字2
                // {11,34,2,8,5}; // 11 34 34 8 5 《=》arr[insertIndex+1] = arr[insertIndex]
                arr[insertIndex + 1] = arr[insertIndex];
                insertIndex--;
            }
            if (insertIndex + 1 != i) {

                arr[insertIndex + 1] = insertVal;
            }

        }

    }

    public static void insertSortV1(int[] arr) {
        // 记住是一个有序和一个无序数组
        // int[] arr = {11,34,2,8,5};
        // 我们从第一个值开始 记录索引和下标 之后依次从无序数组中取第一个值和有序数组比较
        for (int i = 0; i < arr.length - 1; i++) {

            int index = i;
            int value = arr[i + 1]; // 记录下一个
            while (index >= 0 && arr[index] > value) {  // 左边有序数组 因为是有序的 所以只需跟这个value比较 比他大就右移
                // 开始平移
                arr[index + 1] = arr[index];
                index--;
            }
            if (index != i) {

                arr[index + 1] = value;
            }
        }

    }

    public static void insertSortV2(int[] arr){
        // 核心思想 左边看成一个有序数组 右边看成一个无序数组 依次呐右边无序数组第一个值跟左边数组比较

        //  int[] arr = {11, 34, 2, 8, 5};
        for (int i = 0; i < arr.length - 1; i++) {
            // 记录一下当前索引下标和 下一个值的value值
            int index = i;
            int value = arr[i+1];
            while (index>=0 && arr[index]>value){
                // 进行平移
                arr[index+1] = arr[index];
                index--;
            }
            if (index != i) { // 这种情况 第一次的时候 代表不需要排序 直接跳出
                arr[index+1] = value;
            }
        }


    }

    public static void insertSortV3(int[] arr){

        // 基本思想 左边有序 右边无序 拿着右边无序的第一个值 依次和左边比较找到在左边有序数组的位置并插入
        for (int i = 0; i < arr.length - 1; i++) {

            int index = i;
            int nextValue = arr[i+1];
            while (index>=0 && arr[index]>nextValue){

                arr[index+1] = arr[index];
                index--;
            }
            if (index != i){

                arr[index+1] = nextValue;
            }
        }

    }

    public static void insertSortV4(int[] arr){

        //记住基本思想 很简单 就是左边有序数组 右边无序数组 依次从右边无序数组的第一值 和左边有序数组进行依次比较
        // 找到对应的位置 插入进去
        // 通常取第一个值为左边有序数组

        for (int i = 0; i < arr.length-1; i++) {

            int index= i;
            int nextValue = arr[i+1];
            while (index>=0 && arr[index]>nextValue){
                // 进行依次平移左边的有序数到右边
                arr[index+1]  = arr[index];
                index--;
            }

            if (index != i){
                arr[index+1] = nextValue;
            }
        }



    }

    public static void insertSortV5(int[] arr){

        // 插入排序的底层逻辑很简单 就是说 看成左边一个有序数组 和右边一个无序数组
        // 依次将右边的无序数组的第一个值和左边的有序数组进行依次比较

        for (int i = 0; i < arr.length-1; i++) {

            int index = i;
            int nextValue = arr[i+1];
            while (index>=0 && arr[index]>nextValue){

                arr[index+1] = arr[index];
                index--;
            }
            if (index !=i){

                arr[index+1] = nextValue;
            }


        }

    }

    public static void insertSortV6(int[] arr){

        // 插入排序的底层逻辑 我们可以理解成是一个有序数组和一个无序数组

        // 我们先找第一个值 和 对应的下标索引

        for (int i = 0; i < arr.length - 1; i++) {

            int index = i;
            int nextValue = arr[i+1];
            while (index>=0 && arr[index]>nextValue){
                // 进行平移

                arr[index+1] = arr[index];
                index --;
            }

            if (index != i) {
                arr[index+1] = nextValue;
            }
        }

    }

    public static void insertSortV7(int[] arr){
        for (int i = 1; i < arr.length ; i++) {

            int index = i;
            int value = arr[i];
            while (index-1>=0 && arr[index-1]>value){

                arr[index] = arr[index-1];
                index--;
            }
            if (index != i){

                arr[index] = value;
            }

        }


    }

    public static void insertSortV8(int[] arr){

        for (int i = 0; i < arr.length-1; i++) {

            int index = i;
            int value = arr[i+1];
            while (index>=0 && arr[index]>value){

                arr[index+1] = arr[index];
                index--;

            }
            if (index != i){

                arr[index+1] = value;
            }
        }

    }

    public static void insertSortV9(int[] arr){

        // 插入排序的底层逻辑其实很简单的 就是有一个有序和一个无序数组
        // 将无序数组的第一个元素依次和左边的有序数组进心比较找到一个合理的位置并插入进去

        for (int i = 0; i < arr.length - 1; i++) {

            int index = i;
            int value = arr[i+1];
            while (index>=0 && arr[index]>value){

                arr[index+1] = arr[index];
                index--;
            }
            if (index!= i){
                arr[index+1] = value;
            }
        }
    }

    public static void insertSortV10(int[] arr){

        for (int i = 0; i < arr.length - 1; i++) {

            int index = i;
            int value = arr[i+1];
            while (index>=0 && arr[index]>value){

                arr[index+1] = arr[index];
                index--;
            }
            if (index != i){
                arr[index+1]= value;
            }
        }
    }

    public static void insertSortV11(int[] arr){

        for (int i = 0; i < arr.length - 1; i++) {

            int index = i;
            int value = arr[i+1];
            while (index>=0 && arr[index]>value){

                arr[index+1] = arr[index];
                index--;
            }
            if (index != i){
                arr[index+1] = value;
            }
        }
    }

    public static void insertSortV12(int[] arr){

        for (int i = 0; i < arr.length-1; i++) {

            // 底层逻辑就是 一个有序和一个无序
            int index =i;
            int value = arr[i+1];
            while (index>=0 && arr[index]>value){

                arr[index+1] = arr[index];
                index--;
            }
            if (index != i){
                arr[index+1] = value;
            }

        }
    }

    public static void insertSortV13(int[] arr){

        // 2 11 34 8 5
        for (int i = 0; i < arr.length - 1; i++) {
            int index = i;
            int value = arr[index+1];
            while (index>=0 && arr[index]>value){
                arr[index+1] = arr[index];
                index--;
            }
            if (index != i){
                arr[index+1] = value;
            }
        }

    }


}
