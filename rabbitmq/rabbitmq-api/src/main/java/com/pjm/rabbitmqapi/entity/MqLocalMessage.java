package com.pjm.rabbitmqapi.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author pjm
 * @since 2021-02-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MqLocalMessage {

    private static final long serialVersionUID = 1L;

    private String id;
    private String topicName;
    private String queueName;

    private String context;

    private String msgStatus;

    private Integer sendTimes;

    private Long updateTime;

}