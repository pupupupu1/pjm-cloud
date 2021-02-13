package com.pjm.nettyapi.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
@FeignClient("pjm-service-netty")
public interface NettyClientApi {
    @GetMapping("/nettyApiClient/sendSyncAllOnlineUserLocMsg")
    public void sendSyncAllOnlineUserLocMsg();
}
