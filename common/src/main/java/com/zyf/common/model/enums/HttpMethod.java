package com.zyf.common.model.enums;

/**
 * Http请求方法
 * @author yuanfeng.z
 * @date 2019/6/29 12:14
 */
public enum HttpMethod {

    GET("GET"),

    PUT("PUT"),

    POST("POST"),

    DELETE("DELETE");

    private String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
