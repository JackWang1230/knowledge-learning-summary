package cn.wr.collect.sync.utils;

import cn.wr.collect.sync.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;


public class StoreUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreUtil.class);

    public static String buildHBaseStoreKey(String tableName, List<Field> fields, List<Method> methods, Model model) {
        StringBuilder sb = new StringBuilder();
        // sb.append(RedisConstant.REDIS_TABLE_PREFIX).append(tableName).append(RedisConstant.REDIS_TABLE_SEPARATOR);
        try {
            // key：1-1-2   1-1   横线分割
            for (int i = 0; i < fields.size(); i++) {
                if (i > 0) {
                    sb.append("-");
                }
                // sb.append(fields.get(i).getName()).append(methods.get(i).invoke(model));
                sb.append(methods.get(i).invoke(model));
            }
        } catch (Exception e) {
            LOGGER.error("### StoreUtil 执行getter方法失败! error:{}", e);
        }
        return sb.toString();
    }
}
