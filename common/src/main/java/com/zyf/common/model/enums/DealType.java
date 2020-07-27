package com.zyf.common.model.enums;

import cn.hutool.core.util.StrUtil;

/**
 * 合约类型
 *
 * @author yuanfeng.z
 * @date 2019-12-23
 */
public enum DealType {

    /**
     * 开仓
     */
    OPEN_POSITION("OPEN_POSITION"),

    /**
     * 平仓
     */
    CLOSE_POSITION("CLOSE_POSITION"),

    /**
     * 爆仓
     */
    BREAK_POSITION("BREAK_POSITION"),

    /**
     * 加仓
     */
    ADD_POSITION("ADD_POSITION"),

    /**
     * 减仓
     */
    SUB_POSITION("SUB_POSITION");

    private String value;

    DealType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DealType getByValue(String type) {
        if (StrUtil.isBlank(type)) {
            return null;
        }
        else if(OPEN_POSITION.value.equals(type)){
            return OPEN_POSITION;
        }
        else if(CLOSE_POSITION.value.equals(type)){
            return CLOSE_POSITION;
        }
        else if(BREAK_POSITION.value.equals(type)){
            return BREAK_POSITION;
        }
        else if(SUB_POSITION.value.equals(type)){
            return SUB_POSITION;
        }
        return null;
    }
}
