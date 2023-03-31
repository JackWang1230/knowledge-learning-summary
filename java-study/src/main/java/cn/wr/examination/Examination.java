package cn.wr.examination;

/**
 * @author : WangRui
 * @date : 2023/3/31
 */

public class Examination {

    // 写一个方法 冒泡排序
    public static void bubbleSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    public static void main(String[] args) {

        int[] a =new int[] {18,34,1,44,65,66,68};
        Examination.bubbleSort(a);
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i]+" ");
        }

    }

}
