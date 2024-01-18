package cn.wr.generic;

/**
 * 泛型类的构建
 *
 * @author : WangRui
 * @date : 2023/8/10
 */

public class GenDemo<T> {

    private T x;

    private T getX() {
        return x;
    }

    /**
     * 是一个普通方法，把泛型T以形参的方式设置值
     */
    private void setX(T x) {
        this.x = x;
    }


    /**
     * 此方法是一个泛型方法，是一个没有返回参的泛型方法
     * 在void前面带有了<E>泛型方法的标识，尖括号内的字母是任意写，A、B、C……都可以
     * void 前面的E 相当于是一个修饰符 ，并不代表该方法的返回类型
     * 如果把void的<E>去掉，编译会报错提示E cannot be resolved to a type，意思就是无法解析E
     */
    public <E> void setGenericInfo(String a,E x,T m) {

        System.out.println(a);
    }


    /**
     * 此方法是一个泛型方法,<E>表示泛型方法，且是一个带返回类型的T的泛型方法
     * 和上面的描述基本一样的，都带了泛型方法的标识符<E>,说明是泛型方法
     */
    @SuppressWarnings("unchecked")
    public <E>  T getGenericInfo(T t,E x) {

        Class<?> aClass = t.getClass();
        return t;
    }


    public static void main(String[] args) {

        GenDemo<String> demo1 = new GenDemo<>();
        GenDemo<Integer> demo2 = new GenDemo<>();
        GenDemo<Boolean> demo3 = new GenDemo<>();

        demo1.setX("111");
        demo2.setX(111);
        demo3.setX(false);

    }
}
