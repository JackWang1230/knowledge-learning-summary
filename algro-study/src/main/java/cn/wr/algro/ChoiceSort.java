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


    public int[] selectSort(int[] list){

        for (int i = 0; i < list.length-1; i++) {

            int minPos = i;
            // 依次进行判断后面的数是否比第一个值小，如果小记录下该值的下标记位置
            for (int j = i+1; j < list.length; j++) {
                // 依次进行比较
                if (list[j]<list[minPos]){
                    minPos = j;
                }
                // 并将最小值放到第一个位置上
            }
            // 如果只有一个数没必要排序
            if (minPos!=i){
                int tmp= list[i];
                list[i] = list[minPos];
                list[minPos] = tmp;
            }

        }

        return list;
    }

    public int[] choiceSort1(int[] list){
        // 4 2,1 3,5
        int length = list.length;

        int minPos = 0;
        for (int i = 0; i < length; i++) {
             //minPos = i;
            for (int j = 0; j < length; j++) {
                // 找出最小位置
                if (list[j]<list[minPos]){
                    int tmp = list[j];
                    list[minPos] = tmp;
                    list[j] = list[minPos];
                }
            }

        }
        return list;


    }


    public static void main(String[] args) {
        int [] list = {1,5,7,3,2};
        ChoiceSort choiceSort = new ChoiceSort();
        int[] ints = choiceSort.choiceSort(list);
        for (int anInt : ints) {
            System.out.println(anInt);
        }
    }

}
