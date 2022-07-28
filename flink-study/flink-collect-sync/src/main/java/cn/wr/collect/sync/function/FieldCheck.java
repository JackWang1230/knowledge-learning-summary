package cn.wr.collect.sync.function;

import cn.wr.collect.sync.constants.StandardEnum;
import cn.wr.collect.sync.model.Model;
import cn.wr.collect.sync.model.annotations.Column;
import cn.wr.collect.sync.model.annotations.Correspond;
import cn.wr.collect.sync.model.annotations.QueryField;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

public class FieldCheck {

    /**
     * 校验对应es字段是否发生变化
     * @param oldMap
     * @param clazz
     * @return
     */
    public static boolean esFieldCheck(Map<String, Object> oldMap, Class<? extends Model> clazz) {
        return Objects.nonNull(oldMap)
                && Arrays.stream(clazz.getDeclaredFields()).anyMatch(new EsFieldCheck(new ArrayList<>(oldMap.keySet())));
    }

    /**
     * 校验对应es字段是否发生变化
     * @param oldMap
     * @param clazz
     * @return
     */
    public static boolean keyCheck(Map<String, Object> oldMap, Class<? extends Model> clazz) {
        return Objects.nonNull(oldMap)
                && Arrays.stream(clazz.getDeclaredFields()).anyMatch(new KeyCheck(new ArrayList<>(oldMap.keySet())));
    }

    /**
     * 校验redis/hbase key 是否发生变更
     * @param oldMap
     * @param clazz
     * @return
     */
    public static boolean rhKeyCheck(Map<String, Object> oldMap, Class<? extends Model> clazz) {
        return Objects.nonNull(oldMap)
                && Arrays.stream(clazz.getDeclaredFields()).anyMatch(new RHKeyCheck(new ArrayList<>(oldMap.keySet())));
    }

    /**
     * 校验es字段变更
     */
    static class EsFieldCheck implements Predicate<Field> {
        private final List<String> old;

        private EsFieldCheck(List<String> old) {
            this.old = old;
        }

        @Override
        public boolean test(Field field) {
            if (!field.isAnnotationPresent(Correspond.class)) {
                return false;
            }
            if (!field.isAnnotationPresent(Column.class)) {
                return false;
            }
            Column column = field.getDeclaredAnnotation(Column.class);
            return old.contains(column.name());
        }
    }

    /**
     * 校验key是否发生变更
     */
    static class KeyCheck implements Predicate<Field> {

        private final List<String> old;

        private KeyCheck(List<String> old) {
            this.old = old;
        }

        @Override
        public boolean test(Field field) {
            if (!field.isAnnotationPresent(Correspond.class)) {
                return false;
            }
            Correspond correspond = field.getDeclaredAnnotation(Correspond.class);
            switch (correspond.type()) {
                case Key:
                case Both:
                    if (!field.isAnnotationPresent(Column.class)) {
                        return false;
                    }
                    Column column = field.getDeclaredAnnotation(Column.class);
                    return old.contains(column.name());
                case Field:
                default:
                    return false;
            }
        }
    }

    /**
     * 校验key是否发生变更
     */
    static class RHKeyCheck implements Predicate<Field> {

        private final List<String> old;

        private RHKeyCheck(List<String> old) {
            this.old = old;
        }

        @Override
        public boolean test(Field field) {
            if (!field.isAnnotationPresent(QueryField.class)) {
                return false;
            }
            if (!field.isAnnotationPresent(Column.class)) {
                return false;
            }
            Column column = field.getDeclaredAnnotation(Column.class);
            return old.contains(column.name());
        }
    }


    /**
     * 校验字段变更是否需要触发es变更
     * @param fieldList
     * @param tableName
     * @return
     */
    public static boolean standardFieldCheck(Collection<String> fieldList, String tableName) {
        return fieldList.stream().anyMatch(filed -> Objects.nonNull(StandardEnum.getType(tableName, filed)));
    }

    /**
     * 校验字段变更是否是key字段变更，老数据处理
     * @param fieldList
     * @param tableName
     * @return
     */
    public static boolean standardKeyCheck(Collection<String> fieldList, String tableName) {
        return fieldList.stream()
                .anyMatch(filed -> StandardEnum.StandardType.key == StandardEnum.getType(tableName, filed));
    }
}
