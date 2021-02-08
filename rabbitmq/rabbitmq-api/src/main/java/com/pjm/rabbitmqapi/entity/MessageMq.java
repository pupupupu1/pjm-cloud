package com.pjm.rabbitmqapi.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class MessageMq<T> implements Serializable {
    private String id;
    private String exchangeName;
    private String queueName;
    private T messageBody;
}
