package cn.wr.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RWang
 * @Date 2022/4/20
 */

@Data
public class SeniorStudent  extends Student{
    private String level;


    public static void main(String[] args) {
//        Student student = new Student();
//        student.setAge(10);
        SeniorStudent seniorStudent = new SeniorStudent();
        int age = seniorStudent.getAge();
        System.out.println(age);

        List<String> approvalNumbers = new ArrayList<>();
        int size = approvalNumbers.size();
        System.out.println(size);
    }
}
