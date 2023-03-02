package cn.wr.designPattern.sample;

import java.lang.annotation.*;

/**
 * 登录类型自动义注解类
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoginChannel {

    String code();
}
