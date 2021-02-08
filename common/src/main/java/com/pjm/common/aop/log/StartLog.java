package com.pjm.common.aop.log;

import java.lang.annotation.*;

/**
 * 对加入注解的method进行日志存储
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StartLog {
}
