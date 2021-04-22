package com.pjm.nettyapi.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Component
@FeignClient("pjm-service-netty")
public interface NettyClientApi {
    @GetMapping("/nettyApiClient/sendSyncAllOnlineUserLocMsg")
    public void sendSyncAllOnlineUserLocMsg();

    @PostMapping("/nettyApiClient/callUser4FriendAddRequest")
    public void callUser4FriendAddRequest(@RequestBody Map<String,String> info);

    @PostMapping("/nettyApiClient/callUser4GroupAddRequest")
    public void callUser4GroupAddRequest(@RequestBody Map<String,String> info);

    @PostMapping("/nettyApiClient/callUser4CommentResponse")
    public void callUser4CommentResponse(@RequestBody Map<String,String> info);
}
