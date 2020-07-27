package com.zyf.common.model.enums;

import cn.hutool.core.util.StrUtil;

/**
 * 订单类型
 * @author yuanfeng.z
 * @date 2019/6/29 10:30
 */
public enum Type {

    /**
     * 市价
     */
    MARKET("MARKET"),

    /**
     * 限价
     */
    LIMIT("LIMIT"),

    LIQUIDATION("LIQUIDATION"),

    STOP_MARKET("STOP_MARKET");

    private String value;

    Type(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Type getByValue(String type) {
        if (StrUtil.isBlank(type)) {
            return null;
        }
        else if(MARKET.value.equals(type)){
            return MARKET;
        }
        else if(LIMIT.value.equals(type)){
            return LIMIT;
        }
        else if(LIQUIDATION.value.equals(type)){
            return LIQUIDATION;
        }
        return null;
    }

}
