package com.zyf.baseservice.model.enums;

/**
 * 缓存名称枚举
 * @author yuanfeng.z
 * @date 2019/7/11 11:53
 */
public enum CacheNameEnum {
    TICKER("ticker"),
    DEPTH("depth"),
    KLINE("kline");

    private String value;

    CacheNameEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
