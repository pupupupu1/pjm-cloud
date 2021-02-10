package com.pjm.rabbitmqservice.controller;


import com.pjm.rabbitmqservice.service.IMqLocalMessageService;
import com.pjm.userapi.entity.UserApi;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pjm
 * @since 2021-02-10
 */
@Api(tags = {"消息总线控制器"})
@RestController
@RequestMapping("/mqLocalMessage")
public class MqLocalMessageController {
    @Autowired
    private IMqLocalMessageService mqLocalMessageService;

    @PostMapping("getUser")
    public UserApi getUser() {
        return new UserApi();
    }
}

