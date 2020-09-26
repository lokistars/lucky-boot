package com.lucky.platform.util;

/**
 * 统一返回
 * @author Nuany
 */
public class ResponseResult<T> {
    /**
     * 返回状态码
     */
    private Integer code;
    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 提示消息
     */
    private String message;
    /**
     * 返回数据
     */
    private T data;

    public ResponseResult() {
    }

    public static <T> ResponseResult<T> ok(T result) {
        return new ResponseResult(0, true,"common.request.success", result);
    }

    public static <T> ResponseResult<T> fail(int code, Boolean success, String message, Object... args) {
        return new ResponseResult(code, success, message, args);
    }


    public ResponseResult(Integer code, Boolean success, String message, T data) {
        this.code = code;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
