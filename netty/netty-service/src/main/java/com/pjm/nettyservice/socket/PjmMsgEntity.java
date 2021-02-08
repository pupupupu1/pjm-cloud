package com.pjm.nettyservice.socket;

import lombok.Data;

import java.util.Map;


@Data
public class PjmMsgEntity {
    private String id;
    private String sourceAccount;
    private Object msgBody;
    private String receiveAccount;
    private String action;
    private Map<String,String> header;
}
