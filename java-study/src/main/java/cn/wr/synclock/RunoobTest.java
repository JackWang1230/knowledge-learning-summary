package cn.wr.synclock;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author : WangRui
 * @date : 2023/2/3
 */

public class RunoobTest extends Object {

    private List synchedList;

    public RunoobTest() {
        // 创建一个同步列表
        synchedList = Collections.synchronizedList(new LinkedList<>());
    }

    // 删除列表中的元素
    public String removeElement() throws InterruptedException {
        synchronized (synchedList) {

            while (synchedList.isEmpty()) {
                System.out.println("List is empty");
                synchedList.wait();
                System.out.println("Waiting...");
            }
            String element = (String) synchedList.remove(0);
            return element;
        }
    }

    public void addElement(String element) {

        System.out.println("Opening...");
        synchronized (synchedList) {

            // 添加一个元素。并通知元素存在
            synchedList.add(element);
            System.out.println("New Element:'" + element + "'");

            synchedList.notifyAll();
            System.out.println("notifyAll called");
        }

        System.out.println("Closing....");
    }


    public static void main(String[] args) {


        RunoobTest demo = new RunoobTest();

        Runnable runA = () -> {
            try {
                String item = demo.removeElement();
                System.out.println("" + item);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        try {

            Runnable runB = () -> demo.addElement("Hello");

            Thread thread1 = new Thread(runA, "Google");
            thread1.start();
            Thread.sleep(500);

            Thread thread2 = new Thread(runA, "Runoob");
            thread2.start();
            Thread.sleep(500);

            Thread thread3 = new Thread(runB, "Taobao");
            thread3.start();

            Thread.sleep(1000);

//            thread1.interrupt();
//            thread2.interrupt();

        } catch (Exception e) {

        }


    }


}
