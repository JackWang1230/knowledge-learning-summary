package cn.wr.designPattern.factory;

/**
 * @author : WangRui
 * @date : 2023/2/28
 */

public class Running implements SportsInterface {
    @Override
    public void like() {
        System.out.println("我喜欢跑步！");
    }

    @Override
    public void notLike() {

        System.out.println("我不喜欢跑步！");
    }
}
