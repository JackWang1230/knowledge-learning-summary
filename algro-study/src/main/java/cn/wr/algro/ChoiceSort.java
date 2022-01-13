package cn.wr.algro;

/**
 * @author RWang
 * @Date 2022/1/13
 */

public class ChoiceSort {


    /**
     * 找到数组中最大的元素，与数组最后一位元素交换。当只有一个数时，则不需要选择了，因此需要n-1趟排序
     * 两个for循环，外层循环控制排序的趟数，内层循环找到当前趟数的最大值，随后与当前趟数组最后的一位元素交换
     * @param list
     * @return
     */
    public int[] choiceSort(int[] list){

        int length = list.length;
        int pos = 0;
        // 外层循环 控制排序的次数 n-1 次
        for (int i = 0; i < length-1; i++) {
            pos = i;
            for (int j = i; j < length; j++) {
                if (list[j]<list[pos]){
                    pos = j;
                }
            }
            if (pos != i){
                int tmp = list[i];
                list[i] = list[pos];
                list[pos] = tmp;
            }
        }

        return list;
    }


    public static void main(String[] args) {
        int [] list = {1,5,3,2,7};
        ChoiceSort choiceSort = new ChoiceSort();
        int[] ints = choiceSort.choiceSort(list);
        for (int anInt : ints) {
            System.out.println(anInt);
        }
    }

}
