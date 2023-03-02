package cn.wr.designPattern.factory;

/**
 * @author : WangRui
 * @date : 2023/2/28
 */

public class BasketBall implements SportsInterface {
    @Override
    public void like() {
        System.out.println("我喜欢打篮球！");
    }

    @Override
    public void notLike() {
        System.out.println("我不喜欢打篮球！");
    }
}
