package cn.wr.datastruct.sort;

import java.util.Arrays;

/**
 * 和无序数中进行比较取值
 *
 * @author : WangRui
 * @date : 2023/8/14
 */

public class BubbleSort {

    public static void main(String[] args) {
        int arr[] = {3, 9, -1, 10, -2};
        bubbleSortV6(arr);
        System.out.println(Arrays.toString(arr));

    }


    public static void bubbleSort(int[] arr) {

        int temp = 0;
        boolean flag = true;
        for (int i = 0; i < arr.length - 1; i++) {

            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    flag = true;
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
            if (!flag) { //在一趟排序中 没有进行过排序
                break;
            } else {
                flag = false;
            }
        }


    }


    public static void bubbleSortV1(int[] arr) {

        // 冒泡比较简单 底层逻辑就是 前后两个值 依次比较把 最终把最大的值放在最后面

        for (int i = 0; i < arr.length - 1; i++) {

            for (int j = 0; j < arr.length - 1 - i; j++) {

                //进行比较 交换位置
                if (arr[j] > arr[j + 1]) {

                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }


            }
        }

    }

    public static void bubbleSortV2(int[] arr){

        for (int i = 0; i < arr.length - 1; i++) {

            for (int j = 0; j < arr.length-1-i; j++) {

                if (arr[j]>arr[j+1]){
                    int tmp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = tmp;
                }
            }
        }
    }

    public static void bubbleSortV3(int[] arr){

        for (int i = 0; i < arr.length - 1; i++) {

            for (int j = 0; j < arr.length - 1 - i; j++) {

                if (arr[j]>arr[j+1]){

                    int tmp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = tmp;

                }
            }
        }
    }

    public static void bubbleSortV4(int[] arr){

        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {

                if (arr[j]>arr[j+1]){
                    int tmp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = tmp;
                }
            }
        }
    }

    public static void bubbleSortV5(int[] arr){

        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {

                if (arr[j]>arr[j+1]){
                    int tmp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = tmp;
                }
            }
        }
    }

    public static void bubbleSortV6(int[] arr){

        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {

                if (arr[j]>arr[j+1]){
                    int tmp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = tmp;
                }
            }
        }
    }


}
