package cn.wr.collect.sync.utils;

public class MathUtil {
    /**
     * 生成随机数 [min, max)
     * @param min
     * @param max
     * @return
     */
    public static int random(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            int random = MathUtil.random(1, 5);
            System.out.println(random);
        }
    }
}
