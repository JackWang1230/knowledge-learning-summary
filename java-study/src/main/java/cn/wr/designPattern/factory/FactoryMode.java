package cn.wr.designPattern.factory;

/**
 * @author : WangRui
 * @date : 2023/2/28
 */

public class FactoryMode {

    public static SportsInterface checkSports(Class clazz) {
        SportsInterface sportsInterface = null;
        try {
            sportsInterface = (SportsInterface) Class.forName(clazz.getName()).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return sportsInterface;
    }

    public static void main(String[] args) {

        SportsInterface basketball = FactoryMode.checkSports(BasketBall.class);
        SportsInterface running = FactoryMode.checkSports(Running.class);

        basketball.like();
        basketball.notLike();

        running.like();
        running.notLike();
    }
}
