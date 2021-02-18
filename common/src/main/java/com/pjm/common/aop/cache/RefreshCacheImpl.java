package com.pjm.common.aop.cache;

import com.pjm.common.util.JedisUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 刷新缓存（延迟双删）
 */

@Slf4j
@Aspect
@Component
public class RefreshCacheImpl {
    @Autowired
    private JedisUtil jedisUtil;

    @Around("@annotation(com.pjm.common.aop.cache.RefreshCache) && @annotation(refreshCache)")
    public Object around(ProceedingJoinPoint point, RefreshCache refreshCache) throws Throwable {
        log.info("刷新的key为：{}", refreshCache.key());
        Set<String> keys = jedisUtil.keysS("*"+refreshCache.key()+"*");
        log.info("删除key：{}", keys);
        keys.forEach(key -> {
            jedisUtil.delKey(key);
        });
        Long startTime = System.currentTimeMillis();
        Object res = point.proceed();
        Long endTime = System.currentTimeMillis();
        log.info("线程睡眠时间：{}", refreshCache.sleepTime());
        Thread.sleep(refreshCache.sleepTime());
        keys.forEach(key -> {
            jedisUtil.delKey(key);
        });
        return res;
    }
}
