package cn.wr.synclock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;

/**
 * @author RWang
 * @Date 2022/3/4
 */

public class MyReadThread implements Runnable{


    private int count;
    public MyReadThread(){
        this.count=0;
    }
    @Override
    public void run() {
        synchronized (this){
            for (int i = 0; i < 5; i++) {
                try {
                    System.out.println(Thread.currentThread().getName()+":"+count++);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) {
//        MyReadThread myReadThread = new MyReadThread();
//        //注意此刻是一个同一个实例对象
//        Thread thread1 = new Thread(myReadThread, "myReadThread1");
//        Thread thread2 = new Thread(myReadThread, "myReadThread2");
//        thread1.start();
//        thread2.start();
//
//        Thread myReadThread3 = new Thread(new MyReadThread(), "myReadThread3");
//        Thread myReadThread4 = new Thread(new MyReadThread(), "myReadThread4");
//        myReadThread3.start();
//        myReadThread4.start();

        Class<MyReadThread> myReadThreadClass = MyReadThread.class;
        String name = myReadThreadClass.getName();
        String typeName = myReadThreadClass.getTypeName();
        Field[] declaredFields = myReadThreadClass.getDeclaredFields();
        Annotation[] annotations = myReadThreadClass.getAnnotations();
        Constructor<?>[] constructors = myReadThreadClass.getConstructors();
        Field[] fields = myReadThreadClass.getFields();
        Method[] methods = myReadThreadClass.getMethods();
        Class<?>[] interfaces = myReadThreadClass.getInterfaces();
        TypeVariable<Class<MyReadThread>>[] typeParameters = myReadThreadClass.getTypeParameters();


    }
}
