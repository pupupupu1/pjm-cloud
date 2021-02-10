package com.pjm.quartzservice;

import com.pjm.rabbitmqapi.service.MqApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.pjm.*")
@ComponentScan("com.pjm")
@RefreshScope
public class QuartzServiceApplication {
    @Resource
    MqApiClient mqApiClient;

    public static void main(String[] args) {
        SpringApplication.run(QuartzServiceApplication.class, args);
        System.out.println("\u2611");
    }


}
