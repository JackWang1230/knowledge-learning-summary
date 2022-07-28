package cn.wr.collect.sync.utils;

import cn.wr.collect.sync.dao.QueryLimitDAO_V2;
import cn.wr.collect.sync.dao.QueryLimitDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryUtil.class);
    public static final Integer QUERY_PAGE_SIZE = 10000;
    // public static final Integer[] its = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    public static final Integer[] its = new Integer[]{0};

    public static <R> List<R> findLimitEnhance(Function<? super Integer, ? extends Stream<? extends R>> mapper) {
        List<R> collect = Stream.of(its).parallel().flatMap(mapper).collect(Collectors.toList());
        return collect;
    }

    // 并行流起多线程一起查,查完合并结果
    public static <R> List<R> findLimitPlus(QueryLimitDao<R> queryLimitDao, int i,
                                            Map<String, Object> params,
                                            Connection connection) {
        long start = System.currentTimeMillis();

        long offset = i * QUERY_PAGE_SIZE;
        List<R> collect = queryLimitDao.findLimit(offset, QUERY_PAGE_SIZE, params, connection);

        long end = System.currentTimeMillis();
        LOGGER.info("QueryUtil class:{}, page:{}, size:{}, time:{}(ms), params:{}",
                queryLimitDao.getClass().getName(), i, collect.size(), end - start, params);
        return collect;
    }

    public static <R> List<R> pageQueryByNo_V2(QueryLimitDAO_V2<R> queryLimitDAO, int pageNo,
                                            Map<String, Object> params) {
        long start = System.currentTimeMillis();
        long offset = pageNo * QUERY_PAGE_SIZE;
        List<R> collect = queryLimitDAO.pageQuery(offset, QUERY_PAGE_SIZE, params);
        long end = System.currentTimeMillis();
        LOGGER.info("QueryUtil class:{}, params:{}, pageNo:{}, size:{}, time:{}(ms)",
                queryLimitDAO.getClass().getName(), params, pageNo, collect.size(), (end - start));
        return collect;
    }

    public static <R> List<R> pageQueryByOffset_V2(QueryLimitDAO_V2<R> queryLimitDAO, int offset,
                                            Map<String, Object> params) {
        long start = System.currentTimeMillis();
        List<R> collect = queryLimitDAO.pageQuery(offset, QUERY_PAGE_SIZE, params);
        long end = System.currentTimeMillis();
        LOGGER.info("QueryUtil class:{}, params:{}, offset:{}, size:{}, time:{}(ms)",
                queryLimitDAO.getClass().getName(), params, offset, collect.size(), (end - start));
        return collect;
    }
}
