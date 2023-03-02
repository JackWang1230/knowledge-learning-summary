package cn.wr.designPattern.template;

/**
 * @author : WangRui
 * @date : 2023/2/28
 */

public class BMWModel2 extends BMWModel {
    @Override
    public void start() {
        System.out.println("宝马2启动~");
    }

    @Override
    public void alarm() {
        System.out.println("宝马2鸣笛~");
    }

    @Override
    public void stop() {
        System.out.println("宝马2停止~");
    }
}
