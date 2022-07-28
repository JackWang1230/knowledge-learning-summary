package cn.wr.collect.sync.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface QueryField {
    /**
     * 数据在redis中存储为hash结构,order为0的作为hash的key,order为1的作为field
     * @return
     */
    int order() default 0;
}
