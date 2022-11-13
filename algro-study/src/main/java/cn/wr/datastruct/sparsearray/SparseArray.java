package cn.wr.datastruct.sparsearray;

/**
 *
 *  二维数组转稀疏数组
 *  1。获取二维数组的行和列
 *  2。获取非零数组的个数
 *  3。将二维数组中的有效数据一次存入稀疏数组中
 *
 *
 *  稀疏数组转二维数组
 *  1。先读取稀疏数组的第一行 获取原始二维数组的行和列长度
 *  2。在读取稀疏数组的后面行数 根据数据依次给二维数组赋值即可
 *
 *
 * @author : WangRui
 * @date : 2022/10/25
 */

public class SparseArray {

    public static void main(String[] args) {

        // 创建一个原始二维数组 11*11
        // 0：表示黑子 1表示黑子 2表示蓝子
        int[][] cheerArray = new int[11][12];

        System.out.println(cheerArray.length);
        cheerArray[1][2] =1;
        cheerArray[2][3] =2;
        for (int[] ints : cheerArray) {
            for (int anInt : ints) {
                System.out.printf("%d\t",anInt);
            }
            System.out.println();
        }

        // 二维数组转 稀疏数组的思路
        // 1. 先遍历二维数组 得到非零的个数
        // 2. 创建稀疏数组 并赋值

        int sum=0;
        for (int[] ints : cheerArray) {
            for (int anInt : ints) {
                if (anInt !=0){
                    sum++;
                }
            }
        }

        int[][] sparseArr = new int[sum + 1][3];
        // 给稀疏数组赋值
        sparseArr[0][0] = 11;
        sparseArr[0][1] = 11;
        sparseArr[0][2] = sum;

        // 遍历二维数组 将非零值存入sparseArray数组中即可
        // 需要一个计数器去记录对应的第几个数
        int count = 0;
        for (int i = 0; i < cheerArray.length; i++) {
            for (int i1 = 0; i1 < cheerArray[0].length; i1++) {
                // 不为零的时候 将数据直接存入稀疏数组中
                if (cheerArray[i][i1] != 0){
                    count++;
                    sparseArr[count][0] = i;
                    sparseArr[count][1] = i1;
                    sparseArr[count][2] = cheerArray[i][i1];
                }
            }
        }
        // 输出稀疏数组

        for (int i = 0; i < sparseArr.length; i++) {
            System.out.printf("%d\t%d\t%d",sparseArr[i][0],sparseArr[i][1],sparseArr[i][2]);
            System.out.println();
        }

        // 将稀疏数组恢复成原来的二维数组

        // 1。基于稀疏数组第一行 创建二维数组的行和列
        int row = sparseArr[0][0];
        int col = sparseArr[0][1];
        int[][] dim2Array = new int[row][col];
        // 从稀疏数组的第二行开始遍历
        for (int i = 1; i < sparseArr.length; i++) {
            dim2Array[sparseArr[i][0]][sparseArr[i][1]]= sparseArr[i][2];
        }
        for (int[] ints : dim2Array) {
            for (int anInt : ints) {
                System.out.printf("%d\t",anInt);
            }
            System.out.println();
        }


    }
}
