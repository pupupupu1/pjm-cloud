package com.pjm.gatewayservice.common;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class GlobalExceptionHandler extends DefaultErrorWebExceptionHandler {

    @Resource
    private ExceptionHandlerCore exceptionHandlerCore;

    public GlobalExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        Throwable error = super.getError(request);
        // todo 添加自己处理异常的逻辑
        Object o = exceptionHandlerCore.handlerException(error);
        String json= JSON.toJSONString(o);
        Map<String,Object> res=JSON.parseObject(json,HashMap.class);
        if (Objects.isNull(res.get("data"))){
            res.put("data",null);
        }
        return res;
    }

    // 指定响应处理方法为JSON处理的方法
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    //设置返回状态码，由于我的返回json里面已经包含状态码了，不用这里的状态码，所以这里直接设置200即可
    @Override
    protected HttpStatus getHttpStatus(Map<String, Object> errorAttributes) {
        return HttpStatus.OK;
    }
}
