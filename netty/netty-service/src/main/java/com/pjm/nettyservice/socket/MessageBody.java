package com.pjm.nettyservice.socket;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MessageBody {
    private Header header;
    private String name;
    private String action;
    private Long time;
    private String extendBody;
}
