package cn.wr.collect.sync.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface QueryLimitDao<T> extends Serializable {
    /*Integer[] its = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    static <R> List<R> findLimitEnhance(Function<? super Integer, ? extends Stream<? extends R>> mapper) {
        List<R> collect = Stream.of(its).flatMap(mapper).collect(Collectors.toList());
        return collect;
    }*/

    // List<T> findLimit(long offset, int pageSize, Map<String, Object> params, ParameterTool parameterTool);

    List<T> findLimit(long offset, int pageSize, Map<String, Object> params, Connection connection);

}
