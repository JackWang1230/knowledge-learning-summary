package cn.wr.designPattern.singleton;


/**
 * @author : WangRui
 * @date : 2023/6/29
 */

public class SingletonLazyMode {

    // 将这个对象的引用若加上 final修饰，则不能再次new这个对象的引用
    private static SingletonLazyMode singletonLazyMode=null;

    private SingletonLazyMode() {}

    /**
     * 该懒汉加载方式仅适用于单线程 在多线程中 存在隐患
     * @return
     */
    public static SingletonLazyMode getInstance(){
        if (null == singletonLazyMode){
             singletonLazyMode= new SingletonLazyMode();
        }
        return singletonLazyMode;
    }

    /**
     * 多线程场景可以解决线程安全
     * 但是 加上同步锁存在性能低下
     *
     * @return
     */
    public static synchronized SingletonLazyMode getSyncInstance(){

        if (null == singletonLazyMode){
            singletonLazyMode = new SingletonLazyMode();
        }
        return singletonLazyMode;
    }
}
