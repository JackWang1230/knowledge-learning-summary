package cn.wr.designPattern.singleton;


/**
 * 单例模式确保某一个类只要一个实例，而且自行实例化并向整个系统提供这个实例。
 * <p>
 * 单例模式保证一个类仅有一个实例，并提供一个访问它的全局访问点。
 * <p>
 * 单例模式只因在有真正的单一实例的需求时才可使用。
 *
 * @author : WangRui
 * @date : 2023/2/28
 */

public class SingletonMode {

    private static final SingletonMode singletonMode = new SingletonMode();

    private SingletonMode() {
    }

    public synchronized static SingletonMode getInstance() {
        return singletonMode;
    }

    public static void main(String[] args) {

        SingletonMode.getInstance();
    }
}
