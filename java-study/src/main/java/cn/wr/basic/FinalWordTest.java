package cn.wr.basic;

/**
 * @author RWang
 * @Date 2022/3/16
 */

public class FinalWordTest {

    // 被final 修改后的成员变量 不可更改
    private final int a=10 ;

    private void testData(){

    }

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        FinalWordTest finalWordTest = FinalWordTest.class.newInstance();

    }
}
