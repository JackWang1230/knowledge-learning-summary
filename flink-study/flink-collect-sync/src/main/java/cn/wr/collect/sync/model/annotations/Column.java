package cn.wr.collect.sync.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {

    String name();

    Type type() default Type.Varchar;

    public static enum Type {
        Varchar, Datetime, Timestamp, Int, Bigint, Decimal;
    }
}
