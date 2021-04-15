package com.pjm.gatewayservice.factory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pjm.common.common.Constant;
import com.pjm.common.util.JwtUtil;
import com.pjm.common.util.common.StringUtil;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 修改请求参数GatewayFilterFactory拦截器（类名格式必须为filterName + GatewayFilterFactory）
 * 注：在GlobalFilter里面处理参数不成功，必须在GatewayFilter处理才行。很神奇。。。。
 */
@Slf4j
@Component
public class InjectParameterGatewayFilterFactory extends AbstractGatewayFilterFactory<InjectParameterGatewayFilterFactory.Config> {

    @Autowired
    private JwtUtil jwtUtil;
//    private static final Logger logger = LoggerFactory.getLogger(InjectParameterGatewayFilterFactory.class);


    public InjectParameterGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // grab configuration from Config object
        return (exchange, chain) -> {
            log.info("自定义注入参数网关拦截器生效");
            return handleParameter(exchange, chain);
        };
    }

    public static class Config {
        //Put the configuration properties for your filter here
    }

    /**
     * 处理请求(添加全局类参数)
     *
     * @param exchange
     * @param chain
     * @return
     */
    private Mono<Void> handleParameter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        HttpMethod method = serverHttpRequest.getMethod();
        String contentType = serverHttpRequest.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        String token = serverHttpRequest.getHeaders().getFirst("Authorization");
        //post请求时，如果是文件上传之类的请求，不修改请求消息体
        if (method == HttpMethod.POST && (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equalsIgnoreCase(contentType)
                || MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType))) {
            // TODO: 2018/12/21 参考api文档中GatewapFilter中“修改请求消息体拦截器”：ModifyRequestBodyGatewayFilterFactory.java
            //从请求里获取Post请求体
            String bodyStr = resolveBodyFromRequest(serverHttpRequest);

            // 这种处理方式，必须保证post请求时，原始post表单必须有数据过来，不然会报错
            if (org.apache.commons.lang3.StringUtils.isBlank(bodyStr)) {
                log.error("请求异常：{} POST请求必须传递参数", serverHttpRequest.getURI().getRawPath());
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return response.setComplete();
            }

//            //application/x-www-form-urlencoded和application/json才添加参数
//            //其他上传文件之类的，不做参数处理，因为文件流添加参数，文件原格式就会出问题了
//            if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equalsIgnoreCase(contentType)) {
//                // 普通键值对，增加参数
//                bodyStr = String.format(bodyStr+"&%s=%s&%s=%s",ServiceConstants.COMMON_PARAMETER_ENTERPRISEID,authenticationVO.getEnterpriseId()
//                        ,ServiceConstants.COMMON_PARAMETER_USERID,authenticationVO.getUserId());
//            }
            if (MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType)) {
                // json，增加参数
                JSONObject json = null;
                if (org.apache.commons.lang3.StringUtils.isBlank(bodyStr)) {
                    json = new JSONObject();
                } else {
                    json = JSON.parseObject(bodyStr);//创建jsonObject对象
                }
                // 转换回字符串
                bodyStr = JSON.toJSONString(json);
            }
            //记录日志
            log.info("全局参数处理: {} url：{} 参数：{}", method.toString(), serverHttpRequest.getURI().getRawPath(), bodyStr);

            //下面的将请求体再次封装写回到request里，传到下一级，否则，由于请求体已被消费，后续的服务将取不到值
            URI uri = serverHttpRequest.getURI();
            URI newUri = UriComponentsBuilder.fromUri(uri).build(true).toUri();
            ServerHttpRequest request = exchange.getRequest().mutate().uri(newUri).build();
            DataBuffer bodyDataBuffer = stringBuffer(bodyStr);
            Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);

            // 定义新的消息头
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());

            // 添加消息头
//            headers.set(ServiceConstants.SHIRO_SESSION_PRINCIPALS,GsonUtils.toJson(authenticationVO));
            if (StringUtil.isNotBlank(token)) {
                headers.set("userAccount", jwtUtil.getClaim(token, Constant.ACCOUNT));
            }
            // 由于修改了传递参数，需要重新设置CONTENT_LENGTH，长度是字节长度，不是字符串长度
            int length = bodyStr.getBytes().length;
            headers.remove(HttpHeaders.CONTENT_LENGTH);
            headers.setContentLength(length);

            // 设置CONTENT_TYPE
            if (org.apache.commons.lang3.StringUtils.isNotBlank(contentType)) {
                headers.set(HttpHeaders.CONTENT_TYPE, contentType);
            }

            // 由于post的body只能订阅一次，由于上面代码中已经订阅过一次body。所以要再次封装请求到request才行，不然会报错请求已经订阅过
            request = new ServerHttpRequestDecorator(request) {
                @Override
                public HttpHeaders getHeaders() {
                    long contentLength = headers.getContentLength();
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.putAll(super.getHeaders());
                    if (contentLength > 0) {
                        httpHeaders.setContentLength(contentLength);
                    } else {
                        httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                    }
                    return httpHeaders;
                }

                @Override
                public Flux<DataBuffer> getBody() {
                    return bodyFlux;
                }
            };

            //封装request，传给下一级
            request.mutate().header(HttpHeaders.CONTENT_LENGTH, Integer.toString(bodyStr.length()));
            return chain.filter(exchange.mutate().request(request).build());
        } else if (method == HttpMethod.GET) {
            // TODO: 2018/12/21 参考api文档中GatewapFilter中“添加请求参数拦截器”：AddRequestParameterGatewayFilterFactory.java

            // 获取原参数
            URI uri = serverHttpRequest.getURI();
            StringBuilder query = new StringBuilder();
            String originalQuery = uri.getRawQuery();
            if (org.springframework.util.StringUtils.hasText(originalQuery)) {
                query.append(originalQuery);
                if (originalQuery.charAt(originalQuery.length() - 1) != '&') {
                    query.append('&');
                }
            }

            //记录日志
            log.info("全局参数处理: {} url：{} 参数：{}", method.toString(), serverHttpRequest.getURI().getRawPath(), query.toString());
            // 添加查询参数
//            query.append(ServiceConstants.COMMON_PARAMETER_ENTERPRISEID+"="+authenticationVO.getEnterpriseId()
//                    +"&"+ServiceConstants.COMMON_PARAMETER_USERID+"="+authenticationVO.getUserId());

            // 替换查询参数
            URI newUri = UriComponentsBuilder.fromUri(uri)
                    .replaceQuery(query.toString())
                    .build(true)
                    .toUri();

            ServerHttpRequest request = exchange.getRequest().mutate().uri(newUri).build();
            return chain.filter(exchange.mutate().request(request).build());
        }

        return chain.filter(exchange);
    }


    /**
     * 从Flux<DataBuffer>中获取字符串的方法
     *
     * @return 请求体
     */
    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();

        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        //获取request body
        return bodyRef.get();
    }

    /**
     * 字符串转DataBuffer
     *
     * @param value
     * @return
     */
    private DataBuffer stringBuffer(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }

}
