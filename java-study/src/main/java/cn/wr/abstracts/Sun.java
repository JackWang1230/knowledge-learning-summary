package cn.wr.abstracts;

/**
 * @author RWang
 * @Date 2022/2/11
 */

public class Sun {

    /**
     * flink 中 StreamExecutionEnvironment 实则就是设计模式中单例
     *
     * @return
     */
    public static Sun getAbc() {
        System.out.println("");
        return new Sun();
    }

    public void execute() {
        System.out.println("d");
    }


    public static void main(String[] args) {
        Sun abc = Sun.getAbc();
        abc.execute();
    }
}
