package cn.wr.designPattern.proxy;

/**
 * @author : WangRui
 * @date : 2023/2/28
 */

public class ProxyMode {

    public static void main(String[] args) {

        //代理模式
        BuyHouse buyHouse = new HouseBuyer(); //声明接口，但new被代理类的实例，下面要传入代理中
        HouseProxy houseProxy = new HouseProxy(buyHouse);
        houseProxy.buyHouse();

    }
}
