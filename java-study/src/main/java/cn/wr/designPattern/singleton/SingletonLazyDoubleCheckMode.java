package cn.wr.designPattern.singleton;

/**
 * @author : WangRui
 * @date : 2023/6/29
 */

public class SingletonLazyDoubleCheckMode {

    /**
     * volatile 禁止语义重排序,保证下一个读取操作会在前一个写操作之后发生 jdk5中引入该关键字
     */
    private  volatile  static SingletonLazyDoubleCheckMode singletonLazyDoubleCheckMode = null;

    private SingletonLazyDoubleCheckMode(){}

    public static SingletonLazyDoubleCheckMode getInstance(){
        if (null == singletonLazyDoubleCheckMode){
            // 同步锁后面加上if判断 防止创建多个对象 保证单例
            synchronized (SingletonLazyDoubleCheckMode.class){
                if (null == singletonLazyDoubleCheckMode){
                    singletonLazyDoubleCheckMode = new SingletonLazyDoubleCheckMode();
                }
            }
        }
        return singletonLazyDoubleCheckMode;

    }
}
