package com.pjm.gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <Description>
 */
@Configuration
@Slf4j
public class GatewayRoutesConfiguration {
    /**
     * java 配置 server 服务路由
     * @param builder
     * @return
     */
//    @Bean
//    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
//        log.info("ServerGatewayFilter filtet........");
//        return builder.routes()
//                .route(r ->
//                        r.path("/**")
//                                .filters(
//                                        f -> f.stripPrefix(1)
//                                                .filters(new ServerGatewayFilter())
//                                )
//                                .uri("lb://**")
//                )
//                .build();
//    }
}
