package com.pjm.rabbitmqservice.controller;

import com.pjm.userapi.entity.UserApi;
import com.pjm.userapi.service.UserClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/rabbitmq")
public class TestController {
    @Resource
    private UserClient userClient;

    @PostMapping("getUser")
    public UserApi getUser(){
        return userClient.getUser();
    }
}
