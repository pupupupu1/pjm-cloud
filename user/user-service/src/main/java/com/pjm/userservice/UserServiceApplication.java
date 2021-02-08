package com.pjm.userservice;

import com.pjm.common.util.JedisUtil;
import com.pjm.userservice.entity.User;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
@EnableFeignClients(basePackages = "com.pjm.*")
@MapperScan({"com.pjm.*.mapper"})
@ComponentScan("com.pjm")
public class UserServiceApplication {
    @Autowired
    private JedisUtil jedisUtil;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @GetMapping("service")
    public String service(){
        return "hello user-service";
    }
    @GetMapping("getUser")
    public User getUser(){
        return new User().setUserName("pjm").setUserPassword("ASDasdasdasd");
    }
}
