package com.pjm.common.aop.cache;


import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableCache {
    String key();

    int expirTime() default 1000 * 10;
}
