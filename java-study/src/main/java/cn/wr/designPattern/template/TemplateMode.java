package cn.wr.designPattern.template;

/**
 * @author : WangRui
 * @date : 2023/2/28
 */

public class TemplateMode {

    public static void main(String[] args) {

        //模板方法模式
        BMWModel bmw1 = new BMWModel1();
        bmw1.run();

        BMWModel bmw2 = new BMWModel2();
        bmw2.run();
    }
}
