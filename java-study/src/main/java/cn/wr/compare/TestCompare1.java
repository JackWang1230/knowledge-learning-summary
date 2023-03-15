package cn.wr.compare;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author : WangRui
 * @date : 2022/2/27
 */
@Data
public class TestCompare1 {

    public static void main(String[] args) {
        ArrayList<Student1> student1s = new ArrayList<>();
        Student1 student1 = new Student1("abc", 30, "soft");
        Student1 student11 = new Student1("abd", 19, "soft1");
        Student1 student12 = new Student1("abm", 40, "soft3");
        student1s.add(student1);
        student1s.add(student11);
        student1s.add(student12);
        System.out.println("排序前：");
        //使用foreach遍历stuList，并调用stuList中每个对象的sayHello()方法
        for (Student1 s : student1s) {
            s.sayHello();
        }
        //使用Collections.sort排序，一个参数情况下是List实例名
        //使用此方法排序父类要实现Comparable接口并重写compareTo方法
        //通过重写compareTo方法，修改返回值，可以实现升序或降序排序
        System.out.println("升序排序后：");
        Collections.sort(student1s);
        // collections.sort 进行排序完成后，数组中原先存储的元素位置会在数组中基于comparable后的数据进行重新排序
        for (Student1 s : student1s) {
            s.sayHello();
        }


        System.out.println("----------------------------------");

    }

}
