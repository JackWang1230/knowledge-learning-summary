package cn.wr.designPattern.proxy;

/**
 * @author : WangRui
 * @date : 2023/2/28
 */

//定义被代理类，实现接口
public class HouseBuyer implements BuyHouse {
    @Override
    public void buyHouse() {
        System.out.println("我是买房者，我要买房");
    }
}
