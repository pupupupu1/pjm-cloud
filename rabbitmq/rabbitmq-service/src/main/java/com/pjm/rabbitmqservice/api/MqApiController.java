package com.pjm.rabbitmqservice.api;

import com.alibaba.fastjson.JSON;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.service.AsyncService;
import com.pjm.msgstater.entity.Duanxin;
import com.pjm.msgstater.service.EmailService;
import com.pjm.msgstater.service.SmsService;
import com.pjm.rabbitmqservice.entity.MessageMq;
import com.pjm.rabbitmqservice.entity.MqLocalMessage;
import com.pjm.rabbitmqservice.service.IMqLocalMessageService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/mqApiClient")
public class MqApiController implements ReturnCallback, ConfirmCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private IMqLocalMessageService mqLocalMessageService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AsyncService asyncService;


    @PostMapping("add")
    public <T> ResponseEntity<MessageMq<T>> add(@RequestBody MessageMq<T> messageMq) {
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
        CorrelationData correlationData = new CorrelationData(messageMq.getId());//用于确认之后更改本地消息状态或删除--本地消息id
        rabbitTemplate.convertAndSend(messageMq.getExchangeName(), messageMq.getId(), messageMq.getMessageBody(), correlationData);
        return ResponseEntity.success(messageMq);
    }

    @PostMapping("insertMsg")
    public void insertMsg(@RequestBody MqLocalMessage mqLocalMessage) {
        mqLocalMessage.setUpdateTime(System.currentTimeMillis());
        mqLocalMessageService.insert(mqLocalMessage);
    }

    @PostMapping("updateLocalMsg")
    public void updateLocalMsg(@RequestBody MqLocalMessage mqLocalMessage) {
        String id = mqLocalMessage.getId();
        String status = mqLocalMessage.getMsgStatus();
        Long time = System.currentTimeMillis();
        MqLocalMessage updateDto = new MqLocalMessage();
        updateDto.setId(id).setMsgStatus(status).setUpdateTime(time);
        mqLocalMessageService.updateById(updateDto);
    }

    @PostMapping("findEnableMsg2Send")
    public void findEnableMsg2Send() {
        List<MqLocalMessage> mqLocalMessages = mqLocalMessageService.findNotSendMsg();
        mqLocalMessages.forEach(msg -> {
            rabbitTemplate.setMandatory(true);
            rabbitTemplate.setConfirmCallback(this);
            rabbitTemplate.setReturnCallback(this);
            CorrelationData correlationData = new CorrelationData(msg.getId());//用于确认之后更改本地消息状态或删除--本地消息id
            rabbitTemplate.convertAndSend(msg.getTopicName(), msg.getId(), msg.getContext(), correlationData);
//            log.info("打印返回值！！！！！！！：{}", res);
        });
    }

    @PostMapping("sendMsg4ErrorJob")
    public void sendMsg4ErrorJob() {
        List<MqLocalMessage> mqLocalMessages = mqLocalMessageService.findCusmuerErrorMsg();
        mqLocalMessages.forEach(msg -> {
            try {
                asyncService.asyncInvoke(() -> {
                    emailService.sendqqemail("2522340502@qq.com", "异步事件出现异常，请登录管理平台检查，数据是：" + JSON.toJSONString(msg), "异步任务异常反馈");
                    Map<String, String> map = new HashMap<>();
                    map.put("msgId", msg.getId().substring(1, 6));
                    map.put("content", msg.getContext());
                    smsService.sendSms(new Duanxin().setTemplateCode("SMS_211493499").setContentMap(map).setTelNumber("18782217026"));
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("处理back消息");
        String localMessageId = correlationData.getId();
        if (ack) {// 消息发送成功,更新本地消息为已成功发送状态或者直接删除该本地消息记录,剩余的由MQ投递到消费者端，消费者端需要进行幂等，避免产生脏数据
            log.info("生产者投递成功,修改数据库消息表:{}", cause);
            MqLocalMessage updateDto = new MqLocalMessage();
            updateDto.setId(localMessageId);
            updateDto.setMsgStatus("1");
            mqLocalMessageService.updateById(updateDto);
        } else {
            //失败处理
            log.info("生产者投递失败:{}", cause);
        }
    }


    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("发送失败");
        log.info("return--message:" + new String(message.getBody()) + ",replyCode:" + replyCode
                + ",replyText:" + replyText + ",exchange:" + exchange + ",routingKey:" + routingKey);
    }
}
