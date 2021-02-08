package com.pjm.nacosservice.controller;

import com.pjm.common.aop.cache.EnableCache;
import com.pjm.common.aop.log.StartLog;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.util.JedisUtil;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.hello.service.HelloService;
import com.pjm.msgstater.entity.Duanxin;
import com.pjm.msgstater.service.EmailService;
import com.pjm.msgstater.service.SmsService;
import com.pjm.rabbitmqapi.entity.MessageMq;
import com.pjm.rabbitmqapi.service.MqApiClient;
import com.pjm.userapi.entity.UserApi;
import com.pjm.userapi.service.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("nacos")
public class NacosController {

    @Resource
    private UserClient userClient;
    @Autowired
    private HelloService helloService;
    @Resource
    private SmsService smsService;
    @Resource
    private EmailService emailService;
    @Resource
    private MqApiClient mqApiClient;
    @Autowired
    private JedisUtil jedisUtil;


    @StartLog
    @GetMapping("/service")
    public String service() {
        return "hello nacos-service";
    }

    //    @StartLog
    @GetMapping("/getUser")
    public UserApi getUser() {
        log.info("开始getUser");
        return userClient.getUser();
    }

    @StartLog
    @GetMapping("/hello")
    public String hello() {
        return helloService.hello();
    }

    @GetMapping("/sendsms")
    String sendsms() {
        Map<String, String> map = new HashMap<>();
        map.put("Position", "低级管理员");
        smsService.sendSms(new Duanxin().setTelNumber("18782217026").setTemplateCode("SMS_181201659").setContentMap(map));
        return "success";
    }

    @GetMapping("/sendemail")
    String sendemail() {
        emailService.sendqqemail("2522340502@qq.com", "hello world", "nmsl");
        return "success";
    }

    @GetMapping("/addQunue")
    public ResponseEntity<MessageMq<UserApi>> addQunue() {
        MessageMq<UserApi> messageMq = new MessageMq<>();
        messageMq.setId("nacos." + UuidUtil.next());
        messageMq.setQueueName("nacos.queue");
        messageMq.setExchangeName("pjm.topic2");
        messageMq.setMessageBody(new UserApi());
        return mqApiClient.add2Qunue(messageMq);
    }

    @GetMapping("/redisListPush")
    public String redisListPush(String item) {
        jedisUtil.rpush("pjm-key", item);
        return item;
    }

    @GetMapping("/redisListRange")
    public List<String> redisListRange() {
        return jedisUtil.popAll("pjm-key");
    }

    @GetMapping("/testCache1")
    @EnableCache(key = "testKey")
    public String testCache1() {
        return "hello world";
    }

    @GetMapping("/testCache2")
    @EnableCache(key = "testKey22")
    public String testCache2() {
        return "hello world22";
    }
}
