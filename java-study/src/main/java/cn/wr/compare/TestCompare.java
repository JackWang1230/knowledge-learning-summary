package cn.wr.compare;

import cn.wr.model.Student;

import java.util.Collections;
import java.util.Comparator;

/**
 * @author RWang
 * @Date 2022/1/27
 */

public class TestCompare implements Comparable<Student> {
    @Override
    public int compareTo(Student o) {
//        Collections.sort();
        // 如果是 正数 代表升序 负数代表降序
//        new Comparator<>()
        return 0;
    }

    public static void main(String[] args) {
        TestCompare testCompare = new TestCompare();
    }
}
