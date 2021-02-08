package com.pjm.rabbitmqservice.config;

import com.pjm.common.util.JedisUtil;
import com.pjm.rabbitmqservice.lisnter.UserLinster;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMqConfig {
    //交换机名称
    public static final String ITEM_TOPIC_EXCHANGE = "pjm.topic2";
    //队列名称
    public static final String ITEM_QUEUE = "nacos.queue";

    //声明交换机
    @Bean("itemTopicExchange")
    public Exchange topicExchange(){
        return ExchangeBuilder.topicExchange(ITEM_TOPIC_EXCHANGE).durable(true).build();
    }

    //声明队列
    @Bean("itemQueue")
    public Queue itemQueue(){
        return QueueBuilder.durable(ITEM_QUEUE).build();
    }
    //声明队列
    @Bean("itemQueue2")
    public Queue itemQueue2(){
        return QueueBuilder.durable("user.queue").build();
    }
    //声明队列
    @Bean("gateWayUpdateQueue")
    public Queue gateWayUpdateQueue(){
        return QueueBuilder.durable("gateway-update").build();
    }
    //声明队列
    @Bean("gateWayAddQueue")
    public Queue gateWayAddQueue(){
        return QueueBuilder.durable("gateway-add").build();
    }
    //声明队列
    @Bean("gateWayDeleteQueue")
    public Queue gateWayDeleteQueue(){
        return QueueBuilder.durable("gateway-delete").build();
    }
    //绑定队列和交换机
    @Bean
    public Binding itemQueueExchange(@Qualifier("itemQueue") Queue queue,
                                     @Qualifier("itemTopicExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("nacos.#").noargs();
    }

    //绑定队列和交换机
    @Bean
    public Binding itemQueueExchange2(@Qualifier("itemQueue2") Queue queue,
                                      @Qualifier("itemTopicExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("user.#").noargs();
    }
    //绑定队列和交换机
    @Bean
    public Binding gateWayUpdateExchange(@Qualifier("gateWayUpdateQueue") Queue queue,
                                      @Qualifier("itemTopicExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("gateway-update.#").noargs();
    }
    //绑定队列和交换机
    @Bean
    public Binding gateWayAddExchange(@Qualifier("gateWayAddQueue") Queue queue,
                                   @Qualifier("itemTopicExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("gateway-add.#").noargs();
    }
    //绑定队列和交换机
    @Bean
    public Binding gateWayDeleteExchange(@Qualifier("gateWayDeleteQueue") Queue queue,
                                   @Qualifier("itemTopicExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("gateway-delete.#").noargs();
    }

}