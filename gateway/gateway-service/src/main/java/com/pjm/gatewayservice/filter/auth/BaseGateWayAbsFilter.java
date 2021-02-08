package com.pjm.gatewayservice.filter.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Set;

//@Component
public abstract class BaseGateWayAbsFilter {
    @Value("${com.pjm.filter.enable.intercept}")
    private boolean filterEnable;
//    public Set<String> set;
    public abstract void doFilter(ServerWebExchange exchange, GatewayFilterChain chain);
}
