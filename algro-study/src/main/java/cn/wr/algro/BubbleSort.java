package cn.wr.algro;

/**
 * @author RWang
 * @Date 2022/1/13
 */

public class BubbleSort {

    /**
     * 思路 前后两个值进行比较，小的放在前面
     * 第一层循环 确定排序的次数
     * 第二层循环  1） 第一次 排序 通过比较 j 和 j+1 可以确定 将最大的值 放在最后位置
     *           2） 第二次 排序 那么对应的长度 j依旧是从0开始 ，由于第一次已经确定的最大值 此时 遍历长度减少一位
     *           3） 第三次往后 以此类推
     */
    public int[] bubbleSort(int[] list){

        // 1,5,3,2,7
        int length = list.length;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length-1-i; j++) {
                if (list[j]>list[j+1]){
                    int tmp = list[j+1];
                    list[j+1] = list[j];
                    list[j] = tmp;
                }
            }
        }
        return list;
    }


    public int[] bubbleSort1(int[] list){

        int length = list.length;
         // 4 2 1 3 6
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length-1-i; j++) {
                if (list[j]>list[j+1]){
                    int tmp = list[j];
                    list[j] = list[j+1];
                    list[j+1] = tmp;
                }
            }
        }


        return list;

    }

    public static void main(String[] args) {
        int [] list = {1,5,3,2,7};
        BubbleSort bubbleSort = new BubbleSort();
        int[] ints = bubbleSort.bubbleSort(list);
        for (int anInt : ints) {
            System.out.println(anInt);
        }
    }


}
