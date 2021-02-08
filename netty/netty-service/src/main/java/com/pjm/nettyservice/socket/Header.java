package com.pjm.nettyservice.socket;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Header {
    private Integer length;
}
