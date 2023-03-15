package cn.wr.compare;

/**
 * @author : WangRui
 * @date : 2023/3/15
 */

public class Student {

    private String stuName;
    private int age;
    private String major;

    public Student(String stuName, int age, String major) {
        this.stuName = stuName;
        this.age = age;
        this.major = major;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void sayHello() {
        System.out.println("大家好！我叫" + this.stuName + "，今年" + age + "岁了。");
    }
}
