package com.pjm.gatewayservice.filter.auth;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.pjm.common.exception.CustomException;
import com.pjm.common.util.JwtUtil;
import com.pjm.common.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.Resource;
import java.util.Objects;

@RefreshScope
@Component
@Slf4j
public class GatewayLoginFilter extends BaseGateWayAbsFilter {
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private JwtUtil jwtUtil;
    @Value("${com.pjm.filter.enable.login}")
    private boolean filterEnable;
    @Resource(name = "gatewayInterceptFilter")
    public BaseGateWayAbsFilter next;
    @Override
    public void doFilter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!filterEnable) {
            log.info("GatewayLoginFilter未开启");
            if (this.next == null) {
                return;
            } else {
                next.doFilter(exchange, chain);
            }
        }
        //验证登录状态
        String token=userUtil.getToken(exchange);
        if (StringUtils.isEmpty(token)){
            throw new CustomException("无效密钥，必填");
        }
//        if (!jwtUtil.verify(token)){
//            throw new CustomException("无效密钥，不合法");
//        }
        userUtil.doLogin(exchange);
        if (Objects.nonNull(next)){
            next.doFilter(exchange,chain);
        }else {
            throw new CustomException("登录失效！");
        }
    }
}
