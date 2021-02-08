package com.pjm.gatewayservice.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@Configuration
public class ExceptionHandlerCore implements ApplicationRunner {

    /**
     * key是处理异常的类型
     * value是处理异常的方法
     */
    private HashMap<Class<? extends Throwable>, Node> exceptionHandlerMap;

    /**
     * 解析类上的注解
     * 将处理异常的方法注册到map中
     */
    private void register(Object exceptionAdvice) {
        Method[] methods = exceptionAdvice.getClass().getMethods();
        Arrays.stream(methods).forEach(method -> {
            ExceptionHandler exceptionHandler = method.getAnnotation(ExceptionHandler.class);
            if (Objects.isNull(exceptionHandler)) {
                return;
            }
            Arrays.asList(exceptionHandler.value()).forEach(a -> exceptionHandlerMap.put(a, new Node(method, exceptionAdvice)));
        });
    }

    /**
     * 根据异常对象获取解决异常的方法
     *
     * @param throwable 异常对象
     * @return handler method
     */
    private Node getHandlerExceptionMethodNode(Throwable throwable) {
        ArrayList<Class<?>> superClass = this.getSuperClass(throwable.getClass());
        for (Class<?> aClass : superClass) {
            Node handlerNode = null;
            if ((handlerNode = exceptionHandlerMap.get(aClass)) != null) {
                return handlerNode;
            }
        }
        return null;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        exceptionHandlerMap = new HashMap<>();
        log.info("-------------异常处理容器内存分配完毕-------------");
        Map<String, Object> beans = SpringContextHolder.getBeansWithAnnotation(RestControllerAdvice.class);
        log.info("-------------异常处理对象获取完毕-------------");
        beans.keySet()
                .stream()
                .map(beans::get)
                .forEach(this::register);
        log.info("-------------异常处理方法注册完毕-------------");
    }

    /**
     * 对外暴露的处理异常的方法
     *
     * @param throwable 处理的异常
     * @return 调用异常后的返回值
     */
    public Object handlerException(Throwable throwable) {
        throwable.printStackTrace();
        Node exceptionMethodNode = this.getHandlerExceptionMethodNode(throwable);
        if (Objects.isNull(exceptionMethodNode)) {
            throw new RuntimeException("没有处理异常的方法");
        }

        Object returnResult = null;
        try {
            returnResult = exceptionMethodNode.method.invoke(exceptionMethodNode.thisObj, throwable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnResult;
    }

    /**
     * 用于存放方法和方法所在的实例
     */
    private static class Node {
        Node(Method method, Object thisObj) {
            this.method = method;
            this.thisObj = thisObj;
        }

        Method method;
        Object thisObj;
    }


    /**
     * 获取该类的class以及所有父的class
     *
     * @param clazz this.class
     * @return list
     */
    public ArrayList<Class<?>> getSuperClass(Class<?> clazz) {
        ArrayList<Class<?>> classes = new ArrayList<>();
        classes.add(clazz);
        Class<?> suCl = clazz.getSuperclass();
        while (suCl != null) {
            classes.add(suCl);
            suCl = suCl.getSuperclass();
        }
        return classes;
    }
}


