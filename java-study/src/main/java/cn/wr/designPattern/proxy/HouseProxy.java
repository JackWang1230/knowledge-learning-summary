package cn.wr.designPattern.proxy;

/**
 * @author : WangRui
 * @date : 2023/2/28
 */
//定义代理类，也实现接口
public class HouseProxy implements BuyHouse {

    private BuyHouse buyHouse;

    public HouseProxy(BuyHouse buyHouse) {
        this.buyHouse = buyHouse;
    }

    @Override
    public void buyHouse() {
        //重写接口中的方法，在接口方法原来的内容基础加上一些代理动作
        System.out.println("买房前准备");
        buyHouse.buyHouse();
        System.out.println("买房后售后");
    }
}
