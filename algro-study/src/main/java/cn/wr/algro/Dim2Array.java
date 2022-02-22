package cn.wr.algro;

/**
 * @author RWang
 * @Date 2022/2/22
 */

/**
 * 二维数组的一些浅显认知
 */
public class Dim2Array {

    public static void main(String[] args) {
        // 指定好二维数组的行 和 列
        int[][] ints = new int[1][10];
        System.out.println(ints.length);
        for (int i = 0; i < ints.length; i++) {
            for (int j = 0; j < 10; j++) {
                ints[i][j]=j;
            }
        }
        for (int i = 0; i < ints.length; i++) {
            for (int j = 0; j < ints[i].length; j++) {
                System.out.println(ints[i][j]);
            }
        }
    }
}
