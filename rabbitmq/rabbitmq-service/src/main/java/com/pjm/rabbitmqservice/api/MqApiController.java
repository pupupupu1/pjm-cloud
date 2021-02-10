package com.pjm.rabbitmqservice.api;

import com.pjm.common.entity.ResponseEntity;
import com.pjm.rabbitmqservice.entity.MessageMq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/mqApiClient")
public class MqApiController implements ReturnCallback, ConfirmCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("add")
    public <T> ResponseEntity<MessageMq<T>> add(@RequestBody MessageMq<T> messageMq) {
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
        CorrelationData correlationData = new CorrelationData(messageMq.getId());//用于确认之后更改本地消息状态或删除--本地消息id
        rabbitTemplate.convertAndSend(messageMq.getExchangeName(), messageMq.getId(), messageMq.getMessageBody(),correlationData);
        return ResponseEntity.success(messageMq);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("处理back消息");
        String localMessageId = correlationData.getId();
        if (ack) {// 消息发送成功,更新本地消息为已成功发送状态或者直接删除该本地消息记录,剩余的由MQ投递到消费者端，消费者端需要进行幂等，避免产生脏数据
            log.info("生产者投递成功:{}",cause);
        } else {
            //失败处理
            log.info("生产者投递失败:{}",cause);
        }
    }


    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("return--message:" + new String(message.getBody()) + ",replyCode:" + replyCode
                + ",replyText:" + replyText + ",exchange:" + exchange + ",routingKey:" + routingKey);
        log.info("发送失败信息");
    }
}
