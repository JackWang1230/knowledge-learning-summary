package cn.wr.model;


import lombok.Builder;
import lombok.Data;


/**
 * @author RWang
 * @Date 2022/4/20
 */


@Builder
public class SeniorStudent  extends Student{
    private String level;


//    public static void main(String[] args) throws JsonProcessingException {
////        Student student = new Student();
////        student.setAge(10);
//        SeniorStudent seniorStudent = new SeniorStudent();
//        int age = seniorStudent.getAge();
//        System.out.println(age);
//
//        String a ="{\"name\":\"wr\",\"age\":12,\"height\":1.2}";
//        ObjectMapper objectMapper = new ObjectMapper();
//        SeniorStudent student = objectMapper.readValue(a, SeniorStudent.class);
//        int age1 = student.getAge();
//        System.out.println(age1);
//
////        List<String> approvalNumbers = new ArrayList<>();
////        int size = approvalNumbers.size();
////        System.out.println(size);
//    }
}
