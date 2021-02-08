package com.pjm.circleoffriendservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.pjm.*")
@MapperScan({"com.pjm.*.mapper"})
@ComponentScan("com.pjm")
@RefreshScope
public class CircleoffriendServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CircleoffriendServiceApplication.class, args);
    }

}
