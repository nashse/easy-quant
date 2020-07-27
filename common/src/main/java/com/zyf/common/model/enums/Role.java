package com.zyf.common.model.enums;

import cn.hutool.core.util.StrUtil;

/**
 * 合约角色
 *
 * @author god
 * @date 2019-12-23
 */
public enum  Role {

    /**
     * taker
     */
    TAKER("TAKER"),

    /**
     * maker
     */
    MAKER("MAKER");


    private String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Role getByValue(String type) {
        if (StrUtil.isBlank(type)) {
            return null;
        }
        else if(TAKER.value.equals(type)){
            return TAKER;
        }
        else if(MAKER.value.equals(type)){
            return MAKER;
        }
        return null;
    }
}
