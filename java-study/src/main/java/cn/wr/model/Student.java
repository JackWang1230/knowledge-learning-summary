package cn.wr.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Builder;
import lombok.Data;

import java.util.HashSet;

/**
 * @author RWang
 * @Date 2022/1/27
 */
@Data

public class Student {

    private String name;
    private int age = 10;
    private float height;

//    public static void main(String[] args) throws JsonProcessingException {
//        Student.builder().age(1).height(11);
//    }


    public static void main(String[] args) {
        HashSet<String> strings = new HashSet<>();
        strings.add("a");
        strings.add("b");
        strings.add("c");

        String s = strings.toString();
        System.out.println(s);
    }
}
