package com.pjm.nacosservice.controller;

import com.alibaba.fastjson.JSON;
import com.pjm.common.aop.cache.EnableCache;
import com.pjm.common.aop.cache.RefreshCache;
import com.pjm.common.aop.log.StartLog;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.service.AsyncService;
import com.pjm.common.util.JedisUtil;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.hello.service.HelloService;
import com.pjm.msgstater.entity.Duanxin;
import com.pjm.msgstater.service.EmailService;
import com.pjm.msgstater.service.SmsService;
import com.pjm.nacosservice.entity.WhiteListFilter;
import com.pjm.rabbitmqapi.entity.MessageMq;
import com.pjm.rabbitmqapi.entity.MqLocalMessage;
import com.pjm.rabbitmqapi.service.MqApiClient;
import com.pjm.userapi.entity.UserApi;
import com.pjm.userapi.service.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

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

    @Autowired
    private AsyncService asyncService;


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
    @EnableCache(key = "$P0+$P1")
    public String testCache2(String name, String age) {
        return name + age;
    }

    @GetMapping("/testCache4")
    @EnableCache(key = "asfasdasd")
    public Set<String> testCache4() {
        return new HashSet<>();
    }


    @PostMapping("/testCache3")
    @EnableCache(key = "$P0:id+$P0:filterCode")
    public String testCache3(@RequestBody WhiteListFilter whiteListFilter) {
        return "name:" + whiteListFilter.getId();
    }

    @PostMapping("sendMqMsg2DB")
    public String sendMqMsg2DB(String name) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        mqApiClient.insertMsg(new MqLocalMessage().setId("user." + UuidUtil.next()).setTopicName("pjm.topic2")
                .setQueueName("user.queue").setContext(JSON.toJSONString(map)));
        return "success";
    }

    @GetMapping("async")
    public String async() throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            int finalI = i;
            asyncService.asyncInvoke(() -> {
                Thread.sleep(1000);
                log.info("异步打印：{}", finalI);
            });
        }
        return "success";
    }

    @GetMapping("refreshCache")
    @RefreshCache(key = "WhiteListFilter", sleepTime = 5000)
    public String refreshCache() {
        jedisUtil.keysS("WhiteListFilter");
        return "success";
    }
}
