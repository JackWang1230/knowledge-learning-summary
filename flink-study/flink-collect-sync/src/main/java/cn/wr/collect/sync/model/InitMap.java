package cn.wr.collect.sync.model;

import cn.wr.collect.sync.dao.QueryLimitDao;
import cn.wr.collect.sync.model.annotations.Table;

public class InitMap {
    private Table.BaseDataTable table;
    private QueryLimitDao queryLimitDao;

    public InitMap(Table.BaseDataTable table, QueryLimitDao queryLimitDao) {
        this.table = table;
        this.queryLimitDao = queryLimitDao;
    }

    public Table.BaseDataTable getTable() {
        return table;
    }

    public void setTable(Table.BaseDataTable table) {
        this.table = table;
    }

    public QueryLimitDao getQueryLimitDao() {
        return queryLimitDao;
    }

    public void setQueryLimitDao(QueryLimitDao queryLimitDao) {
        this.queryLimitDao = queryLimitDao;
    }
}
