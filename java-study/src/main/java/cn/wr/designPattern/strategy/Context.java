package cn.wr.designPattern.strategy;

/**
 * @author : WangRui
 * @date : 2023/2/28
 */

public class Context {

    private StrategyInterface strategyInterface;

    public Context(StrategyInterface strategyInterface) {
        this.strategyInterface = strategyInterface;
    }

    public void setStrategyInterface(StrategyInterface strategyInterface) {
        this.strategyInterface = strategyInterface;
    }

    public void operate() {
        this.strategyInterface.operate();
    }

    public static void main(String[] args) {
        //策略模式
        Context context;
        context = new Context(new StrategyDemo1());
        context.operate();

        context = new Context(new StrategyDemo2());
        context.operate();


    }
}
