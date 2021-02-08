package com.pjm.nettyservice.socket;

import lombok.Data;

@Data
public class chatRecord4Option {
    private String optionId;
    private String optionName;
    private boolean isMe;
    private String messageBody;
    private long sendTime;
}
