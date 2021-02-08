package com.pjm.common.aop.cache;

import com.pjm.common.util.JedisUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Aspect
@Component
public class EnableCacheImpl {
    @Autowired
    private JedisUtil jedisUtil;

    @SneakyThrows
    @Around("@annotation(com.pjm.common.aop.cache.EnableCache) && @annotation(enableCache)")
    public Object around(ProceedingJoinPoint point, EnableCache enableCache) {
        Object res = null;
        if (Objects.isNull(enableCache.key())) {
            throw new Exception("服务器异常");
        }
        String key = "pjm:cloud:cache:method:" + enableCache.key();
        Object cacheValue = jedisUtil.getObject(key);
        if (Objects.nonNull(cacheValue)) {
            return cacheValue;
        }
        try {
            res = point.proceed();
            if (Objects.isNull(enableCache.expirTime())) {
                jedisUtil.setObject(key, res);
            } else {
                jedisUtil.setObject(key, res, enableCache.expirTime());
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return res;
    }
}
