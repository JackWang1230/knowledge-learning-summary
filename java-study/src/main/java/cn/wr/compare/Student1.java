package cn.wr.compare;

/**
 * @author : WangRui
 * @date : 2022/2/27
 */

public class Student1 implements Comparable<Student1>{

    private String stuName;
    private int age;
    private String major;
    public Student1(String stuName, int age, String major) {

        this.stuName = stuName;
        this.age = age;
        this.major = major;
    }

    public Student1() {
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

    @Override
    public int compareTo(Student1 o) {
        //如果被比较对象的年龄o.age大于this.age,返回1
        //如果被比较对象的年龄o.age小于this.age,返回-1
        //通过修改返回值，可以实现升序或降序排序
        if (this.age < o.age) {
            return -1;
        } else if (this.age > o.age) {
            return 1;
        }
        return 0;
    }
}
