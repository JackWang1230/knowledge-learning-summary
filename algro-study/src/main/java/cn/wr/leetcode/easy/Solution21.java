package cn.wr.leetcode.easy;

/**
 * @author : WangRui
 * @date : 2022/12/26
 */

class ListNode{

    int val;
    ListNode next;
    ListNode (){}
    ListNode(int val){this.val = val; }
    ListNode(int val,ListNode next){
        this.val =val;
        this.next = next;
    }
}

/**
 * 将两个升序链表合并为一个新的 升序 链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。
 *
 * 输入：l1 = [1,2,4], l2 = [1,3,4]
 * 输出：[1,1,2,3,4,4]
 *
 * 输入：l1 = [], l2 = []
 * 输出：[]
 *
 * 输入：l1 = [], l2 = [0]
 * 输出：[0]
 *
 */
public class Solution21 {

    public ListNode mergeTwoLists(ListNode list1,ListNode list2){

        if (list1 == null){
            return list2;
        }
        if (list2 == null){
            return list1;
        }
        ListNode node = new ListNode();

        ListNode head = node;
        while (list1 != null && list2 != null){

            if (list1.val>= list2.val){
                head.next = list2;
                list2 = list2.next;
            }else if (list1.val < list2.val){
                head.next = list1;
                list1 = list1.next;
            }
            head = head.next;

            if (list1 == null){
                head.next = list2;
                return node.next;
            }
            if (list2 == null){
                head.next = list1;
                return node.next;
            }

        }

        return node.next;

    }
}
