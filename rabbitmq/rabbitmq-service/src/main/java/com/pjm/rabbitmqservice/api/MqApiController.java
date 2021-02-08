package com.pjm.rabbitmqservice.api;

import com.pjm.common.entity.ResponseEntity;
import com.pjm.rabbitmqservice.entity.MessageMq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/mqApiClient")
public class MqApiController implements RabbitTemplate.ReturnCallback, RabbitTemplate.ConfirmCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("add")
    public <T> ResponseEntity<MessageMq<T>> add(@RequestBody MessageMq<T> messageMq) throws ClassNotFoundException, NoSuchMethodException {
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
        rabbitTemplate.convertAndSend(messageMq.getExchangeName(), messageMq.getId(), messageMq.getMessageBody());
        return ResponseEntity.success(messageMq);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {

    }

    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {

    }
}
