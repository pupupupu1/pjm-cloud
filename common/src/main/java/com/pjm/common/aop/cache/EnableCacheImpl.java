package com.pjm.common.aop.cache;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.pjm.common.exception.PjmException;
import com.pjm.common.util.JedisUtil;
import com.pjm.common.util.common.StringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.logging.Logger;

@Slf4j
@Aspect
@Component
public class EnableCacheImpl {
    @Autowired
    private JedisUtil jedisUtil;

    @SneakyThrows
    @Around("@annotation(com.pjm.common.aop.cache.EnableCache) && @annotation(enableCache)")
    public Object around(ProceedingJoinPoint point, EnableCache enableCache) {
        log.info("进入缓存准备");
//        point.getSignature().getDeclaringType().getMethod(point.getSignature().getName()).getAnnotatedReturnType().getType().getTypeName();
        Object res = null;
        if (Objects.isNull(enableCache.key())) {
            throw new Exception("服务器异常");
        }
        String key = "pjm:cloud:cache:method:" + point.getSignature().getName() + ":" + getCompleteKey(enableCache.key(), point.getArgs());
        Object cacheValue = jedisUtil.getObject(key);
        if (Objects.nonNull(cacheValue)) {
            log.info("返回缓存数据：{}", cacheValue);
            return cacheValue;
        }
        try {
            res = point.proceed();
            log.info("缓存返回值:{}", res);
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

    private String getCompleteKey(String key, Object[] params) throws NoSuchFieldException, IllegalAccessException {
        if (!key.contains("+")) {
            return getKey(key, params);
        }
        String[] var1 = key.split("\\+");
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : var1) {
            stringBuilder.append(getKey(s, params));
        }
        return stringBuilder.toString();
    }

    private String getKey(String key, Object[] params) throws NoSuchFieldException, IllegalAccessException {
        if (key.length() == 1 || !"$P".equals(key.substring(0, 2))) {
            return key;
        }
        int index;
        key = key.substring(2);
        if (!key.contains(":")) {
            if (!StringUtil.isInteger(key)) {
                throw new PjmException(500, "变量只能是INTEGER");
            }
            index = Integer.parseInt(key);
            Object realKey = params[index];
            if (!(realKey instanceof String)) {
                throw new PjmException(500, "指定的变量不能是复杂对象");
            }
            return String.valueOf(params[index]);
        }
        String[] keys = key.split(":");
        if (keys.length > 2) {
            throw new PjmException(500, "最多只能指定对象的一个字段value作为key");
        }
        if (!StringUtil.isInteger(keys[0])) {
            throw new PjmException(500, "变量只能是INTEGER");
        }
        index = Integer.parseInt(keys[0]);
        if (index < 0 || index >= params.length) {
            throw new PjmException(500, "下标超出变量个数");
        }
        String fieldName = keys[1];
        Object obj = params[index];
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Object value = field.get(obj);
        return String.valueOf(value);

    }

}
