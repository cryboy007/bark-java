package com.tao.common.retry;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author william
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface NeedRetryMethod {

    @AliasFor("name") String value() default "";

    @AliasFor("value") String name() default "";

    Class<? extends Throwable>[] needRetryFor() default {NeedRetryException.class};

    int maxRetryTimes() default 3;

    String billNo() default "";
}
