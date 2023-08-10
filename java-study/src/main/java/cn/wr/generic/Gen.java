package cn.wr.generic;

/**
 * 定义接口 时指定一个类型形参 该参为E
 * @param <E>
 */
public interface Gen<E> {

    E getGenInfo();

    void addGenInfo(E e);
}
