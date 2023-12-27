package cn.wr.datastruct.sort;

import java.util.Arrays;

/**
 * @author : WangRui
 * @date : 2023/8/14
 */

public class SelectSort {

    public static void main(String[] args) {

        int[] arr = {1, 5, 7, 3, 2, 3, 9};
        selectSortV11(arr);
        System.out.println(Arrays.toString(arr));

    }

    /**
     * 找出最小的值 以及最小值得索引 并替换到第一个位置
     *
     * @param arr
     */
    public static void selectSort(int[] arr) {
        // 使用逐步推岛的方式来 讲解排序
        // 第一轮
        // 原始的数组 101 34 119 1
        // 第一轮排序 1 , 34 , 119, 101
        // 算法 先简单-》复杂 就是可以吧一个复杂的算法 拆分成一个简单的问题 -》逐步解决


        for (int i = 0; i < arr.length - 1; i++) {

            int min = arr[i];
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (min > arr[j]) {
                    minIndex = j;
                    min = arr[minIndex];
                }
            }
            if (minIndex != i) {

                int tmp = arr[i];
                arr[i] = arr[minIndex];
                arr[minIndex] = tmp;
            }

        }

      /*  // 第1轮
        int minIndex = 0;
        int min = arr[0];
        for (int j = 0 + 1; j < arr.length; j++) {
            if (min > arr[j]) {
                minIndex = j; // 重置minIndex
                min = arr[j]; // 重置min值
            }
        }
        // 将最小值放在arr[0]
        arr[0] = min;
        arr[minIndex] = arr[0];

        System.out.println("第一轮后");
        System.out.println(Arrays.toString(arr));

        // 第2轮
        int minIndex = 1;
        int min = arr[1];
        for (int j = 1 + 1; j < arr.length; j++) {
            if (min > arr[j]) {
                minIndex = j; // 重置minIndex
                min = arr[j]; // 重置min值
            }
        }
        // 将最小值放在arr[0]
        arr[0] = min;
        arr[minIndex] = arr[0];

        System.out.println("第二轮后");
        System.out.println(Arrays.toString(arr));*/
    }

    public static void selectSortV1(int[] arr) {

        //上来还是基本思想 看成一个值找最小索引
        //  int[] arr = {1, 5, 7, 3, 2};
        for (int i = 0; i < arr.length - 1; i++) {
            int index = i;
            int value = arr[i];
            for (int j = i + 1; j < arr.length; j++) {

                //比较最小值和下标
                if (value > arr[j]) {
                    index = j;
                    value = arr[j];
                }

            }
            if (index != i) {

                int tmp = arr[i];
                arr[i] = value;
                arr[index] = tmp;
            }
        }
    }

    public static void selectSortV2(int[] arr) {
        // 先想一下基本思路 选择排序 就是找到最小的数 和 对应的索引
        // 之后把所有的数和这个最小值进行比较

        for (int i = 0; i < arr.length - 1; i++) {

            int index = i;
            int min = arr[i];
            for (int j = i + 1; j < arr.length; j++) {

                if (min > arr[j]) { // 出现找到比认为的最小值还要小的数的时候 记录该最小值
                    index = j;
                    min = arr[j];
                }
            }

            if (index != i) { // index=i的时候同一个值 不需要交换

                int tmp = arr[i];
                arr[i] = min;
                arr[index] = tmp;
            }
        }


    }

    public static void selectSortV3(int[] arr) {

        // 选择排序的底层逻辑很简单的 就是确定一个数 然后找最小的索引和值

        for (int i = 0; i < arr.length - 1; i++) {

            // 默认选择第一个值为最小下标和索引

            int index = i;
            int min = arr[i];

            for (int j = i + 1; j < arr.length; j++) {

                if (min > arr[j]) {

                    index = j;
                    min = arr[j];
                }
            }

            if (index != i) {
                int tmp = arr[i];
                arr[i] = min;
                arr[index] = tmp;
            }
        }

    }

    public static void selectSortV4(int[] arr) {
        // 选择排序的底层逻辑很清楚 就是找一个最小下标的数 和 索引 放到最前面

        for (int i = 0; i < arr.length - 1; i++) {

            int index = i;
            int min = arr[i];

            for (int j = i + 1; j < arr.length; j++) {

                if (min > arr[j]) {
                    min = arr[j];
                    index = j;
                }
            }

            if (index!= i){
                int tmp = arr[i];
                arr[i] = min;
                arr[index] = tmp;
            }
        }

    }

    public static void selectSortV5(int[] arr){

        // 选择排序 底层逻辑就是说 是看成找最小的值的索引下标 把它放到最前面

        for (int i = 0; i < arr.length ; i++) {

            // 记录当前值的索引和值
            int index = i;
            int value = arr[i];
            for (int j = i; j < arr.length ; j++) {

                if (arr[j]<value){

                    index = j;
                    value = arr[j]; // 一直是跟最小值进行比较的
                }

            }

            if (index !=i){

                // 将最小值放到当前位置
                int tmp = arr[i];  // 经常搞错 是选i 而不是取index 因为此时的index 已经是最小索引的位置
                arr[i] = value;
                arr[index] = tmp;

            }


        }

    }

    public static void selectSortV6(int[] arr){

        for (int i = 0; i < arr.length; i++) {

            int index = i;
            int min = arr[i];
            for (int j = i; j < arr.length; j++) {

                if (arr[j]<min){

                    index = j;
                    min = arr[j];

                }
            }

            if (index != i){

                int tmp = arr[i];

                arr[i] = arr[index];
                arr[index] = tmp;
            }

        }

    }

    public static void selectSortV7(int[] arr){

        for (int i = 0; i < arr.length; i++) {

            int index = i;
            int min = arr[i];
            for (int j = i; j < arr.length; j++) {

                if (arr[j]< min){

                    index = j;
                    min = arr[j];

                }

            }
            if (index != i){

                int tmp = arr[i];

                arr[i] = arr[index];
                arr[index] = tmp;


            }



        }

    }

    public static void selectSortV8(int[] arr){

        // 选择排序的底层逻辑其实也很简单 就是默认第一个值是最小值和最小索引
        // 然后依次比较索引下标 找到最小值索引下标并替换

        for (int i = 0; i < arr.length-1; i++) {

            int index = i;
            int min = arr[i];
            for (int j = i+1; j <arr.length ; j++) {

                if (arr[j]<min){
                    index = j;
                    min = arr[j];
                }
            }

            if (index != i){
                // 交换位置
                int tmp = arr[i];
                arr[i] = arr[index];
                arr[index] = tmp;

            }
        }
    }

    public static void selectSortV9(int[] arr){

        for (int i = 0; i < arr.length-1; i++) {

            int index=i;
            int min = arr[i];

            for (int j = i+1; j <arr.length ; j++) {

                if (arr[j]<min){

                    index = j;
                    min = arr[j];
                }
            }
            if (index != i){

                int tmp = arr[i];
                arr[i] = arr[index];
                arr[index] = tmp;
            }
        }
    }

    public static void selectSortV10(int[] arr){

        for (int i = 0; i < arr.length - 1; i++) {

            int index = i;
            int min = arr[i];
            for (int j = i+1; j < arr.length; j++) {

                if (arr[j]<min){
                    min = arr[j];
                    index = j;
                }
            }
            if (index != i){

                int tmp = arr[i];
                arr[i] = arr[index];
                arr[index] = tmp;
            }
        }
    }

    public static void selectSortV11(int[] arr){

        for (int i = 0; i < arr.length-1; i++) {

            int index = i;
            int value = arr[i];
            for (int j = i+1; j < arr.length; j++) {

                if (arr[j]<value){
                    index = j;
                    value = arr[j];
                }
            }
            if (index != i){

                int tmp = arr[i];
                arr[i] = value;
                arr[index] =tmp;
            }
        }
    }

}
