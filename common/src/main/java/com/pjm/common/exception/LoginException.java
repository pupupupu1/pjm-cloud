package com.pjm.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LoginException extends RuntimeException {
    private Integer code;
    private String msg;
    public LoginException(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public LoginException() {
        super();
    }
}
