package cn.wr.datastruct.sort;

import java.util.Arrays;

/**
 * @author : WangRui
 * @date : 2023/8/17
 */

public class QuickSort {

    public static void main(String[] args) {

//        int[] arr = {8, 9, 1, 7, 2, 3, 5, 4, 6, 0};
        int[] arr = {-9, 45, 3, 0, 1, 2, 5};
//        int[] arr1 = {-9, 9, -9, -9, -9, -9, 5};
        quickSortV4(arr, 0, arr.length - 1);
        System.out.println(Arrays.toString(arr));
    }

    public static void quickSort(int[] arr, int left, int right) {

        // 定义左边和右边的下表索引，以及中轴值
        int l = left;
        int r = right;
        int pivot = arr[(left + right) / 2];
        int temp = 0;
        while (l < r) { // 左边如果小于右边就一直循环不退出
            // 我们考虑从小到大排序的场景
            while (arr[l] < pivot) { // while表示一直寻找直到找到 大于等于pivot的值才会停下来
                l++;
            }
            while (arr[r] > pivot) { // while表示一直寻找直到找到 小于等于pivot的值才会停下来
                r--;
            }

            if (l == r) { // 最终的情况是 左右两个下标会重合 这个时候退出
                break;
            }

            // 左边找一个 右边找一个后开始进行交换位置
            temp = arr[l];
            arr[l] = arr[r];
            arr[r] = temp;

            // 左下标直到下标值等于中轴值
            if (arr[l] == pivot) {
                // 此时它不不再变化 等待右下标往左移直到左下标相等
                r--;
            }

            // 右下标直到下标值等于中轴值
            if (arr[r] == pivot) {
                l++; // 此时它不不再变化 等待左下标往右移直到左下标相等
            }
        }

        if (l == r) { // 这个很重要 当左右两边重合后
            // 其实此时 pivot的左边都是比他小的值 右边都是比它大的值
            // 而下标的话 必须要各自移一位 否则死循环
            l++;
            r--;
        }
        // 之后针对左边进行 递归
        if (left < r) {
            quickSort(arr, left, r);
        }
        // 之后针对右边进行 递归
        if (right > l) {
            quickSort(arr, l, right);
        }

    }


    public static void quickSortV1(int[] arr, int left, int right) {

        // 快速排序的底层逻辑其实很简单 就是 找一个中轴值 将比这个值大的值都放到右边 将比这个值小的都放在左边

        int l = left;
        int r = right;
        int pivot = arr[(left + right) / 2];

        // 将比中轴值大的放到他右边 比他小的放他左边
        // 左边下标往右移动 右边下表往左移
        while (l < r) {
            while (arr[l] < pivot) { // 当左边的值比中轴值小 就往右移动 直到找到大于等于他的值 停下来
                l++;
            }
            // 停下来之后 开始移动右边的值
            while (arr[r] > pivot) { // 当右边的值比中轴值大 就往左边移动 直到找到一个小于等于他的值

                r--;
            }
            //当左边和右边的下标相同的时候 或者说假设这个数组有序 则按照这个规律 此时 l=r 且 arr[l]=arr[r]=pivot
            if (l == r) {
                //当出现这种情况 说明此时 中轴值左边的都已经小于等于他
                // 中轴值右边的都大于等于他
                break;
            }
            // 当左边的值小于等于pivot 且 右边的值 大于等于pivot 且 l 不等于 r的
            // 进行替换位置
            int tmp = arr[l];
            arr[l] = arr[r];
            arr[r] = tmp;

            // 还有一种特殊情况 也算是优化把
            if (arr[l] == pivot){   // 当换完位置以后 如果 arr[l] == pivot，说明此时左边的值都是比pivot小的值 此时只需要 右边的下标继续移动即可
                r--;                // 直到找到比中轴值小的数 再继续替换位置
            }
            // 同理
            if (arr[r] == pivot){
                l++;
            }

        }

        // 此时对左边利用递归进行排序
        if (left < r) { // left <r 的目的是优化的作用 假设第一次排序直接把最小值放在了第一位就不需要继续排序

            quickSortV1(arr, left, r - 1);
        }

        // 对右边同理也是通过递归进行排序
        if (l < right) {  // l <right 的目的是优化的作用 假设第一次排序直接把最大值放在了第一位就不需要继续排序

            quickSortV1(arr, l + 1, arr.length - 1);
        }


    }


    public static void quickSortV2(int[] arr,int left, int right){

        // 快速排序底层实现逻辑很简单 就是找一个中轴值 把比他大的放右边 比他小的放左边
        // 之后不停的递归重复即可

        int l = left;
        int r = right;
        int pivot = arr[(left+right)/2];

       while (l<r){ // 只要保证左边比右边小 就不停的移动左右下标

           while (arr[l]<pivot){ // 左边比中轴值小

               l++; // 直到找到一个大于等于的值的下标l
           }

           // 右边同理 找到小于等于中轴值的小标r 停下来
           while ( arr[r]>pivot){
               r-- ;
           }

           // 找到一个退出点
           if (l==r){
               break;
           }

           // 不满足的时候 需要将左边和右边的值进行交换位置 一直到 满足左边的值都比中轴值小 右边的值都比中轴值大

           int tmp = arr[l];
           arr[l] =arr[r];
           arr[r] = tmp;

           // 优化部分 若交换之后出现相等
           if (arr[l] == pivot){
               // 这种情况说明昨天都是比中轴值小的数了
               // 此时只需要将右下标移动一位后继续比较即可
               r--;
           }

           // 同理
           if (arr[r]== pivot){
               l++;
           }

       }

       if(left<r){
           quickSortV2(arr,left,r-1);
       }
       if (right>l){
           quickSortV2(arr,l+1,arr.length-1);
       }

    }


    public static void quickSortV3(int[] arr, int left,int right){


        // 快速排序的底层逻辑就是说 选择一个中轴值 然后依次从左边和右边选一个值和他进行比较
        // 若左边比他大停下 同理右边比他小 也需要停下进行交换位置

        int l = left;
        int r = right;
        int pivot= arr[(right+left)/2];
        while (l<r){

            while (arr[l]<pivot){
                l++;
            }
            while (arr[r]>pivot){
                r--;
            }
            if (l==r){
                break;
            }

            // 交换位置
            int tmp = arr[l];
            arr[l] = arr[r];
            arr[r] = tmp;

            if (arr[l] == pivot){
                r--;
            }
            if (arr[r] == pivot){
                l++;
            }
        }


        if (left<r){

            quickSortV3(arr,left,r-1);
        }
        if (right>l){
            quickSortV3(arr,l+1,right);
        }

    }

    public static void quickSortV4(int[] arr,int left,int right){

        int l = left;
        int r = right;
        int pivot = arr[(left+right)/2];
        while (l<r){

            while (arr[l]<pivot){
                l++;
            }
            while (arr[r]>pivot){
                r--;
            }

            if (l==r){
                break;
            }

            // 左右找寻到值以后 开始交换位置

            int tmp = arr[l];
            arr[l] = arr[r];
            arr[r] = tmp;

            if (arr[l]==pivot){
                r--;
            }
            if (arr[r]== pivot){
                l++;
            }
        }

        if (left<r){
            quickSortV4(arr,left,r-1);
        }
        if (right>l){
            quickSortV4(arr,l+1,right);
        }
    }
}
