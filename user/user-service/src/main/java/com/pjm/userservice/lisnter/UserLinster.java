package com.pjm.userservice.lisnter;

import com.pjm.common.util.JedisUtil;
import com.pjm.rabbitmqapi.entity.MqLocalMessage;
import com.pjm.rabbitmqapi.service.MqApiClient;
import com.pjm.userapi.service.UserClient;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
@Slf4j
public class UserLinster {
    @Resource
    private UserClient userClient;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private JedisUtil jedisUtil;
    @Resource
    private MqApiClient mqApiClient;

    @RabbitListener(queues = "nacos.queue")
    public void pjmQueueListener(Object msg) {
        try {
//            Class c=Class.forName("com.pjm.userapi.service.UserClient");
//            System.out.println(c.getMethod("getUser").invoke(userClient));
            Object o = context.getBean("com.pjm.userapi.service.UserClient");
            System.out.println(o.getClass().getMethod("getUser").invoke(o));
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("nacos被监听到了！{}", msg);
    }

    @RabbitListener(queues = "user.queue")
    public void pjmQueueListener1(Channel channel, Message msg) throws ClassNotFoundException, NoSuchMethodException, IOException {
        log.info("getUser被监听到了！{}", msg);
        try {
//            throw new Exception("test exception");
            Object o = context.getBean("com.pjm.userapi.service.UserClient");
            log.info("打印userclient返回值{}", o.getClass().getMethod("getUser").invoke(o));
            //告诉服务器收到这条消息 已经被我消费了 可以在队列删掉 这样以后就不会再发了 否则消息服务器以为这条消息没处理掉 后续还会在发
            log.info("打印msg.getMessageProperties().getDeliveryTag()：{}", msg.getMessageProperties().getDeliveryTag());
//            System.out.println(1 / 0);
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
            mqApiClient.updateLocalMsg(new MqLocalMessage().setId(msg.getMessageProperties().getReceivedRoutingKey()).setMsgStatus("3"));

        } catch (Exception e) {
            e.printStackTrace();
            if (msg.getMessageProperties().getRedelivered()) {
                log.info("消息已重复处理失败,拒绝再次接收");
                // 拒绝消息，requeue=false 表示不再重新入队，如果配置了死信队列则进入死信队列
                channel.basicReject(msg.getMessageProperties().getDeliveryTag(), false);
                mqApiClient.updateLocalMsg(new MqLocalMessage().setId(msg.getMessageProperties().getReceivedRoutingKey()).setMsgStatus("2"));
            } else {
                log.info("消息即将再次返回队列处理");
                // requeue为是否重新回到队列，true重新入队
                channel.basicNack(msg.getMessageProperties().getDeliveryTag(), false, true);
            }
//            e.printStackTrace();

        }

    }

}
