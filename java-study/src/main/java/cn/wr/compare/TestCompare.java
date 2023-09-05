package cn.wr.compare;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author RWang
 * @Date 2022/1/27
 */

public class TestCompare  {

    public static void main(String[] args) {


        ArrayList<Student> students = new ArrayList<>();
        Student student1 = new Student("abc", 30, "soft");
        Student student2 = new Student("abd", 19, "soft1");
        Student student3 = new Student("abm", 40, "soft3");
        students.add(student1);
        students.add(student2);
        students.add(student3);
        System.out.println("排序前：");
        //使用foreach遍历stuList，并调用stuList中每个对象的sayHello()方法
        for (Student s : students) {
            s.sayHello();
        }
        //使用Collections.sort排序，第一个参数是List实例名第二个参数是实现Comparator接口
        //在外部比较器实现compare方法进行通过比较器排序
        System.out.println("升序排序后：");
        Collections.sort(students,new StudentComparator());

        // 降序
        List<Student> students1 = students.stream().sorted((x, y) -> y.getAge() - x.getAge()).collect(Collectors.toList());

        // 升序
        List<Student> students2 = students.stream().sorted(Comparator.comparing(Student::getAge)).collect(Collectors.toList());

        // collections.sort 进行排序完成后，数组中原先存储的元素位置会在数组中基于comparable后的数据进行重新排序
        for (Student s : students) {
            s.sayHello();
        }

        System.out.println("------");
        for (Student s : students1) {
            s.sayHello();
        }

        System.out.println("------");
        for (Student s : students2) {
            s.sayHello();
        }
    }
}
