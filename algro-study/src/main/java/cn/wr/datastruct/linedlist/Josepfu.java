package cn.wr.datastruct.linedlist;

/**
 *
 * 约瑟夫问题 获取出队顺序序列
 * @author : WangRui
 * @date : 2022/11/2
 */

public class Josepfu {


    public static void main(String[] args) {
        CircleSingleLinkedList circleSingleLinkedList = new CircleSingleLinkedList();
        circleSingleLinkedList.addBoy(5); // 加入五个小孩节点
        circleSingleLinkedList.showBoy();

        // 测试小孩出圈是否正确
        circleSingleLinkedList.countBoy(1,2,5); // 2>4>1>5>3

    }
}

/**
 * n=5 五个人
 * k=1 从第一个开始数
 * m=2 数两下
 *
 * 1。需要创建一个辅助指针(变量)helper，事先应该指向环形节点的最后一个节点
 * 补充： 小孩报数前，先让first和helper移动k-1次
 * 2。当小孩报数时，让first和helper指针同时移动 m-1次
 * 3。这时候将first指向的小孩节点出圈
 *    first = first.next
 *    helper.next = first
 */


// 单向环形链表
class CircleSingleLinkedList {
    // 创建一个first 节点 ，当前没有编号
    private  Boy first = new Boy(-1);

    // 添加小孩节点，构建一个环形的链表
    public void  addBoy(int nums){
        // nums 做一个数据校验
        if (nums <1){
            System.out.println("数据不正确");
            return;
        }
        Boy curBoy = null; // 辅助指针，帮助构建环形链表

        // for循环创建环形链表
        for (int i = 1; i <= nums; i++) {
            // 根据编号，创建小孩节点
            Boy boy = new Boy(i);
            // 如果是第一个小孩
            if (i==1){
                first = boy;
                first.setNext(first); // 构建环
                curBoy = first;
            }else {
                curBoy.setNext(boy);
                boy.setNext(first);
                curBoy = boy;
            }
        }
    }

    /**
     * 遍历打印
     */
    public void showBoy(){
        if (first == null){
            System.out.println("没有任何小孩～～");
            return;
        }
        // 因为first 不能动 因此我们仍然使用一个辅助指针完成遍历
        Boy curBoy = first;
        while (true){
            System.out.println(curBoy.getNo());
            if (curBoy.getNext() == first){
                break;
            }
            curBoy = curBoy.getNext(); // 相当于curBoy 后移
        }
    }


    /**
     *
     * @param startNo 起始位置
     * @param countNum 数几下
     * @param nums 最初有几个小孩在圈中
     */
    // 根据用户的输入 计算小孩出圈的顺序
    public  void  countBoy(int startNo,int countNum,int nums){
        if (first == null || startNo<1 || startNo>nums){
            System.out.println("输入参数有误");
            return;
        }
        // 创建要给的辅助指针，帮助小孩出圈
        Boy helper = first;
        while (true){
            // 辅助节点是出圈节点的前一个节点
            if (helper.getNext()==first){
                break;
            }
            helper = helper.getNext();
        }
        // 小孩报数前 先让first和helper移动k-1次
        for (int j = 0; j < startNo - 1; j++) {
            first = first.getNext();
            helper = helper.getNext();
        }
        // 当小孩报数时，让first 和 helper同时移动m-1次
        // 通过while循环 知道圈内只有一个人 即 first == helper
        while (true){
            if (first == helper){
                System.out.println(first.getNo());
                break;
            }
            // first 和 helper 移动m-1次
            for (int i = 1; i < countNum; i++) {
                first = first.getNext();
                helper= helper.getNext();
            }
            // 打印出出圈的小孩节点
            System.out.println(first.getNo());
            // 这时将first指向的小孩节点出圈的下一个位置
            first = first.getNext();
            helper.setNext(first); // 设置辅助节点的next指向 first
        }

    }


}


// 创建一个Boy类，表示一个节点
class Boy{
    private int no;
    private Boy next; // 指向下一个节点

    public Boy(int no) {
        this.no = no;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public Boy getNext() {
        return next;
    }

    public void setNext(Boy next) {
        this.next = next;
    }
}
