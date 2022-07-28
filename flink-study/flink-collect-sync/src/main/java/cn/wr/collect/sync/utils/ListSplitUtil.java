package cn.wr.collect.sync.utils;

import java.util.ArrayList;
import java.util.List;

public class ListSplitUtil {
    /**允许使用的最大记录数量*/
    private static final int SELECT_MAX_COUNT = 1000;

    public static <T> List<List<T>> splitList(List<T> lists, Integer num) {
        int maxCount;
        if (num == null || num == 0) {
            maxCount = SELECT_MAX_COUNT;
        } else {
            maxCount = num.intValue();
        }
        List<List<T>> result = new ArrayList<>();
        if (lists != null) {
            //记录数量
            int length = lists.size();
            int time = length / num;
            //计算拆分几条
            time = length % num > 0 ? time + 1 : time;
            int end;
            List<T> tmpList;
            for (int i = 1, start = 0; i <= time; i++) {
                if (i == time) {
                    end = length;
                } else {
                    end = i * maxCount;
                }
                tmpList = lists.subList(start, end);
                start = end;
                result.add(tmpList);
            }
        }
        return result;
    }

    /**
     * 按照count拆分成多个list
     * 默认拆分记录条数 1000
     * @param lists 需要拆分的数据
     * @return 返回拆分后 数据集合用于使用
     */
    public static <T> List<List<T>> splitList(List<T> lists){
        return splitList(lists,SELECT_MAX_COUNT);
    }
}
