package cn.wr.huawei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 *  /*
 *     给定一个正整数数组
 *     检查数组中是否存在满足规则的数组组合
 *     规则：
 *       A=B+2C
 *     输入描述
 *      第一行输出数组的元素个数
 *      接下来一行输出所有数组元素  用空格隔开
 *     输出描述
 *      如果存在满足要求的数
 *      在同一行里依次输出 规则里 A/B/C的取值 用空格隔开
 *      如果不存在输出0
 *
 *      示例1：
 *        输入
 *        4
 *        2 7 3 0
 *
 *        输出
 *        7 3 2
 *        说明：
 *         7=3+2*2
 * @author : WangRui
 * @date : 2022/10/11
 */

public class Solution4 {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        int num = Integer.parseInt(in.nextLine());
        String[] nums = in.nextLine().split(" ");
        int[] arr = new int[num];
        in.close();
        for (int i = 0; i < nums.length; i++) {
            arr[i] = Integer.parseInt(nums[i]);
        }
        Arrays.sort(arr);
        String res ="0";
        for (int i = arr.length-1; i >0; i--) {
            for (int j = 0; j < i; j++) {
                for (int k = 0; k < i; k++) {
                    //  0 2 3 7
                    int a = arr[i];
                    int b = arr[j];
                    int c = arr[k];
                    if (a == b+2*c){
                        res = a+" "+b+" "+c;
                    }
                }
            }
        }
        System.out.println(res);
    }
}
