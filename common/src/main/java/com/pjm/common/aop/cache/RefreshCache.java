package com.pjm.common.aop.cache;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RefreshCache {
    String key();
    int sleepTime() default 1000;
}

