package cn.wr.lambdaSyntax;

// 第一个T 表示这个是一个范型类 后续才可以在参数中使用
@FunctionalInterface
public interface NoneReturnSingleParameterObject {
    void test(User a);
}
