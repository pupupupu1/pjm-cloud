package com.pjm.rabbitmqapi.service;

import com.pjm.common.entity.ResponseEntity;
import com.pjm.rabbitmqapi.entity.MessageMq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pjm-service-rabbitmq")
public interface MqApiClient {
    @PostMapping("/mqApiClient/add")
    public <T> ResponseEntity<MessageMq<T>> add2Qunue(@RequestBody MessageMq<T> messageMq);
}
