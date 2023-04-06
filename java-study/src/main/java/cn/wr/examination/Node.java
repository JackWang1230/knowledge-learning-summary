package cn.wr.examination;

/**
 * @author : WangRui
 * @date : 2023/3/31
 */

public class Node {
    int data;
    Node prev, next;

    Node(int value) {
        this.data = value;
        this.prev = null;
        this.next = null;
    }
}
class DoublyLinkedList {
    Node head, tail;

    DoublyLinkedList() {
        head = tail = null;
    }

    void insert(int value) {
        Node node = new Node(value);
        if (head == null) {
            head = tail = node;
        } else if (node.data < head.data) {
            node.next = head;
            head.prev = node;
            head = node;
        } else if (node.data > tail.data) {
            node.prev = tail;
            tail.next = node;
            tail = node;
        } else {
            Node current = head;
            while (current.next != null && current.next.data < node.data) {
                current = current.next;
            }
            node.next = current.next;
            node.prev = current;
            current.next.prev = node;
            current.next = node;
        }
    }

    void print() {
        Node current = head;
        while (current != null) {
            System.out.print(current.data + " ");
            current = current.next;
        }
        System.out.println();
    }
}

class Main {
    public static void main(String[] args) {
        DoublyLinkedList list = new DoublyLinkedList();
        list.insert(18);
        list.insert(34);
        list.insert(1);
        list.insert(44);
        list.insert(65);
        list.insert(66);
        list.insert(68);
        list.print();
    }
}







