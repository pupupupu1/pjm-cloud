package com.pjm.common.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ResponseEntity<T> implements Serializable {
    private int code = 0;
    private String msg;
    private T data;

    public ResponseEntity(int code, String msg) {
        if (!StringUtils.isEmpty(code)) {
            this.code = code;
        }
        if (!StringUtils.isEmpty(msg)) {
            this.msg = msg;
        }
    }

    public ResponseEntity(String msg) {
        if (!StringUtils.isEmpty(msg)) {
            this.msg = msg;
        }
    }

    public ResponseEntity(T data) {
        if (!StringUtils.isEmpty(data)) {
            this.data = data;
        }
    }

    public ResponseEntity(int code, String msg, T data) {
        this.code = code;
        this.msg  = msg;
        this.data = data;
    }

    /**
     * 成功返回结果
     * @param data 获取的数据
     * @param <T> 获取的数据类型
     * @return 返回结果
     */
    public static <T> ResponseEntity<T> success(T data) {
        return new ResponseEntity<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回结果
     * @param data 获取的数据
     * @param mesage 提示信息
     * @param <T> 获取的数据类型
     * @return
     */
    public static <T> ResponseEntity<T> success(T data, String mesage) {
        return new ResponseEntity<T>(ResultCode.SUCCESS.getCode(), mesage, data);
    }

    /**
     * 失败返回结果
     * @param errorCode 错误码
     * @param <T> 泛型
     * @return 失败返回
     */
    public static <T> ResponseEntity<T> failed(IErrorCode errorCode) {
        return new ResponseEntity<T>(ResultCode.FAILED.getCode(), ResultCode.FAILED.getMessage(), null);
    }

    /**
     * 失败返回结果
     * @param message 返回消息
     * @param <T> 泛型
     * @return 失败结果
     */
    public static <T> ResponseEntity<T> failed(String message) {
        return new ResponseEntity<T>(ResultCode.FAILED.getCode(), message, null);
    }

    /**
     * 失败返回结果
     * @param <T> 泛型
     * @return 失败结果
     */
    public static <T> ResponseEntity<T> failed() {
        return failed(ResultCode.FAILED);
    }
}
