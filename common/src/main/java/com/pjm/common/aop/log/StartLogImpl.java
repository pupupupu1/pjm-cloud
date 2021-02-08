package com.pjm.common.aop.log;

import com.pjm.common.util.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class StartLogImpl {
    @Autowired
    private ApplicationContext context;
    @Pointcut("@annotation(com.pjm.common.aop.log.StartLog)")
    public void startLog(){

    }
    @Around("startLog()")
    public void around(ProceedingJoinPoint point){
        String methodName=point.getSignature().getName();
//        return methodName;
    }
}
