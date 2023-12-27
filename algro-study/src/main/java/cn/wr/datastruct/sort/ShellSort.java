package cn.wr.datastruct.sort;

import java.util.Arrays;

/**
 * @author : WangRui
 * @date : 2023/8/15
 */

public class ShellSort {

    public static void main(String[] args) {


        int[] arr = {8, 9, 1, 7, 2, 3, 5, 4, 6, 0};
//        shellSort(arr);
//        moveShellSort(arr);
        moveShellSortV10(arr);
        System.out.println(Arrays.toString(arr));
    }

    /**
     * 底层逻辑就是通过 交换法
     * 不停的通过分成多个小的数组 进行在小的数组中使用直接插入排序
     *
     * @param arr
     */
    public static void shellSort(int[] arr) {

        // int[] arr = {8, 9, 1, 7, 2, 3, 5, 4, 6, 0};
        // 3 5  1  6  0  8  9  4  7  2   []
        // 0 2  1  4  3  5  7  6  9  8
        // 0 1  2  3  4  5  6  7  8  9


        //  1 8 11 13 19
        //   4 5 6 7 9

        // 1 4 8 5 11 6 13 7 19 9
        // 1 4 5 6  7 8  9 11 13 19


        // gap 表示步长
        // 根据前面逐步分析 使用循环处理
        int temp = 0;
        for (int gap = arr.length / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < arr.length; i++) { // 第一层循环表示执行的次数
                // 遍历各组中所有的元素（共gap组 ）
                for (int j = i - gap; j >= 0; j -= gap) { //第二层循环是实际的处理逻辑
                    // 如果当前元素大雨加上步长后的那个元素 使用交换方法
                    if (arr[j] > arr[j + gap]) {
                        temp = arr[j];
                        arr[j] = arr[j + gap];
                        arr[j + gap] = temp;
                    }
                }

//                for (int j = 0; j < arr.length-gap; j+=gap) {
//                    if (arr[j] > arr[j + gap]) {
//                        temp = arr[j];
//                        arr[j] = arr[j + gap];
//                        arr[j + gap] = temp;
//                    }
//                }
            }
        }


//        当gap=1 的时候 下面就是一个冒泡排序算法
//        for (int i = gap; i < arr.length; i++) {
//            // 遍历各组中所有的元素（共gap组 ）
//            for (int j = i - gap; j >= 0; j -= gap) {
//                // 如果当前元素大雨加上步长后的那个元素 使用交换方法
//                if (arr[j] > arr[j + gap]) {
//                    temp = arr[j];
//                    arr[j] = arr[j + gap];
//                    arr[j + gap] = temp;
//                }
//            }
//        }

    }

    /**
     * 移动法
     *
     * @param arr
     */
    public static void moveShellSort(int[] arr) {

        for (int gap = arr.length / 2; gap > 0; gap /= 2) {

            // 利用移动法处理
            for (int i = gap; i < arr.length; i++) {

                int temp = arr[i];
                int index = i - gap;

                if (arr[index] > arr[i]) {

                    while (index >= 0 && temp < arr[index]) { // 保证不为负
                        arr[index + gap] = arr[index];
                        index -= gap;
                    }
                    arr[index + gap] = temp;
                }

            }
        }


    }

    /**
     * 移动法2
     * 核心思想 步长gap确定后 利用插入法实现即可
     *
     * @param arr
     */
    public static void moveShellSort2(int[] arr) {

        for (int gap = arr.length / 2; gap > 0; gap /= 2) {

            //gap 也是分成的组数 也是步长
            for (int i = gap; i < arr.length; i++) {

                int index = i;
                // 取出当前的这个变量
                int temp = arr[i];

                // 为什么是temp < arr[index - gap] 因此是拿着这个temp 一直和前面的有序数组中数字进行比较
                // 为什么是index - gap >= 0 包含等于 是因为 当等于0的时候代表走到了起始位置
                while (index - gap >= 0 && temp < arr[index - gap]) { // index-gap>0保证了不为负数
                    arr[index] = arr[index - gap]; // 此处代表移动前一个步长值到下一位
                    index -= gap;
                }
                // ? 为何此时不会出现 index<0? 因为index-gap 都赋值给了index 所以index=index-gap必然存在值
                arr[index] = temp; // 此时的index = index-gap 因此回到了前一个值的位置 并将原来的值替换掉


            }
        }


    }


    public static void moveShellSortV1(int[] arr) {

        // 本质就是 优化的插入排序 将 大数组 分为一个个小的数组 在小的数组中进行插入排序（左边有序 右边无序）
        // 二分法的依次进行分层 直到分不了
        //  int[] arr = {8, 9, 1, 7, 2, 3, 5, 4, 6, 0};

        for (int gap = arr.length / 2; gap > 0; gap /= 2) {

            // 每次二分分完后 对分成的每一个新数组进行 插入排序 步长为 gap
            for (int i = gap; i < arr.length; i ++) {

                int index = i;
                int value = arr[i];
                while (index - gap >= 0 && arr[index - gap] > value) {

                    // 向右平移
                    arr[index] = arr[index - gap];
                    index -= gap;
                }

                if (index !=i){
                    arr[index] = value;
                }

            }
        }


    }


    public static void moveShellSortV2(int[] arr){

        // 希尔排序的底层逻辑就是说 通过二分法 依次分成多个小的数组 并对每个小数组进行插入排序

        for (int gap = arr.length/2; gap >0; gap/=2) { //依次分解多个小数组

            for (int i = gap; i < arr.length; i++) { // 开始对小数组进行插入排序

                // 获取得到对应的下标和值
                int index = i; // 默认取到右边无序数组的第一值 ，gap 表示步长
                int value = arr[i];
                while (index-gap>=0 && value< arr[index-gap]){

                    arr[index] = arr[index-gap]; // 左边有序的数组依次往右移动步长为gap
                    index -=gap; // 索引需要向左边移动步长gap
                }

                if (index !=i){ // index = i 表示两者相同 不需要变化

                    arr[index] = value;
                }

            }
        }



    }


    public static void moveShellSortV3(int[] arr){

        // 希尔排序 本质其实就是一个插入排序
        // 利用二分法 将数组分成一个个小的数组 直到不能分割 并对每一个小数组 进行插入排序
        // 相当与是对插入排序进行了一个预处理

        for (int gap = arr.length/2; gap >0; gap/=2) {

            for (int i = gap; i < arr.length ; i++) {

                // 定义当前右边的无序数组第一个值和下标
                int index = i;
                int value = arr[i];
                while (index-gap>=0 && value<arr[index-gap]){
                    // 进行向右平移 步长gap
                    arr[index] = arr[index-gap];
                    index-=gap;
                }
                if (index !=i){

                    arr[index] = value;
                }

            }

        }

    }


    public static void moveShellSortV4(int[] arr){

        // 希尔排序的底层逻辑其实就是 将插入排序进行了优化成一个个小的数组进行插入排序

        for (int gap = arr.length/2; gap >0 ; gap/=2) { // 依次二分

            for (int i = gap; i < arr.length; i++) { // 对每一个小的数组进行插入排序

                int index = i;
                int value = arr[i];
                while (index-gap>=0 && arr[index-gap]>value){

                    // 进行平移
                    arr[index] = arr[index-gap];
                    index-=gap;
                }
                if (index!=i){
                    arr[index] = value;
                }
            }

        }


    }

    public static void moveShellSortV5(int[] arr){

        for (int gap = arr.length/2; gap >0 ; gap/=2) {

            for (int i = gap; i < arr.length; i++) {

                int index = i;
                int value = arr[index];
                while (index-gap>=0 && arr[index-gap]>value){

                    arr[index] = arr[index-gap];
                    index-=gap;
                }

                if (index != i){

                    arr[index] = value;

                }

            }
        }
    }


    public static void  moveShellSortV6(int[] arr){

        for (int gap = arr.length/2; gap >0; gap/=2) {

            for (int i = gap; i < arr.length; i++) {

                int index= i;
                int value = arr[i];

                while (index-gap>=0 && arr[index-gap]>value){

                    arr[index] = arr[index-gap];
                    index-=gap;

                }
                if (index != i){

                    arr[index] = value;

                }




            }
        }


    }

    public static void  moveShellSortV7(int[] arr){

        for (int gap = arr.length/2; gap >0 ; gap/=2) {


            for (int i = gap; i < arr.length; i++) {

                int index = i;
                int value = arr[i];
                while (index-gap>=0 && arr[index-gap]>value){

                    arr[index] = arr[index-gap];
                    index-=gap;
                }
                if (index != gap){
                    arr[index] = value;
                }
            }
        }
    }

    public static void  moveShellSortV8(int[] arr){

        for (int gap = arr.length/2; gap >0 ; gap/=2) {

            for (int i = gap; i <arr.length ; i++) {

                int index = i;
                int value = arr[i];
                while (index-gap>=0 && arr[index-gap]>value){

                    arr[index] = arr[index-gap];
                    index-=gap;
                }
                if (index != i){
                    arr[index] = value;
                }
            }
        }
    }

    public static void moveShellSortV9(int[] arr){

        for (int gap = arr.length/2; gap >0; gap/=2) {

            for (int i = gap; i < arr.length; i++) {

                int index = i;
                int value = arr[i];
                while (index-gap>=0 && arr[index-gap]>value){

                    arr[index] = arr[index-gap];
                    index-=gap;
                }
                if (index != i){
                    arr[index] = value;
                }
            }
        }
    }

    public static void moveShellSortV10(int[] arr){

        for (int gap = arr.length/2; gap >0 ; gap/=2) {

            for (int i = gap; i < arr.length; i++) {

                int index = i;
                int value = arr[i];
                while (index-gap>=0 && arr[index-gap]>value){
                    arr[index] = arr[index-gap];
                    index-=gap;
                }
                if (index !=i){
                    arr[index] = value;
                }
            }
        }
    }

}
