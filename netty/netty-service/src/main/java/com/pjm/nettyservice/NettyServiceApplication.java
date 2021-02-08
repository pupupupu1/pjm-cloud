package com.pjm.nettyservice;

import com.pjm.nettyservice.socket.ServerMain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.pjm.*")
@ComponentScan("com.pjm")
@RefreshScope
@Slf4j
public class NettyServiceApplication {
    private static ServerMain serverMain;
    @Autowired
    private void setServerMain(ServerMain serverMain) {
        NettyServiceApplication.serverMain = serverMain;
    }

    public static void main(String[] args) {
        SpringApplication.run(NettyServiceApplication.class, args);
        log.info("准备加载通信服务器");
        serverMain.startImServer();
    }
}
