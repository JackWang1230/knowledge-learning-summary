package cn.wr.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Builder;
import lombok.Data;

/**
 * @author RWang
 * @Date 2022/1/27
 */
@Data

public class Student {

    private String name;
    private int age=10;
    private float height;

//    public static void main(String[] args) throws JsonProcessingException {
//        Student.builder().age(1).height(11);
//    }
}
