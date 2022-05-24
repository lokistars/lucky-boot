package com.lucky.platform.config.web;

import com.lucky.platform.config.web.ResultModel;

/**
 * 定义状态码
 * @author Nuany
 */

public enum ResultCode {
    // 成功返回
    SUCCESS("success"),
    ERROR("error"),
    COMMON_REQUEST_000("操作成功"),
    COMMON_REQUEST_001("操作失败"),
    COMMON_REQUEST_050("请求异常"),
    COMMON_REQUEST_051("非法请求"),
    COMMON_REQUEST_060("超出最大上传大小限制"),
    COMMON_PARAM_100("请求失败，参数验证失败"),
    COMMON_PARAM_101("请求失败，参数解析失败"),
    COMMON_PARAM_102("请求失败，参数不能为空"),
    COMMON_PARAM_103("请求失败，缺少必要的参数"),
    USER_LOGIN_150("未登录"),
    USER_LOGIN_151("登录已失效，请重新登录"),
    USER_LOGIN_152("登录已失效，已在其他设备登录"),
    USER_LOGIN_153("登录已失效，已被踢线"),
    USER_LOGIN_160("无权访问");

    private String name;
    private Integer code;

    private ResultCode(String defMsg) {
        this.name = defMsg;
    }

    private ResultCode(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    public <T> ResultModel<T> toResult() {
        return new ResultModel(this.code, this.getName(), null);
    }

    public String getName() {
        return name;
    }

    public void setName(String key) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
