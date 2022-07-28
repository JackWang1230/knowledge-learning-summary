package cn.wr.collect.sync.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cache<T> {
    /**
     * 缓存时间
     */
    private long time;
    /**
     * 值
     */
    private T value;

}
