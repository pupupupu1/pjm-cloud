package com.pjm.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PjmException extends RuntimeException {
    private Integer code;
    private String msg;
    public PjmException(Integer code, String msg){
        this.code=code;
        this.msg=msg;
    }

    public PjmException() {
        super();
    }
}
