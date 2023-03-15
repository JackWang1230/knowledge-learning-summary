package cn.wr.compare;

import java.util.Comparator;

/**
 * @author : WangRui
 * @date : 2023/3/15
 */

public class StudentComparator implements Comparator<Student> {
    @Override
    public int compare(Student o1, Student o2) {

        // 保证升序排序
        /**
         *
         * 升序的概念就是 小的在前 大的在后
         * 降序的概念就是 大的在前 小的在后
         *
         *
         * 比较器概念
         *     return -1 表示 o1 和 o2不需要进行替换位置
         *     return 1  表示 o1 和 o2 需要进行位置互换
         *
         *     假设我们是升序 当o1<o2时候 o1-o2 结果是<0 符合 return<0 不更换位置的条件
         *                 当o1>o2时候 o1-o2 的结果是>0 符合 return>0 更换两者位置，
         *                 更换完成后 o2在前，o1在后，仍然是小的在前面，大的在后
         *                 因此 升序的时候 只要 保证 return o1-o2 即用o1-o2 即可
         *
         *     假设我们是降序 当o1<o2时候 o2-o1 结果是>0 符合return>0 更换位置
         *                  更换位置后 将o2放在了前面，o1放在了后面 即 大的在前 小的在后
         *                  当o1>o2时候 o2-o1 结果是<0 符合 return<0 不进行更换位置
         *                  因此 在降序的时候 只要保证 return o2-o1 即用o2-o1即可
         *
         * 本次我们要求实现的逻辑是 升序排列 那么return o1.age-o2.age 即可保证升序
         *
         */
        return o1.getAge()-o2.getAge();
    }
}
