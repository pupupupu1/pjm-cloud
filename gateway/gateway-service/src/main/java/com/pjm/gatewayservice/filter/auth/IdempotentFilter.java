package com.pjm.gatewayservice.filter.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;

public class IdempotentFilter extends BaseGateWayAbsFilter {
    @Value("${com.pjm.filter.enable.intercept}")
    private boolean filterEnable;

    @Override
    public void doFilter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //根据applicationName和interface进行幂等判断
    }
}
