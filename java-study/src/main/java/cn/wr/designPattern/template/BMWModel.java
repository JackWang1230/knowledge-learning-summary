package cn.wr.designPattern.template;

/**
 * @author : WangRui
 * @date : 2023/2/28
 */

public abstract class BMWModel {

    public abstract void start();

    public abstract void alarm();

    public abstract void stop();

    public void run() {
        this.start();
        this.alarm();
        this.stop();
    }
}
