package com.pjm.gatewayservice.filter.auth;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.pjm.common.exception.CustomException;
import com.pjm.common.util.JedisUtil;

import com.pjm.nacosapi.entity.WhiteListFilter;
import com.pjm.nacosapi.service.NacosApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.ApplicationContext;
import com.pjm.common.util.UserUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.Resource;
import javax.inject.Qualifier;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Component
@RefreshScope
@Slf4j
public class GatewayWhiteListFilter extends BaseGateWayAbsFilter {
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private UserUtil userUtil;
    @Resource
    private NacosApiClient nacosApiClient;
    @Value(value = "${com.pjm.filter.enable.white}")
    private boolean filterEnable;
    @Resource(name = "gatewayLoginFilter")
    public BaseGateWayAbsFilter next;
    private Set<WhiteListFilter> applicationNameSet;

    @Override
    public void doFilter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("filterEnable：{}" + filterEnable);
        String applicationName = userUtil.getReqApplicationName(exchange);
        String interfaceName = userUtil.getInterFaceName(exchange);
        if (!filterEnable) {
            log.info("GatewayWhiteListFilter未开启");
            if (this.next == null) {
                return;
            } else {
                next.doFilter(exchange, chain);
            }
//            throw new CustomException("测试异常CustomException");
        }
//        applicationNameSet = (Set<WhiteListFilter>) jedisUtil.getObject("pjm:cloud:cache:whiteList");
//        if (Objects.isNull(applicationNameSet)) {
//            applicationNameSet = nacosApiClient.getApplicationNameSet();
//            jedisUtil.setObject("cloud:cache:whiteList", applicationNameSet, 3600000);
//        }
        applicationNameSet = nacosApiClient.getApplicationNameSet();
        System.out.println("进入WhiteListFilter");
        List<WhiteListFilter> filterList = applicationNameSet.stream().filter(item -> Pattern.matches(item.getFilterCode(), applicationName)).collect(Collectors.toList());
        if (filterList.size() > 0) {
            Set<WhiteListFilter> interfaceSet = new HashSet<>();
            for (int i = 0; i < filterList.size(); i++) {
//                interfaceSet.addAll((Set<WhiteListFilter>) jedisUtil.getObject("pjm:cloud:cache:whiteList:" + filterList.get(i).getFilterCode()));
                interfaceSet.addAll(nacosApiClient.getInterfaceNameSetByApplicationName(new WhiteListFilter().setFilterCode(filterList.get(i).getFilterCode())));
            }
//            if (CollectionUtils.isEmpty(interfaceSet)) {
//                for (int i = 0; i < filterList.size(); i++) {
//                    Set<WhiteListFilter> temp = nacosApiClient.getInterfaceNameSetByApplicationName(new WhiteListFilter().setFilterCode(userUtil.getReqApplicationName(exchange)));
//                    interfaceSet.addAll(temp);
//                    jedisUtil.setObject("pjm:cloud:cache:whiteList:" + userUtil.getReqApplicationName(exchange), temp, 3600000);
//                }
////                interfaceSet = nacosApiClient.getInterfaceNameSetByApplicationName(new WhiteListFilter().setFilterCode(userUtil.getReqApplicationName(exchange)));
//            }
            List<WhiteListFilter> interFaceList = interfaceSet.stream().
                    filter(item -> Pattern.matches(item.getFilterCode(), interfaceName)).
                    collect(Collectors.toList());
            log.info(interfaceName);
            if (interFaceList.size() > 0) {
                System.out.println("通过白名单");
                return;
            }
        }
        if (this.next == null) {
//            System.out.println("WhiteListFilter跳出责任链,抛出异常");
//            chain.filter(exchange);
            throw new CustomException("登陆异常");
        } else {
            next.doFilter(exchange, chain);
        }
    }
}
