package cn.wr.collect.sync.dao;

import java.util.List;
import java.util.Map;

public interface QueryLimitDAO_V2<T> {

    /**
     * 查询分页
     * @param offsetId    偏移id
     * @param pageSize  分页大小
     * @param params    自定义参数
     * @return
     */
    List<T> pageQuery(long offsetId, int pageSize, Map<String, Object> params);

    /**
     * 单条数据查询
     * @param params
     * @return
     */
    T querySingle(Map<String, Object> params);
}
