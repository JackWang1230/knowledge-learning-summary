package cn.wr.datastruct.linedlist;

/**
 * @author : WangRui
 * @date : 2022/11/2
 */

public class DoubleLinkedListDemo {

    public static void main(String[] args) {

    }
}

// 创建双向链表
class DoubleLinkedList{

    // 初始化第一个表头节点
    private HeroNode2 head =new HeroNode2(0,"","");

    // 获取头节点
    public HeroNode2 getHead(){
        return head;
    }

    /**
     * 默认添加数据在尾部
     * @param heroNode2
     */
    public void add(HeroNode2 heroNode2){

        // 定义一个辅助节点,考虑到头部节点不可动
        HeroNode2 temp = this.head;

        // 每次添加的逻辑就是 需要首先找到添加节点的上一个节点位置
       while (true){
           if (temp.next == null){
               break;
           }
           // 依次向后移，直到找到该节点
          temp = head.next;
       }
       // 开始真正处理该节点绑定逻辑
        // 形成一个双向链表最后
        temp.next = heroNode2;
        heroNode2.pre = temp;
    }

    /**
     * 修改一个双向链表的数据
     * @param heroNode2
     */
    public void update(HeroNode2 heroNode2){

        // 借助辅助节点 寻找heroNode2的位置
        HeroNode2  temp = head;
        boolean flag = false;

        // 通过循环找到位置
        while (true){
            // 不存在双向链表
            if (temp.next == null){
                break;
            }
            if (temp.next==heroNode2.next && temp.pre == heroNode2.pre){
                flag = true;
                break;
            }
            temp = head.next;

        }
        // 进行业务逻辑处理
        if (flag){
            temp.name = heroNode2.name;
            temp.nickName = heroNode2.nickName;
            temp.no = heroNode2.no;
        }else {
            System.out.println("没有找到");
        }

    }

    /**
     * 双向链表删除一个节点
     */
    public void delete(HeroNode2 heroNode2){
        // 辅助节点
        HeroNode2 temp = head;
        // 表识查找到该节点
        Boolean flag = false;
        while (true){
            if (temp.next==null){
                System.out.println("没有节点");
                break;
            }
            if (temp.next.pre==heroNode2.pre && temp.next.next==heroNode2.next){
                flag = true;
                break;
            }
            temp = head.next;
        }
        if (flag){
            temp.pre.next = temp.next;
            // 直接写 temp.next.pre = temp.pre有问题 ？
            // 因删除最后一个节点的时候 没有下一个值
            if (temp.next != null){
                temp.next.pre = temp.pre;
            }
        }else {
            System.out.println("不存在该节点");
        }

    }
    // 遍历双向链表的方法
    public void list(){

        if (head.next == null){
            return;
        }
        // 辅助节点temp
        HeroNode2 temp = head.next;
        while (true){
            if (temp== null){
                break;
            }
            System.out.println(temp);
            temp = temp.next;
        }

    }

}

class HeroNode2{

    public int no;
    public String name;
    public String nickName;
    public HeroNode2 next=null; // 指向下一个节点 默认null
    public HeroNode2 pre= null; // 指向前一个节点 默认 null

    public HeroNode2(int no, String name, String nickName) {
        this.no = no;
        this.name = name;
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "HeroNode2{" +
                "no=" + no +
                ", name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", next=" + next +
                ", pre=" + pre +
                '}';
    }
}
