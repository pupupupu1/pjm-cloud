package com.pjm.gatewayservice.config;

import com.pjm.common.util.JedisUtil;
import com.pjm.gatewayservice.filter.auth.GatewayInterceptFilter;
import com.pjm.gatewayservice.filter.auth.GatewayWhiteListFilter;
import com.pjm.nacosapi.entity.WhiteListFilter;
import com.pjm.nacosapi.entity.ext.WhiteListFilterExt;
import com.pjm.nacosapi.service.NacosApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.WebFilter;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Configuration
public class GatewayBeanConfig {
    @Value("gateway")
    private String prefix;

    public GatewayBeanConfig(JedisUtil jedisUtil,NacosApiClient nacosApiClient) {
        Set<WhiteListFilter> applicationNameSet=(Set<WhiteListFilter>) jedisUtil.getObject("cloud:cache:whiteList");
        if (Objects.isNull(applicationNameSet)){
            log.info("开始初始化白名单");
            //初始化白名单
            applicationNameSet = nacosApiClient.getApplicationNameSet();
            jedisUtil.setObject("cloud:cache:whiteList", applicationNameSet);
            WhiteListFilterExt query = new WhiteListFilterExt();
            query.setPageNum(0).setPageSize(0);
            query.setFilterType(2d);
            List<WhiteListFilter> whiteListFilterList = nacosApiClient.getList(query).getData().getList();
            if (!CollectionUtils.isEmpty(applicationNameSet)) {
                applicationNameSet.forEach(item -> {
                    Set<WhiteListFilter> reqMethodSet = new HashSet<>();
                    whiteListFilterList.forEach(item2 -> {
                        if (item2.getFilterParentId().equals(item.getId())) {
                            reqMethodSet.add(item2);
                        }
                    });
                    if (!CollectionUtils.isEmpty(reqMethodSet)) {
                        jedisUtil.setObject("cloud:cache:whiteList:" + item.getFilterCode(), reqMethodSet);
                    }
                });
            }
        }else {
            log.info("白名单保持缓存");
        }
    }

    @Bean
    public GatewayInterceptFilter interceptFilter() {
        return new GatewayInterceptFilter();
    }

    @Bean
    public GatewayWhiteListFilter whiteListFilter() {
        return new GatewayWhiteListFilter();
    }

//    @Bean
//    @Order(-1)
//    public WebFilter apiPrefixFilter(){
//        return (exchange,chain)->{
//            ServerHttpRequest request=exchange.getRequest();
//        }
//    }

}
