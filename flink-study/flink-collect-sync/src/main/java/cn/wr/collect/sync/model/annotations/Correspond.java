package cn.wr.collect.sync.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Correspond {
    /**
     * es 关联类型
     */
    Type type() default Type.Field;

    /**
     * es 对应 key
     */
    /*String[] key() default {};*/

    /**
     * es 对应字段
     */
    String[] field() default {};

    /**
     * es 字段对应类型
     */
    Mode mode() default Mode.Single;

    /**
     * es关联类型
     */
    enum Type {
        Key,    // es 关联 key
        Field,   // es 关联 field
        Both
    }

    /**
     * 字段取值方式
     */
    enum Mode {
        Single, // 单字段
        Multi   // 多字段组合
    }
}
