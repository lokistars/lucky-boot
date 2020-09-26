package com.lucky.platform.enumtype;

/**
 * 定义状态码
 * @author Nuany
 */

public enum ResultCode {
    // 成功返回
    SUCCESS("success", 200),
    ERROR("error",501);

    private String key;
    private Integer value;

    ResultCode(String key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
