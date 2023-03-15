package cn.wr.datastruct.recursion;

/**
 *
 * 八皇后问题算法思路分析
 * 1）第一个皇后先放第一行第一列
 * 2）第二个皇后放在第二行第一列，然后判断是否ok,
 * 如果不ok,继续放在第二列，第三列，依次把所有列都放完，找到一个合适
 * 3）继续第三个皇后，还是第一列，第二列...直到第8个皇后也能放在一个不冲突的位置
 * 算是找到一个正确解
 * 4）当得到一个正确解是，在栈回退到上一个栈时，就会开始回溯，即将第一个皇后，放到第一列
 * 的所有正确解，全部找到
 * 5）然后回头继续第一个皇后放第二列，后面继续循环执行1，2，3，4的步骤
 *
 * @author : WangRui
 * @date : 2023/3/2
 */

public class Queue8 {

    //定义一个max表示共有多少个皇后
    int max = 8;
    // 定义数组array 保存皇后放置位置的结果，比如
    // arr ={0,4,7,5,2,6,1,3}
    int[] array = new int[max];

    static int count =0;
    public static void main(String[] args) {

        // 测试一把 8皇后是否正确
        Queue8 queue8 = new Queue8();
        queue8.check(0);
        System.out.println("一共多少种："+count);

    }


    //编写一个方法 放置第n个皇后
    private void check(int n){
        if (n== max){ //n=8,其实8个皇后已然放好
            print();
            return;
        }
        // 依次放入皇后，并判断是否冲突
        for (int i = 0; i < max; i++) {
            // 先把当前这个皇后n，放到该行的第1列
            array[n] =i;
            // 判断当放置第n个皇后到i列时，是否冲突
            if (judge(n)) {//不冲突
                // 接着放n+1 个皇后，即开始递归
                check(n+1);
            }
            // 如果冲突，就继续执行array[n] = i 即将第n个皇后放置在本行的后移一个位置;

        }
    }


    //查看当我们放置第n个皇后，就去检测该皇后是否和前面已经摆放的皇后冲突
    /**
     *
     * @param n n表示放第n个皇后
     * @return
     */
    private boolean judge(int n){

        for (int i = 0; i < n; i++) {
            // 说明
            // 1。array[i] == array[n] 表示判断第n个皇后是否和前面的n-1个皇后是否在同一列
            // 2. Math.abs(n-i) == Math.abs(array[n]-array[i] 表示是否在斜线
            if (array[i] == array[n] ||
                    Math.abs(n-i) == Math.abs(array[n]-array[i])){
                return false;
            }
        }
        return true;
    }

    // 写一个方法 可以将皇后摆放的位置打印出来
    private void print(){
        count++;
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]+" ");
        }
        System.out.println();
    }
}
