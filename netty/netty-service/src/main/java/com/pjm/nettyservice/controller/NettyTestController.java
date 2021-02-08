package com.pjm.nettyservice.controller;

import com.pjm.nettyservice.socket.PjmSocketNewHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class NettyTestController {
    @PostMapping("test1")
    public Object test1() {
        return PjmSocketNewHandler.USER_MAP;
    }
}
