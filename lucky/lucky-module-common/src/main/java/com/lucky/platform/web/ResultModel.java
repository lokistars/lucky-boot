package com.lucky.platform.web;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class ResultModel<T> implements Serializable {

    private int code;

    private String message;

    private T result;

    public ResultModel() {
    }

    public ResultModel(int code, String message) {
        this(code, message, null);
    }

    public ResultModel(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public static <T> ResultModel<T> success(T result) {
        return new ResultModel(0, "OK", result);
    }

    public static <T> ResultModel<T> success() {
        return success(null);
    }

    public static <T> ResultModel<T> failure(int code, String message) {
        return new ResultModel(code, message, (Object)null);
    }

    public static <T> ResultModel<T> failure(String message) {
        return failure(-1, message);
    }

    public static <T> ResultModel<T> failMessage(String message) {
        ResultModel<T> resultModel = new ResultModel();
        resultModel.setCode(-1);
        resultModel.setMessage(message);
        return resultModel;
    }


    public boolean isSuccess() {
        return this.code == 0;
    }


    public ResultModel<T> message(String message) {
        this.setMessage(message);
        return this;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public T getResult() {
        return this.result;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setResult(final T result) {
        this.result = result;
    }

}
