package com.pjm.rabbitmqservice;

//import org.mybatis.spring.annotation.MapperScan;
import com.pjm.common.util.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.pjm.*")
//@MapperScan({"com.pjm.*.mapper"})
@ComponentScan("com.pjm")
@EnableAsync
public class RabbitmqServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqServiceApplication.class, args);
    }

}
