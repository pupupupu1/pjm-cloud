package com.pjm.common.exception.handler;

import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.exception.CustomException;
import com.pjm.common.exception.LoginException;
import com.pjm.common.exception.PjmException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandle {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exception(Exception e) {
        e.printStackTrace();
        return ResponseEntity.failed(e.getMessage());
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<String> loginException(LoginException e){
        return new ResponseEntity<>(e.getCode(),e.getMsg());
    }
    @ExceptionHandler(PjmException.class)
    public ResponseEntity<String> pjmException(PjmException e){
        return new ResponseEntity<>(e.getCode(),e.getMsg());
    }
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> customException(CustomException e){
        return new ResponseEntity<>(403,e.getMessage());
    }
}
