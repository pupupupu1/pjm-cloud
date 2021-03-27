package com.pjm.gatewayservice.filter;

import com.pjm.gatewayservice.filter.auth.GatewayInterceptFilter;
import com.pjm.gatewayservice.filter.auth.GatewayLoginFilter;
import com.pjm.gatewayservice.filter.auth.GatewayWhiteListFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ServerGatewayFilter implements GlobalFilter, Ordered {
    @Autowired
    private GatewayWhiteListFilter gatewayWhiteListFilter;
    @Autowired
    private GatewayInterceptFilter gatewayInterceptFilter;
    @Autowired
    private GatewayLoginFilter gatewayLoginFilter;

    @Override
    @Order(-999)
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //跨域配置
//        cros(exchange);
        log.info("ServerGatewayFilter filter");
//        gatewayLoginFilter.next = gatewayInterceptFilter;
//        gatewayWhiteListFilter.next = gatewayLoginFilter;
        gatewayWhiteListFilter.doFilter(exchange, chain);
        return chain.filter(exchange);
    }

    public void cros(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
//        if (!CorsUtils.isCorsRequest(request)) {
//            return gatewayFilterChain.filter(serverWebExchange);
//        }
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, PUT, OPTIONS, DELETE, PATCH");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
        headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
        if (request.getMethod() == HttpMethod.OPTIONS) {
            response.setStatusCode(HttpStatus.OK);
        }
    }

    @Override
    public int getOrder() {
        return -999;
    }
}