package com.wr.annotation;

import com.wr.enums.PayTypeEnum;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PayType {

    /**
     * 支付方式的值为PayTypeEnum枚举值
     * @return
     */
    PayTypeEnum value();
}
