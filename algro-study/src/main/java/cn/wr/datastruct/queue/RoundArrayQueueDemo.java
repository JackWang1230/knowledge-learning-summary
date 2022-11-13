package cn.wr.datastruct.queue;

/**
 * 使用数组模拟环形队列的思路分析(基于取模的方式实现)
 *
 * 1. 队列头：front 指向的就是队列的第一个元素位置，也就是说arr[front]就是对垒的第一个元素
 * 2。队列尾： rear 指向的就是最后一个元素的下一个下位置，通过空出一个位置作为约定
 * 3。当队列满的时候条件： （rear+1)%maxSize == front
 * 4. 当队列为空的条件：  front== rear
 * 5. 默认的front 和 rear 的初始值为0
 * 6. 队列中的有效的数组的个数 （rear+maxSize-front）%maxSize
 * @author : WangRui
 * @date : 2022/10/26
 */

public class RoundArrayQueueDemo {

    static class CircleArrayQueue {

        private int maxSize;
        private int front; // 数据头
        private int rear; // 数据尾
        private int[] arr;

        public CircleArrayQueue(int maxSize) {
            this.maxSize = maxSize;
            arr = new int[maxSize];
        }

        public boolean isFull(){

            return (rear+1)%maxSize == front;
        }

        public boolean isEmpty(){
            return rear == front;
        }

        public void addQueue(int n){

            if (isFull()){
                return;
            }
            // 直接将数据加入
            arr[rear] = n;
            // rear 后移，这里必须考虑取模
            rear = (rear+1)%maxSize;
        }

        public int getQueue(){
            if (isEmpty()){
                throw  new RuntimeException("队列为空，无法取值");
            }
            // 这里需要分析 front 是指向队列的第一个元素
            // 1。 先把front 对应的值保留到一个临时变量
            // 2。 将front后移
            // 3。 将临时保存的变量返回
            int value = arr[front];
            // front后移 会越界需要取模
            front = (front+1)%maxSize;
            return value;
        }

        public void  showQueue(){

            if (isEmpty()) {
                return;
            }
            // 思路：从 front 开始遍历，遍历多少个元素
            // 求出当前队列的有效数据个数
            for (int i = front; i < front+size() ; i++) {
                System.out.printf("arr[%d]=%d\n",i%maxSize,arr[i%maxSize]);
            }

        }

        /**
         * 获取队列有效个数
         * @return
         */
        public int size(){
             // rear=1;
            // front = 0;
            // maxSize = 3;
            return (rear+maxSize-front)%maxSize;
        }

    }

    public static void main(String[] args) {
        System.out.printf("c测试一把 环形队列的");
        CircleArrayQueue circleArrayQueue = new CircleArrayQueue(4); // 实际有效的空间是 3 因为预留了一个空间

    }
}
