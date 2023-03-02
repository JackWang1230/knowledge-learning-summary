package cn.wr.lambdaSyntax;

/**
 * 入参为T 返回的类型为 R 类型
 *
 * @param <T>
 * @param <R>
 * @param <E>
 */
@FunctionalInterface
public interface UnKnowReturnUnKnowParameter<T, R, E extends Throwable> {
    R apply(T a) throws E;
}
