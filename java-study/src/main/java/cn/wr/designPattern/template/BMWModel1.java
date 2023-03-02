package cn.wr.designPattern.template;

/**
 * @author : WangRui
 * @date : 2023/2/28
 */

public class BMWModel1 extends BMWModel {
    @Override
    public void start() {
        System.out.println("宝马1启动~");
    }

    @Override
    public void alarm() {
        System.out.println("宝马1鸣笛~");
    }

    @Override
    public void stop() {
        System.out.println("宝马1停止~");
    }
}
