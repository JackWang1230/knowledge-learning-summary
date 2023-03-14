package cn.wr.compare;

/**
 * @author : WangRui
 * @date : 2022/2/27
 */

public class Student1 implements Comparable<Student1> {

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
        /**
         * 此方法目的是为了实现升序排asc(最终版)
         * 参考：https://www.cnblogs.com/ylz8401/p/16592870.html
         *
         *  o 相当于第二个对象 以下为实现思路
         *   1.) 如果 返回-1 表示不需要进行替换位置 要想满足升序 则要求this.age< o.age
         *   2.) 如果 返回 1 表示需要 将this 和 o 进行替换位置，为了满足升序要求，
         *       则需要this.age>o.age  这样 替换完位置后，刚好保证o.age作为小的值放在了前面
         *
         *   3.) 因此 下面的这个方面 实际可简写成 return this.age-o.age 实现一致的效果
         *
         *
         */
        if (this.age < o.age) {
            return -1;
        } else if (this.age > o.age) {
            return 1;
        }
        return 0;
//        return this.age-o.age;
    }
}
