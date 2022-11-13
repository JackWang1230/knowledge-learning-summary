package cn.wr.datastruct.queue;

/**
 * @author : WangRui
 * @date : 2022/10/25
 */

public class ArrayQueueDemo {

    // 使用数组模拟队列---编写一个ArrayQueue类
    class ArrayQueue{
        private int maxSize; // 表示数组的最大容量
        private int front ;// 队列头
        private int rear; // 队列尾
        private int[] arr; // 该数组用于存放数据，模拟数据

        // 创建队列构造器
        public ArrayQueue(int maxSize) {
            this.maxSize = maxSize;
            arr = new int[maxSize];
            front = -1; // 指向队列头部的 前一个位置
            rear = -1; // 指向队列尾的具体位置
        }

        /**
         * 队列是否是满
         * @return
         */
        public boolean isFull(){
            return rear == maxSize-1;
        }

        /**
         * 队列是否为空
         * @return
         */
        public boolean isEmpty(){
            return rear==front;
        }

        /**
         * 添加数据
         */
        public void  addQueue(int n){
            if (isFull()){
                System.out.println("队列已经满了");
                return;
            }
            rear ++;
            arr[rear]=n;
        }

        /**
         * 取数据
         * @return
         */
        public int getQueue(){
            if (isEmpty()){
                // 通过抛出异常
                throw new RuntimeException("队列空，无法取值");
            }
            front++; // 需要将front 后移
            return arr[front];
        }

        /**
         * 获取所有数据
         */
        public void showQueue(){
            if (isEmpty()){
                return;
            }
            for (int i = 0; i < arr.length; i++) {
                System.out.printf("arr[%d]=%d\n",i,arr[i]);
            }
        }
    }

    public static void main(String[] args) {

    }
}
