package cn.wr.datastruct.linedlist;

/**
 * @author : WangRui
 * @date : 2022/10/26
 */

public class SingleLinkedListDemo {

    public static void main(String[] args) {
        HeroNode heroNode1 = new HeroNode(1, "宋江", "及时雨");
        HeroNode heroNode2 = new HeroNode(2, "卢俊义", "玉麒麟");
        HeroNode heroNode3 = new HeroNode(3, "吴用", "智多星");
        HeroNode heroNode4 = new HeroNode(4, "林冲", "豹子头");

        // 创建单项链表
        SingleLinkedList singleLinkedList = new SingleLinkedList();
        singleLinkedList.addByOrder(heroNode1);
        singleLinkedList.addByOrder(heroNode4);
        singleLinkedList.addByOrder(heroNode2);
        singleLinkedList.addByOrder(heroNode3);

        singleLinkedList.list();

        // 测试修改节点的代码
        HeroNode ee = new HeroNode(2, "小卢", "玉麒麟～");
        singleLinkedList.update(ee);

    }

    /**
     * 链表反转
     * @param heroNode
     */
    public void reverseList(HeroNode heroNode){
        // 在链表只有一个节点或者为空 直接返回
        if (heroNode.next==null || heroNode.next.next== null){
            return;
        }

        // 定义一个辅助指针
        HeroNode cur = heroNode.next;
        HeroNode next = null; // 指向当前节点的下一个节点
        HeroNode reverseHead = new HeroNode(0,"","");
        //遍历原来的链表，并从头遍历原来的链表，就将其取出，并放在新的链表reverserhead的最前端
        while (cur != null){
            next = cur.next;// 暂时保存当前节点的下一个节点，

            // 这段逻辑很复杂，需要将当前节点的下一个节点 指向原来链表中它的前一个节点
            cur.next = reverseHead.next; // 将cur的下一个节点指向新的链表的最前端
            // 之后需要将自己置换成新链表除了头节点之外的第一个节点
            reverseHead.next = cur;
            // 然后 进行向下一个节点遍历即后移
            cur = next;
        }
        // 最后将辅助节点下一个节点替换成原来的头节点的下一个位置
        heroNode.next = reverseHead.next;

    }

    /**
     * 获取单链表的有效节点个数
     * @param head
     * @return
     */
    public int getLength(HeroNode head){

        if (head.next == null){ // 空链表
            return 0;
        }
        int length = 0;
        HeroNode cur = head.next;
        while (cur != null){
            length++;
            cur= cur.next;
        }
        return length;

    }

    /**
     * 查找单链表中的倒数第k个节点
     * 思路
     * 1。编写一个方法 接收head的节点，同时接收一个index
     * 2。index表示同意是倒数第index个节点
     * 3。先把链表从头到尾遍历，得到链表的总长度getlength
     * 4. 得到size后，我们从链表的第一个开始遍历(size-index)个，就可以得到
     * 5。如果找打了返回该节点 找不到返回空
     * @param head
     * @return
     */
    public HeroNode findLastIndexNode(HeroNode head,int index){
        // 判断如果链表为空，返回null
        if(head.next == null){
            return null;
        }
        // 第一次遍历得到链表的长度（节点的个数）
        int size = getLength(head);
        // 第二次遍历size-index 位置，就是我们倒数的第k个节点
        // 先做一个index的校验
        if (index<=0 || index>size){
            return null;
        }
        // 定义辅助变量 for循环定位到倒数的index
        HeroNode  cur = head.next; // 3个有效节点 找倒数第一项即 index=1 那么遍历 3-1=2（0,1）两次 也就是向后位移两次
        for (int i = 0; i < size - index; i++) {
            cur = cur.next;
        }
        return cur;
    }

}

// 定义一个SingleLinkedList 管理我们的英雄
class SingleLinkedList{

    // 先初始化一个头节点 头节点不能动,不存放具体数据
    private HeroNode head = new HeroNode(0,"","");

    /**
     * 返回头节点
     * @return
     */
    public HeroNode getHead() {
        return head;
    }

    // 添加节点到单向链表
    // d当不考虑编号顺序时，找到当前链表的最后节点，将最后的节点的next 指向新的节点
    public void  add(HeroNode heroNode){

        // 因为head的节点不能动,需要一个辅助节点遍历temp
        HeroNode temp = head;
        // 遍历链表，找到最后
        while (true){
            // 找到最后的节点
            if (temp.next== null){
                break;
            }
            // 如果没有找到，将temp 后移
            temp = temp.next;
        }
        // 当退出while循环时，temp就指向链表的最后
        // 将最后的这个节点的next 指向新的节点
        temp.next = heroNode;
    }


    /**
     * 按照编号进行排序添加
     * @param heroNode
     */
    // 第二种方式 按照英雄的编号顺序进行英雄插入到指定的位置
    public  void  addByOrder(HeroNode heroNode){

        // 因为头节点不能动，需要辅助指针来temp帮助确定需要添加的位置
        // 因为单链表，找到添加位置的前一个位置，否则无法添加
        HeroNode temp = head;
        boolean flag = false; // 标识添加的编号是否存在，默认为false
        while (true){
            if ((temp.next == null)) {
                // 说明temp已经在链表的最后
                break;
            }
            if(temp.next.no>heroNode.no){
                // 找到了该点的位置
                // 因此此时添加的位置就在 temp 和 temp.next之间
                break;
            } else if (temp.next.no == heroNode.no){
                // 说明希望添加的herono的编号存在
                flag = true;
                break;
            }
            // 进行后移 相当于在遍历
            temp = temp.next;
        }
        // 退出循环后 判断flag的值
        if (flag){
            System.out.printf("准备插入的这个英雄编号 %d，已经存在，不能加入",heroNode.no);
        }else {
            // 插入到链表中
            // 1。先让新的节点的next 指向 temp的next
            // 2. 再将temp的next 指向 heroNode
            heroNode.next = temp.next;
            temp.next = heroNode;
        }

    }

    /**
     * 修改节点逻辑
     * @param heroNode
     */
    public void update(HeroNode heroNode){
        if (head.next == null){
            return;
        }
        // 定义一个辅助变量
        HeroNode temp = head.next;
        boolean flag = false; // 判断是否找到该节点
        while (true){
            if (temp == null){
                break; // 表示链表已经遍历结束了
            }
            if (temp.no == heroNode.no){
                flag = true;
                break;
            }
            // 节点向后移一位，表示遍历
            temp = temp.next;
        }
        if (flag){
            temp.name = heroNode.name;
            temp.nickName = heroNode.nickName;
        }else {
            System.out.printf("没有找到编号%d 的节点",heroNode.no);
        }
    }

    /**
     * 从单链表删除一个节点的逻辑
     * 1. 需要一个辅助节点 去找到需要删除的节点的前一个节点
     * 2. temp.next = temp.next.next 也就是前一个节点指向被删除节点的下一个节点
     * 3。此时被删除的节点，将不会有其他饮用指向，会被垃圾回收机制回收
     * @param heroNode
     */
    public void delete(HeroNode heroNode){

        // 判断链表是否为空
        if (head.next== null){
            return;
        }

        // head 节点不能动，需要一个辅助节点 temp，也就是辅助指针
        // 通过temp 找到待删除节点的前一个节点
        HeroNode temp = head.next;
        boolean flag = false; // 是否找到被删除的节点
        while (true){
            if (temp.next == null){
                break;
            }
            if (temp.next.no == heroNode.no){
                // 说明找到被删除节点的前一个节点
                flag = true;
                break;
            }
            // 如果上述都不符合 需要后移
            temp = temp.next;
        }
        if (flag){
            // 表示被删除节点的上一个节点 指向了被删除节点的下一个位置
            // 此时被删除节点将会等待垃圾回收机制回收
            temp.next = temp.next.next;
        }else {
            System.out.printf("要删除的节点%d,b不存在",heroNode.no);
        }
    }


    // 显示链表 通过辅助变量来遍历整个链表
    /**
     * 显示单项列表
     */
    public void  list(){
        // 判断链表是否为空
        if (head.next== null){
            return;
        }
        // 如果头节点不能动，因此我们需要一个辅助变量来遍历
        HeroNode temp = head.next;
        while (true){
            if (temp == null){
                break;
            }
            // 输出节点信息
            System.out.println(temp);
            // 打印完成后，需要将temp 后移
            temp = temp.next;
        }
    }

}


// 定义Hernode 每个HeroNode 对象就是一个节点
class HeroNode {

    public int no;
    public String name;
    public String nickName;
    public HeroNode next; // 指向下一个节点

    // 构造器
    public HeroNode(int no, String name, String nickName) {
        this.no = no;
        this.name = name;
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "HeroNode{" +
                "no=" + no +
                ", name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}
