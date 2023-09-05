package cn.wr.algro.basicSort;

import org.junit.Test;

/**
 * @author : WangRui
 * @date : 2023/7/5
 */

public class BasicSort {

    private static final int[] list = {1, 5, 7, 3, 2};

    @Test
    public void bubbleSort() {

        int length = list.length;
        for (int i = 0; i < length - 1; i++) {
            for (int j = 0; j < length - 1 - i; j++) {
                // 降序排
                if (list[j] < list[j + 1]) {
                    int tmp = list[j];
                    list[j] = list[j + 1];
                    list[j + 1] = tmp;
                }
            }
        }
        for (int i : list) {
            System.out.println(i);
        }

    }

    @Test
    public void selectSort() {
        // 核心 找到最大位置记录下来之后进行替换
        int length = list.length;
        int pos = 0;
        for (int i = 0; i < length - 1; i++) {
            pos = i;

            // 找到最大一位的下标
            for (int j = i + 1; j < length; j++) {
                if (list[j] > list[pos]) {
                    pos = j;
                }
            }
            if (pos != i) {
                int tmp = list[i];
                list[i] = list[pos];
                list[pos] = tmp;
            }
        }
        for (int i : list) {
            System.out.println(i);
        }

    }

    @Test
    public void insertSort() {
        // {1,5,7,3,2}; 理解成打扑克的时候抓牌场景
        int length = list.length;
        for (int i = 1; i < length; i++) {

            int key = list[i]; // 理解成抓到的那张牌
            int j = i - 1;
            while (j >= 0 && list[j] < key) {
                list[j + 1] = list[j];
                j--;
            }
            list[j + 1] = key;
        }

        for (int i : list) {
            System.out.println(i);
        }

        // 默认第一张牌有序 5 1 7 2 3
        for (int i = 1; i < length; i++) {

            int j = i - 1;
            int key = list[i];
            while (j >= 0 && list[j] < key) {
                list[j + 1] = list[j];
                j--;
            }
            list[j + 1] = key;
        }

        // 顺序
        for (int i = 1; i < length; i++) {

            int j = i - 1;
            int key = list[i];
            while (j >= 0 && list[j] > key) {
                //往后平移一位
                list[j + 1] = list[j];
                j--;
            }
            list[j + 1] = key;
        }


    }

    @Test
    public void shellSort() {


    }



}
